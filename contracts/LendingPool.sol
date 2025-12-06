// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/security/ReentrancyGuard.sol";

/**
 * @title LendingPool
 * @dev Core lending protocol contract for Qubic Finance
 * Supports collateralized loans with multiple collateral types
 */
contract LendingPool is Ownable, ReentrancyGuard {
    
    // Loan structure
    struct Loan {
        uint256 loanId;
        address borrower;
        address collateralToken;
        uint256 collateralAmount;
        uint256 loanAmount;
        uint256 paidAmount;
        uint256 interestRate; // basis points (100 = 1%)
        uint256 duration; // in days
        uint256 startDate;
        uint256 dueDate;
        LoanStatus status;
        LoanType loanType;
    }
    
    enum LoanStatus {
        PENDING,
        APPROVED,
        REJECTED,
        ACTIVE,
        COMPLETED,
        DEFAULTED,
        LIQUIDATED
    }
    
    enum LoanType {
        EDUCATION,
        PERSONAL,
        HOME,
        CAR,
        COLLATERALIZED
    }
    
    // State variables
    uint256 public nextLoanId = 1;
    mapping(uint256 => Loan) public loans;
    mapping(address => uint256[]) public userLoans;
    mapping(address => bool) public admins;
    
    // Collateral configuration
    mapping(address => bool) public supportedCollateral;
    mapping(address => uint256) public collateralLTV; // Loan-to-Value ratio (basis points)
    
    // Protocol parameters
    uint256 public constant MIN_COLLATERAL_RATIO = 15000; // 150%
    uint256 public constant LIQUIDATION_THRESHOLD = 13000; // 130%
    uint256 public constant BASIS_POINTS = 10000;
    
    // Treasury
    address public treasury;
    IERC20 public qfinToken;
    
    // Events (Enhanced for EasyConnect Integration)
    event LoanCreated(uint256 indexed loanId, address indexed borrower, uint256 amount);
    event LoanApproved(uint256 indexed loanId, address indexed admin);
    event LoanRejected(uint256 indexed loanId, address indexed admin);
    event PaymentMade(uint256 indexed loanId, address indexed borrower, uint256 amount);
    event LoanCompleted(uint256 indexed loanId);
    event LoanLiquidated(uint256 indexed loanId, address indexed liquidator);
    event CollateralAdded(address indexed token, uint256 ltv);
    
    // EasyConnect Enhanced Events - Rich data for automation
    event LoanCreatedDetailed(
        uint256 indexed loanId,
        address indexed borrower,
        address collateralToken,
        uint256 collateralAmount,
        uint256 loanAmount,
        uint256 interestRate,
        uint256 duration,
        LoanType loanType,
        uint256 timestamp
    );
    
    event PaymentMadeDetailed(
        uint256 indexed loanId,
        address indexed borrower,
        uint256 paymentAmount,
        uint256 remainingAmount,
        uint256 totalPaid,
        uint256 timestamp
    );
    
    event HealthFactorAlert(
        uint256 indexed loanId,
        address indexed borrower,
        uint256 healthFactor,
        uint256 collateralValue,
        uint256 debtValue,
        bool criticalRisk,
        uint256 timestamp
    );
    
    event MilestoneReached(
        address indexed user,
        string milestoneType,
        uint256 value,
        uint256 timestamp
    );
    
    event WhaleLoanActivity(
        uint256 indexed loanId,
        address indexed borrower,
        uint256 amount,
        string activityType,
        uint256 timestamp
    );
    
    constructor(address initialOwner, address _qfinToken, address _treasury) Ownable(initialOwner) {
        qfinToken = IERC20(_qfinToken);
        treasury = _treasury;
        admins[initialOwner] = true;
    }
    
    modifier onlyAdmin() {
        require(admins[msg.sender], "Not an admin");
        _;
    }
    
    /**
     * @dev Create a new loan request
     */
    function createLoan(
        address collateralToken,
        uint256 collateralAmount,
        uint256 loanAmount,
        uint256 duration,
        LoanType loanType
    ) external nonReentrant returns (uint256) {
        require(supportedCollateral[collateralToken], "Collateral not supported");
        require(collateralAmount > 0, "Invalid collateral amount");
        require(loanAmount > 0, "Invalid loan amount");
        require(duration > 0, "Invalid duration");
        
        // Check collateralization ratio
        uint256 requiredCollateral = (loanAmount * MIN_COLLATERAL_RATIO) / BASIS_POINTS;
        require(collateralAmount >= requiredCollateral, "Insufficient collateral");
        
        // Transfer collateral to contract
        IERC20(collateralToken).transferFrom(msg.sender, address(this), collateralAmount);
        
        // Calculate interest rate based on loan type and duration
        uint256 interestRate = calculateInterestRate(loanType, duration);
        
        // Create loan
        uint256 loanId = nextLoanId++;
        loans[loanId] = Loan({
            loanId: loanId,
            borrower: msg.sender,
            collateralToken: collateralToken,
            collateralAmount: collateralAmount,
            loanAmount: loanAmount,
            paidAmount: 0,
            interestRate: interestRate,
            duration: duration,
            startDate: block.timestamp,
            dueDate: 0,
            status: LoanStatus.PENDING,
            loanType: loanType
        });
        
        userLoans[msg.sender].push(loanId);
        
        emit LoanCreated(loanId, msg.sender, loanAmount);
        
        // EasyConnect Enhanced Event
        emit LoanCreatedDetailed(
            loanId,
            msg.sender,
            collateralToken,
            collateralAmount,
            loanAmount,
            interestRate,
            duration,
            loanType,
            block.timestamp
        );
        
        // Whale Activity Detection (>$50k equivalent)
        if (loanAmount > 50000 * 10**18) {
            emit WhaleLoanActivity(
                loanId,
                msg.sender,
                loanAmount,
                "LARGE_LOAN_CREATED",
                block.timestamp
            );
        }
        
        return loanId;
    }
    
    /**
     * @dev Approve loan (admin only)
     */
    function approveLoan(uint256 loanId) external onlyAdmin nonReentrant {
        Loan storage loan = loans[loanId];
        require(loan.status == LoanStatus.PENDING, "Invalid loan status");
        
        loan.status = LoanStatus.ACTIVE;
        loan.dueDate = block.timestamp + (loan.duration * 1 days);
        
        // Transfer loan amount to borrower
        payable(loan.borrower).transfer(loan.loanAmount);
        
        // Mint QFIN rewards to borrower (1% of loan amount)
        uint256 rewards = (loan.loanAmount * 100) / BASIS_POINTS;
        // Note: This would require minting permission
        
        emit LoanApproved(loanId, msg.sender);
    }
    
    /**
     * @dev Reject loan (admin only)
     */
    function rejectLoan(uint256 loanId) external onlyAdmin {
        Loan storage loan = loans[loanId];
        require(loan.status == LoanStatus.PENDING, "Invalid loan status");
        
        loan.status = LoanStatus.REJECTED;
        
        // Return collateral to borrower
        IERC20(loan.collateralToken).transfer(loan.borrower, loan.collateralAmount);
        
        emit LoanRejected(loanId, msg.sender);
    }
    
    /**
     * @dev Make a payment towards loan
     */
    function makePayment(uint256 loanId) external payable nonReentrant {
        Loan storage loan = loans[loanId];
        require(loan.status == LoanStatus.ACTIVE, "Loan not active");
        require(msg.value > 0, "Invalid payment amount");
        
        uint256 totalDue = calculateTotalDue(loanId);
        uint256 remainingDue = totalDue - loan.paidAmount;
        
        require(msg.value <= remainingDue, "Payment exceeds due amount");
        
        loan.paidAmount += msg.value;
        
        // Transfer payment to treasury
        payable(treasury).transfer(msg.value);
        
        emit PaymentMade(loanId, msg.sender, msg.value);
        
        // EasyConnect Enhanced Event
        emit PaymentMadeDetailed(
            loanId,
            msg.sender,
            msg.value,
            remainingDue - msg.value,
            loan.paidAmount,
            block.timestamp
        );
        
        // Check health factor and emit alert if needed
        uint256 collateralRatio = (loan.collateralAmount * BASIS_POINTS) / (remainingDue - msg.value);
        if (collateralRatio < 14000) { // Below 140%
            bool critical = collateralRatio < LIQUIDATION_THRESHOLD;
            emit HealthFactorAlert(
                loanId,
                msg.sender,
                collateralRatio,
                loan.collateralAmount,
                remainingDue - msg.value,
                critical,
                block.timestamp
            );
        }
        
        // Check if loan is completed
        if (loan.paidAmount >= totalDue) {
            loan.status = LoanStatus.COMPLETED;
            
            // Return collateral to borrower
            IERC20(loan.collateralToken).transfer(loan.borrower, loan.collateralAmount);
            
            emit LoanCompleted(loanId);
            
            // Milestone: First loan completed
            if (getUserLoans(msg.sender).length == 1) {
                emit MilestoneReached(
                    msg.sender,
                    "FIRST_LOAN_COMPLETED",
                    loan.loanAmount,
                    block.timestamp
                );
            }
        }
    }
    
    /**
     * @dev Liquidate undercollateralized loan
     */
    function liquidateLoan(uint256 loanId) external nonReentrant {
        Loan storage loan = loans[loanId];
        require(loan.status == LoanStatus.ACTIVE, "Loan not active");
        require(isLiquidatable(loanId), "Loan not liquidatable");
        
        loan.status = LoanStatus.LIQUIDATED;
        
        // Transfer collateral to liquidator (with bonus)
        uint256 liquidationBonus = (loan.collateralAmount * 500) / BASIS_POINTS; // 5% bonus
        IERC20(loan.collateralToken).transfer(msg.sender, loan.collateralAmount + liquidationBonus);
        
        emit LoanLiquidated(loanId, msg.sender);
    }
    
    /**
     * @dev Check if loan can be liquidated
     */
    function isLiquidatable(uint256 loanId) public view returns (bool) {
        Loan memory loan = loans[loanId];
        if (loan.status != LoanStatus.ACTIVE) return false;
        
        uint256 totalDue = calculateTotalDue(loanId);
        uint256 remainingDue = totalDue - loan.paidAmount;
        
        // Calculate current collateralization ratio
        uint256 collateralRatio = (loan.collateralAmount * BASIS_POINTS) / remainingDue;
        
        return collateralRatio < LIQUIDATION_THRESHOLD;
    }
    
    /**
     * @dev Calculate total amount due (principal + interest)
     */
    function calculateTotalDue(uint256 loanId) public view returns (uint256) {
        Loan memory loan = loans[loanId];
        uint256 interest = (loan.loanAmount * loan.interestRate) / BASIS_POINTS;
        return loan.loanAmount + interest;
    }
    
    /**
     * @dev Calculate interest rate based on loan type and duration
     */
    function calculateInterestRate(LoanType loanType, uint256 duration) internal pure returns (uint256) {
        // Base rates in basis points
        uint256 baseRate;
        
        if (loanType == LoanType.EDUCATION) {
            baseRate = 500; // 5%
        } else if (loanType == LoanType.PERSONAL) {
            baseRate = 800; // 8%
        } else if (loanType == LoanType.HOME) {
            baseRate = 600; // 6%
        } else if (loanType == LoanType.CAR) {
            baseRate = 700; // 7%
        } else {
            baseRate = 750; // 7.5% for collateralized
        }
        
        // Adjust for duration (longer = slightly higher rate)
        uint256 durationMultiplier = 100 + (duration / 30); // +1% per month
        return (baseRate * durationMultiplier) / 100;
    }
    
    /**
     * @dev Get all loans for a user
     */
    function getUserLoans(address user) external view returns (uint256[] memory) {
        return userLoans[user];
    }
    
    /**
     * @dev Get loan details
     */
    function getLoan(uint256 loanId) external view returns (Loan memory) {
        return loans[loanId];
    }
    
    /**
     * @dev Add supported collateral token (admin only)
     */
    function addCollateral(address token, uint256 ltv) external onlyAdmin {
        require(token != address(0), "Invalid token");
        require(ltv > 0 && ltv <= 9000, "Invalid LTV"); // Max 90%
        
        supportedCollateral[token] = true;
        collateralLTV[token] = ltv;
        
        emit CollateralAdded(token, ltv);
    }
    
    /**
     * @dev Add/remove admin
     */
    function setAdmin(address admin, bool status) external onlyOwner {
        admins[admin] = status;
    }
    
    /**
     * @dev Update treasury address
     */
    function setTreasury(address _treasury) external onlyOwner {
        treasury = _treasury;
    }
    
    /**
     * @dev Receive ETH
     */
    receive() external payable {}
}

