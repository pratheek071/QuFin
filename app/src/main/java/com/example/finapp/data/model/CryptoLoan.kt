package com.example.finapp.data.model

import java.math.BigDecimal

/**
 * Enhanced Loan model with blockchain integration
 * Extends the original Loan functionality with DeFi features
 */
data class CryptoLoan(
    // Original fields
    val id: String = "",
    val userId: String = "",
    val loanType: LoanType = LoanType.PERSONAL,
    val principalAmount: Double = 0.0,
    val interestRate: Double = 0.0,
    val duration: Int = 0, // in days
    val totalAmount: Double = 0.0,
    val dailyAmount: Double = 0.0,
    val status: LoanStatus = LoanStatus.PENDING,
    val appliedDate: Long = System.currentTimeMillis(),
    val approvedDate: Long? = null,
    val paidAmount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val lastPaymentDate: Long? = null,
    
    // Blockchain-specific fields
    val blockchainLoanId: String? = null, // On-chain loan ID
    val collateralToken: String = "", // Token used as collateral (QBTC, QETH, etc.)
    val collateralAmount: BigDecimal = BigDecimal.ZERO,
    val collateralValueUSD: BigDecimal = BigDecimal.ZERO,
    val collateralizationRatio: Double = 0.0, // Current collateral ratio
    val liquidationThreshold: Double = 130.0, // Liquidation at 130%
    val minCollateralRatio: Double = 150.0, // Minimum 150%
    val isCollateralized: Boolean = false,
    val loanToValue: Double = 0.0, // LTV ratio
    
    // Smart contract interaction
    val contractAddress: String = "",
    val creationTxHash: String? = null,
    val approvalTxHash: String? = null,
    
    // DeFi features
    val apy: Double = 0.0, // Annual Percentage Yield
    val liquidityPoolShare: Double = 0.0,
    val rewardsEarned: BigDecimal = BigDecimal.ZERO, // QFIN tokens earned
    val canBeLiquidated: Boolean = false,
    
    // Risk assessment
    val riskLevel: RiskLevel = RiskLevel.MEDIUM,
    val healthFactor: Double = 0.0 // >1 is healthy, <1 can be liquidated
)

enum class LoanType {
    EDUCATION,
    PERSONAL,
    HOME,
    CAR,
    FLASH_LOAN, // New: Instant uncollateralized loan (must be repaid in same tx)
    COLLATERALIZED // New: Crypto-collateralized loan
}

enum class LoanStatus {
    PENDING,
    APPROVED,
    REJECTED,
    ACTIVE,
    COMPLETED,
    DEFAULTED,
    LIQUIDATED // New: When collateral is liquidated
}

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Collateral information
 */
data class Collateral(
    val token: String,
    val symbol: String,
    val amount: BigDecimal,
    val valueUSD: BigDecimal,
    val ltvRatio: Double,
    val isLocked: Boolean = false,
    val lockTxHash: String? = null
)

/**
 * Loan payment record on blockchain
 */
data class CryptoPayment(
    val id: String = "",
    val loanId: String,
    val userId: String,
    val amount: BigDecimal,
    val txHash: String,
    val from: String,
    val to: String,
    val blockNumber: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: PaymentStatus,
    val gasUsed: String,
    val confirmations: Int = 0
)

enum class PaymentStatus {
    PENDING,
    CONFIRMING,
    CONFIRMED,
    FAILED,
    REVERSED
}

/**
 * Liquidation event
 */
data class LiquidationEvent(
    val loanId: String,
    val liquidator: String,
    val borrower: String,
    val collateralToken: String,
    val collateralAmount: BigDecimal,
    val debtCovered: BigDecimal,
    val txHash: String,
    val timestamp: Long = System.currentTimeMillis()
)

