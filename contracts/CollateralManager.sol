// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

/**
 * @title CollateralManager
 * @dev Manages collateral valuation and liquidation for Qubic Finance
 */
contract CollateralManager is Ownable {
    
    // Price oracle (simplified - in production use Chainlink or similar)
    mapping(address => uint256) public tokenPrices; // Price in USD with 8 decimals
    mapping(address => uint256) public lastPriceUpdate;
    
    // Collateral configuration
    struct CollateralConfig {
        bool isSupported;
        uint256 ltvRatio; // Loan-to-Value ratio (basis points, 10000 = 100%)
        uint256 liquidationThreshold; // basis points
        uint256 liquidationBonus; // basis points
    }
    
    mapping(address => CollateralConfig) public collateralConfigs;
    
    // Oracle updaters (trusted addresses that can update prices)
    mapping(address => bool) public priceOracles;
    
    uint256 public constant BASIS_POINTS = 10000;
    uint256 public constant PRICE_DECIMALS = 8;
    
    event PriceUpdated(address indexed token, uint256 price, uint256 timestamp);
    event CollateralConfigured(address indexed token, uint256 ltv, uint256 liquidationThreshold);
    event OracleUpdated(address indexed oracle, bool status);
    
    constructor(address initialOwner) Ownable(initialOwner) {
        priceOracles[initialOwner] = true;
    }
    
    modifier onlyOracle() {
        require(priceOracles[msg.sender], "Not an oracle");
        _;
    }
    
    /**
     * @dev Update token price (oracle only)
     */
    function updatePrice(address token, uint256 priceUSD) external onlyOracle {
        require(token != address(0), "Invalid token");
        require(priceUSD > 0, "Invalid price");
        
        tokenPrices[token] = priceUSD;
        lastPriceUpdate[token] = block.timestamp;
        
        emit PriceUpdated(token, priceUSD, block.timestamp);
    }
    
    /**
     * @dev Batch update prices
     */
    function updatePrices(address[] calldata tokens, uint256[] calldata prices) external onlyOracle {
        require(tokens.length == prices.length, "Length mismatch");
        
        for (uint256 i = 0; i < tokens.length; i++) {
            tokenPrices[tokens[i]] = prices[i];
            lastPriceUpdate[tokens[i]] = block.timestamp;
            emit PriceUpdated(tokens[i], prices[i], block.timestamp);
        }
    }
    
    /**
     * @dev Configure collateral token
     */
    function configureCollateral(
        address token,
        bool isSupported,
        uint256 ltvRatio,
        uint256 liquidationThreshold,
        uint256 liquidationBonus
    ) external onlyOwner {
        require(token != address(0), "Invalid token");
        require(ltvRatio <= BASIS_POINTS, "Invalid LTV");
        require(liquidationThreshold <= BASIS_POINTS, "Invalid threshold");
        require(liquidationBonus <= 1000, "Bonus too high"); // Max 10%
        
        collateralConfigs[token] = CollateralConfig({
            isSupported: isSupported,
            ltvRatio: ltvRatio,
            liquidationThreshold: liquidationThreshold,
            liquidationBonus: liquidationBonus
        });
        
        emit CollateralConfigured(token, ltvRatio, liquidationThreshold);
    }
    
    /**
     * @dev Get collateral value in USD
     */
    function getCollateralValue(address token, uint256 amount) public view returns (uint256) {
        require(collateralConfigs[token].isSupported, "Collateral not supported");
        
        uint256 price = tokenPrices[token];
        require(price > 0, "Price not available");
        
        // Calculate value: (amount * price) / (10^token_decimals * 10^price_decimals)
        // Simplified calculation - assumes 18 decimals for tokens
        uint256 valueUSD = (amount * price) / (10**18);
        
        return valueUSD;
    }
    
    /**
     * @dev Get maximum loan amount for collateral
     */
    function getMaxLoanAmount(address token, uint256 collateralAmount) external view returns (uint256) {
        uint256 collateralValue = getCollateralValue(token, collateralAmount);
        uint256 ltv = collateralConfigs[token].ltvRatio;
        
        return (collateralValue * ltv) / BASIS_POINTS;
    }
    
    /**
     * @dev Check if position is liquidatable
     */
    function isLiquidatable(
        address collateralToken,
        uint256 collateralAmount,
        uint256 debtAmount
    ) external view returns (bool) {
        uint256 collateralValue = getCollateralValue(collateralToken, collateralAmount);
        uint256 threshold = collateralConfigs[collateralToken].liquidationThreshold;
        
        // Calculate health factor: (collateralValue * threshold) / debtAmount
        // If < BASIS_POINTS (100%), position is liquidatable
        uint256 healthFactor = (collateralValue * threshold) / debtAmount;
        
        return healthFactor < BASIS_POINTS;
    }
    
    /**
     * @dev Calculate health factor for a position
     */
    function calculateHealthFactor(
        address collateralToken,
        uint256 collateralAmount,
        uint256 debtAmount
    ) external view returns (uint256) {
        if (debtAmount == 0) return type(uint256).max;
        
        uint256 collateralValue = getCollateralValue(collateralToken, collateralAmount);
        uint256 threshold = collateralConfigs[collateralToken].liquidationThreshold;
        
        // Health factor with 4 decimal precision
        return (collateralValue * threshold * BASIS_POINTS) / (debtAmount * BASIS_POINTS);
    }
    
    /**
     * @dev Get liquidation bonus
     */
    function getLiquidationBonus(address token) external view returns (uint256) {
        return collateralConfigs[token].liquidationBonus;
    }
    
    /**
     * @dev Check if price is stale
     */
    function isPriceStale(address token, uint256 maxAge) external view returns (bool) {
        return block.timestamp - lastPriceUpdate[token] > maxAge;
    }
    
    /**
     * @dev Set oracle status
     */
    function setOracle(address oracle, bool status) external onlyOwner {
        priceOracles[oracle] = status;
        emit OracleUpdated(oracle, status);
    }
    
    /**
     * @dev Get all collateral info
     */
    function getCollateralInfo(address token) external view returns (
        bool isSupported,
        uint256 ltvRatio,
        uint256 liquidationThreshold,
        uint256 liquidationBonus,
        uint256 currentPrice,
        uint256 lastUpdate
    ) {
        CollateralConfig memory config = collateralConfigs[token];
        return (
            config.isSupported,
            config.ltvRatio,
            config.liquidationThreshold,
            config.liquidationBonus,
            tokenPrices[token],
            lastPriceUpdate[token]
        );
    }
}

