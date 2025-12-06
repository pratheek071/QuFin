// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/token/ERC20/extensions/ERC20Burnable.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

/**
 * @title QFINToken
 * @dev QFIN Governance Token for Qubic Finance Protocol
 * Total Supply: 100,000,000 QFIN
 * Used for: Governance, Staking, Rewards, Fee discounts
 */
contract QFINToken is ERC20, ERC20Burnable, Ownable {
    uint256 public constant MAX_SUPPLY = 100_000_000 * 10**18;
    
    // Reward distribution
    mapping(address => uint256) public rewardsEarned;
    mapping(address => uint256) public stakedBalance;
    
    // Staking parameters
    uint256 public stakingAPY = 12; // 12% APY
    uint256 public totalStaked;
    
    event Staked(address indexed user, uint256 amount);
    event Unstaked(address indexed user, uint256 amount);
    event RewardsClaimed(address indexed user, uint256 amount);
    
    constructor(address initialOwner) ERC20("Qubic Finance Token", "QFIN") Ownable(initialOwner) {
        // Mint initial supply to owner
        _mint(initialOwner, MAX_SUPPLY);
    }
    
    /**
     * @dev Stake QFIN tokens to earn rewards
     */
    function stake(uint256 amount) external {
        require(amount > 0, "Cannot stake 0");
        require(balanceOf(msg.sender) >= amount, "Insufficient balance");
        
        // Update rewards before staking
        _updateRewards(msg.sender);
        
        // Transfer tokens to contract
        _transfer(msg.sender, address(this), amount);
        
        stakedBalance[msg.sender] += amount;
        totalStaked += amount;
        
        emit Staked(msg.sender, amount);
    }
    
    /**
     * @dev Unstake QFIN tokens
     */
    function unstake(uint256 amount) external {
        require(amount > 0, "Cannot unstake 0");
        require(stakedBalance[msg.sender] >= amount, "Insufficient staked balance");
        
        // Update rewards before unstaking
        _updateRewards(msg.sender);
        
        stakedBalance[msg.sender] -= amount;
        totalStaked -= amount;
        
        // Transfer tokens back to user
        _transfer(address(this), msg.sender, amount);
        
        emit Unstaked(msg.sender, amount);
    }
    
    /**
     * @dev Claim staking rewards
     */
    function claimRewards() external {
        _updateRewards(msg.sender);
        
        uint256 rewards = rewardsEarned[msg.sender];
        require(rewards > 0, "No rewards to claim");
        
        rewardsEarned[msg.sender] = 0;
        _mint(msg.sender, rewards);
        
        emit RewardsClaimed(msg.sender, rewards);
    }
    
    /**
     * @dev Internal function to update user rewards
     */
    function _updateRewards(address user) internal {
        if (stakedBalance[user] > 0) {
            // Calculate rewards based on APY
            // Simplified calculation - in production, use time-based calculation
            uint256 rewards = (stakedBalance[user] * stakingAPY) / 100 / 365; // Daily rewards
            rewardsEarned[user] += rewards;
        }
    }
    
    /**
     * @dev Get user's pending rewards
     */
    function pendingRewards(address user) external view returns (uint256) {
        if (stakedBalance[user] == 0) return rewardsEarned[user];
        
        uint256 pending = (stakedBalance[user] * stakingAPY) / 100 / 365;
        return rewardsEarned[user] + pending;
    }
    
    /**
     * @dev Update staking APY (owner only)
     */
    function setStakingAPY(uint256 newAPY) external onlyOwner {
        require(newAPY <= 100, "APY too high");
        stakingAPY = newAPY;
    }
}

