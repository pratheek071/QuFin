package com.example.finapp.data.model

import java.math.BigDecimal

/**
 * Wallet model for blockchain wallet management
 */
data class Wallet(
    val address: String = "",
    val balance: BigDecimal = BigDecimal.ZERO,
    val qfinBalance: BigDecimal = BigDecimal.ZERO,
    val collateralBalances: Map<String, BigDecimal> = emptyMap(),
    val isConnected: Boolean = false,
    val networkId: String = "testnet",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Token balance information
 */
data class TokenBalance(
    val tokenAddress: String,
    val symbol: String,
    val balance: BigDecimal,
    val decimals: Int,
    val valueInUSD: BigDecimal = BigDecimal.ZERO
)

/**
 * Wallet transaction history
 */
data class WalletTransaction(
    val id: String = "",
    val txHash: String,
    val from: String,
    val to: String,
    val amount: BigDecimal,
    val tokenSymbol: String = "QUBIC",
    val type: TransactionType,
    val status: TransactionStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val blockNumber: String? = null,
    val gasUsed: String? = null,
    val gasPrice: String? = null
)

enum class TransactionType {
    SEND,
    RECEIVE,
    LOAN_PAYMENT,
    COLLATERAL_DEPOSIT,
    COLLATERAL_WITHDRAWAL,
    TOKEN_TRANSFER,
    CONTRACT_INTERACTION
}

enum class TransactionStatus {
    PENDING,
    CONFIRMED,
    FAILED
}

