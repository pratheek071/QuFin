package com.example.finapp.blockchain

/**
 * Qubic Network Configuration
 * Testnet and Mainnet endpoints
 */
object QubicConfig {
    // Network Configuration
    // Option 1: Qubetics Testnet (EVM-Compatible) - RECOMMENDED FOR HACKATHON
    const val QUBETICS_TESTNET_RPC = "https://rpc-testnet.qubetics.work"
    const val QUBETICS_TESTNET_CHAIN_ID = 9029
    
    // Option 2: Native Qubic Network (Custom Architecture - Advanced)
    const val QUBIC_TESTNET_RPC = "https://testnet-rpc.qubicdev.com"
    const val QUBIC_MAINNET_RPC = "https://rpc.qubic.org"
    
    // Current network (Using Qubetics for EVM compatibility)
    const val CURRENT_NETWORK = QUBETICS_TESTNET_RPC
    const val CHAIN_ID = QUBETICS_TESTNET_CHAIN_ID
    const val IS_TESTNET = true
    
    // Smart Contract Addresses (Deploy and update these)
    const val LENDING_POOL_CONTRACT = "0x0000000000000000000000000000000000000000" // Update after deployment
    const val QFIN_TOKEN_CONTRACT = "0x0000000000000000000000000000000000000000"     // Update after deployment
    const val COLLATERAL_MANAGER_CONTRACT = "0x0000000000000000000000000000000000000000" // Update after deployment
    
    // Token Configurations
    const val QFIN_TOKEN_SYMBOL = "QFIN"
    const val QFIN_TOKEN_DECIMALS = 18
    const val QFIN_TOKEN_NAME = "Qubic Finance Token"
    
    // Lending Protocol Parameters
    const val MIN_COLLATERAL_RATIO = 150 // 150% collateralization
    const val LIQUIDATION_THRESHOLD = 130 // Liquidation at 130%
    const val BASE_INTEREST_RATE = 5.0 // 5% base APR
    const val UTILIZATION_MULTIPLIER = 2.0 // Interest rate multiplier based on pool utilization
    
    // Transaction Configuration
    const val DEFAULT_GAS_LIMIT = 300000L
    const val DEFAULT_GAS_PRICE = 1000000000L // 1 Gwei
    
    // Supported Collateral Tokens
    val SUPPORTED_COLLATERAL = listOf(
        CollateralToken("QBTC", "Qubic Bitcoin", 18, 0.70), // 70% LTV
        CollateralToken("QETH", "Qubic Ethereum", 18, 0.65), // 65% LTV
        CollateralToken("QUSD", "Qubic USD", 6, 0.90),       // 90% LTV (stablecoin)
        CollateralToken("QBN", "Qubic BNB", 18, 0.60)        // 60% LTV
    )
    
    // Loan Types Configuration
    enum class LoanType(val maxAmount: Double, val maxDuration: Int, val riskMultiplier: Double) {
        EDUCATION(50000.0, 365, 0.8),   // Lower risk
        PERSONAL(30000.0, 180, 1.2),    // Higher risk
        HOME(200000.0, 730, 0.9),       // Lower risk, longer duration
        CAR(100000.0, 365, 1.0)         // Medium risk
    }
    
    data class CollateralToken(
        val symbol: String,
        val name: String,
        val decimals: Int,
        val ltvRatio: Double // Loan-to-Value ratio
    )
}

