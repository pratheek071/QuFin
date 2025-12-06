package com.example.finapp.data.model

import java.math.BigDecimal

/**
 * DeFi Protocol Statistics
 */
data class DeFiStats(
    val totalValueLocked: BigDecimal = BigDecimal.ZERO,
    val totalLoansIssued: Int = 0,
    val totalActiveLoans: Int = 0,
    val totalBorrowed: BigDecimal = BigDecimal.ZERO,
    val totalRepaid: BigDecimal = BigDecimal.ZERO,
    val averageAPY: Double = 0.0,
    val utilizationRate: Double = 0.0, // Percentage of capital being used
    val totalCollateralLocked: BigDecimal = BigDecimal.ZERO,
    val uniqueBorrowers: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * User DeFi Portfolio
 */
data class UserDeFiPortfolio(
    val userId: String,
    val walletAddress: String,
    
    // Borrowing stats
    val totalBorrowed: BigDecimal = BigDecimal.ZERO,
    val totalRepaid: BigDecimal = BigDecimal.ZERO,
    val activeLoans: Int = 0,
    val averageHealthFactor: Double = 0.0,
    
    // Collateral stats
    val totalCollateralValue: BigDecimal = BigDecimal.ZERO,
    val collateralByToken: Map<String, BigDecimal> = emptyMap(),
    
    // Rewards & earnings
    val qfinBalance: BigDecimal = BigDecimal.ZERO,
    val totalRewardsEarned: BigDecimal = BigDecimal.ZERO,
    val pendingRewards: BigDecimal = BigDecimal.ZERO,
    
    // Credit score (on-chain reputation)
    val creditScore: Int = 0, // 0-1000
    val totalTransactions: Int = 0,
    val onTimePayments: Int = 0,
    val latePayments: Int = 0,
    val defaultedLoans: Int = 0,
    
    // Risk metrics
    val riskLevel: RiskLevel = RiskLevel.MEDIUM,
    val maxBorrowingCapacity: BigDecimal = BigDecimal.ZERO
)

/**
 * QFIN Token Info
 */
data class QFINTokenInfo(
    val totalSupply: BigDecimal,
    val circulatingSupply: BigDecimal,
    val price: BigDecimal,
    val marketCap: BigDecimal,
    val holders: Int,
    val yourBalance: BigDecimal = BigDecimal.ZERO,
    val yourStaked: BigDecimal = BigDecimal.ZERO,
    val stakingAPY: Double = 0.0
)

/**
 * Liquidity Pool Information
 */
data class LiquidityPool(
    val poolId: String,
    val name: String,
    val token0: String,
    val token1: String,
    val reserve0: BigDecimal,
    val reserve1: BigDecimal,
    val totalLiquidity: BigDecimal,
    val apy: Double,
    val volume24h: BigDecimal,
    val yourShare: BigDecimal = BigDecimal.ZERO,
    val yourLiquidity: BigDecimal = BigDecimal.ZERO
)

/**
 * Network Statistics
 */
data class NetworkStats(
    val blockNumber: Long,
    val blockTime: Double, // seconds
    val gasPrice: BigDecimal,
    val transactionsPerSecond: Double,
    val networkHashRate: String,
    val activeNodes: Int
)

/**
 * Price Feed Data
 */
data class PriceFeed(
    val tokenSymbol: String,
    val priceUSD: BigDecimal,
    val change24h: Double, // percentage
    val lastUpdate: Long = System.currentTimeMillis()
)

