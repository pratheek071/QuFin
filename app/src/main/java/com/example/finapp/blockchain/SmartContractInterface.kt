package com.example.finapp.blockchain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Smart Contract Interface - Handles all interactions with deployed smart contracts
 * Lending Pool, QFIN Token, and Collateral Manager
 */
@Singleton
class SmartContractInterface @Inject constructor(
    private val qubicSDK: QubicSDK,
    private val walletManager: WalletManager
) {
    
    // ===== LENDING POOL CONTRACT METHODS =====
    
    /**
     * Create a new loan request
     */
    suspend fun createLoan(
        collateralToken: String,
        collateralAmount: BigDecimal,
        loanAmount: BigDecimal,
        duration: Int,
        loanType: String
    ): String = withContext(Dispatchers.IO) {
        val walletAddress = walletManager.getWalletAddress()
            ?: throw IllegalStateException("No wallet connected")
        
        // Encode function call
        val functionSig = "createLoan(address,uint256,uint256,uint256,string)"
        val params = listOf(
            collateralToken,
            toWei(collateralAmount),
            toWei(loanAmount),
            BigInteger.valueOf(duration.toLong()),
            loanType
        )
        
        // Estimate gas
        val data = encodeFunctionCall(functionSig, params)
        val gasLimit = qubicSDK.estimateGas(
            walletAddress,
            QubicConfig.LENDING_POOL_CONTRACT,
            data
        )
        
        // Get nonce and gas price
        val nonce = qubicSDK.getTransactionCount(walletAddress)
        val gasPrice = qubicSDK.getGasPrice()
        
        // Sign and send transaction
        val signedTx = walletManager.signTransaction(
            to = QubicConfig.LENDING_POOL_CONTRACT,
            value = BigInteger.ZERO,
            data = data,
            nonce = nonce,
            gasPrice = gasPrice,
            gasLimit = gasLimit
        )
        
        qubicSDK.sendRawTransaction(signedTx)
    }
    
    /**
     * Make a payment towards loan
     */
    suspend fun makePayment(
        loanId: String,
        amount: BigDecimal
    ): String = withContext(Dispatchers.IO) {
        val walletAddress = walletManager.getWalletAddress()
            ?: throw IllegalStateException("No wallet connected")
        
        val functionSig = "makePayment(uint256,uint256)"
        val params = listOf(
            BigInteger(loanId),
            toWei(amount)
        )
        
        val data = encodeFunctionCall(functionSig, params)
        val gasLimit = qubicSDK.estimateGas(walletAddress, QubicConfig.LENDING_POOL_CONTRACT, data)
        val nonce = qubicSDK.getTransactionCount(walletAddress)
        val gasPrice = qubicSDK.getGasPrice()
        
        val signedTx = walletManager.signTransaction(
            to = QubicConfig.LENDING_POOL_CONTRACT,
            value = toWei(amount),
            data = data,
            nonce = nonce,
            gasPrice = gasPrice,
            gasLimit = gasLimit
        )
        
        qubicSDK.sendRawTransaction(signedTx)
    }
    
    /**
     * Get loan details
     */
    suspend fun getLoanDetails(loanId: String): LoanDetails? = withContext(Dispatchers.IO) {
        try {
            val functionSig = "getLoan(uint256)"
            val result = qubicSDK.callContract(
                QubicConfig.LENDING_POOL_CONTRACT,
                functionSig,
                listOf(BigInteger(loanId))
            )
            
            parseLoanDetails(result)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Get all loans for user
     */
    suspend fun getUserLoans(userAddress: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val functionSig = "getUserLoans(address)"
            val result = qubicSDK.callContract(
                QubicConfig.LENDING_POOL_CONTRACT,
                functionSig,
                listOf(userAddress)
            )
            
            parseArrayResult(result)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Approve loan (admin only)
     */
    suspend fun approveLoan(loanId: String): String = withContext(Dispatchers.IO) {
        val walletAddress = walletManager.getWalletAddress()
            ?: throw IllegalStateException("No wallet connected")
        
        val functionSig = "approveLoan(uint256)"
        val params = listOf(BigInteger(loanId))
        
        val data = encodeFunctionCall(functionSig, params)
        val gasLimit = qubicSDK.estimateGas(walletAddress, QubicConfig.LENDING_POOL_CONTRACT, data)
        val nonce = qubicSDK.getTransactionCount(walletAddress)
        val gasPrice = qubicSDK.getGasPrice()
        
        val signedTx = walletManager.signTransaction(
            to = QubicConfig.LENDING_POOL_CONTRACT,
            value = BigInteger.ZERO,
            data = data,
            nonce = nonce,
            gasPrice = gasPrice,
            gasLimit = gasLimit
        )
        
        qubicSDK.sendRawTransaction(signedTx)
    }
    
    /**
     * Reject loan (admin only)
     */
    suspend fun rejectLoan(loanId: String): String = withContext(Dispatchers.IO) {
        val walletAddress = walletManager.getWalletAddress()
            ?: throw IllegalStateException("No wallet connected")
        
        val functionSig = "rejectLoan(uint256)"
        val params = listOf(BigInteger(loanId))
        
        val data = encodeFunctionCall(functionSig, params)
        val gasLimit = qubicSDK.estimateGas(walletAddress, QubicConfig.LENDING_POOL_CONTRACT, data)
        val nonce = qubicSDK.getTransactionCount(walletAddress)
        val gasPrice = qubicSDK.getGasPrice()
        
        val signedTx = walletManager.signTransaction(
            to = QubicConfig.LENDING_POOL_CONTRACT,
            value = BigInteger.ZERO,
            data = data,
            nonce = nonce,
            gasPrice = gasPrice,
            gasLimit = gasLimit
        )
        
        qubicSDK.sendRawTransaction(signedTx)
    }
    
    // ===== QFIN TOKEN CONTRACT METHODS =====
    
    /**
     * Get QFIN token balance
     */
    suspend fun getQFINBalance(address: String): BigDecimal = withContext(Dispatchers.IO) {
        try {
            val functionSig = "balanceOf(address)"
            val result = qubicSDK.callContract(
                QubicConfig.QFIN_TOKEN_CONTRACT,
                functionSig,
                listOf(address)
            )
            
            fromWei(BigInteger(result.removePrefix("0x"), 16))
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }
    
    /**
     * Transfer QFIN tokens
     */
    suspend fun transferQFIN(to: String, amount: BigDecimal): String = withContext(Dispatchers.IO) {
        val walletAddress = walletManager.getWalletAddress()
            ?: throw IllegalStateException("No wallet connected")
        
        val functionSig = "transfer(address,uint256)"
        val params = listOf(to, toWei(amount))
        
        val data = encodeFunctionCall(functionSig, params)
        val gasLimit = qubicSDK.estimateGas(walletAddress, QubicConfig.QFIN_TOKEN_CONTRACT, data)
        val nonce = qubicSDK.getTransactionCount(walletAddress)
        val gasPrice = qubicSDK.getGasPrice()
        
        val signedTx = walletManager.signTransaction(
            to = QubicConfig.QFIN_TOKEN_CONTRACT,
            value = BigInteger.ZERO,
            data = data,
            nonce = nonce,
            gasPrice = gasPrice,
            gasLimit = gasLimit
        )
        
        qubicSDK.sendRawTransaction(signedTx)
    }
    
    /**
     * Approve token spending
     */
    suspend fun approveToken(
        tokenAddress: String,
        spender: String,
        amount: BigDecimal
    ): String = withContext(Dispatchers.IO) {
        val walletAddress = walletManager.getWalletAddress()
            ?: throw IllegalStateException("No wallet connected")
        
        val functionSig = "approve(address,uint256)"
        val params = listOf(spender, toWei(amount))
        
        val data = encodeFunctionCall(functionSig, params)
        val gasLimit = qubicSDK.estimateGas(walletAddress, tokenAddress, data)
        val nonce = qubicSDK.getTransactionCount(walletAddress)
        val gasPrice = qubicSDK.getGasPrice()
        
        val signedTx = walletManager.signTransaction(
            to = tokenAddress,
            value = BigInteger.ZERO,
            data = data,
            nonce = nonce,
            gasPrice = gasPrice,
            gasLimit = gasLimit
        )
        
        qubicSDK.sendRawTransaction(signedTx)
    }
    
    // ===== COLLATERAL MANAGER METHODS =====
    
    /**
     * Get collateral value in USD
     */
    suspend fun getCollateralValue(
        tokenAddress: String,
        amount: BigDecimal
    ): BigDecimal = withContext(Dispatchers.IO) {
        try {
            val functionSig = "getCollateralValue(address,uint256)"
            val result = qubicSDK.callContract(
                QubicConfig.COLLATERAL_MANAGER_CONTRACT,
                functionSig,
                listOf(tokenAddress, toWei(amount))
            )
            
            fromWei(BigInteger(result.removePrefix("0x"), 16))
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }
    
    /**
     * Check if loan is liquidatable
     */
    suspend fun isLiquidatable(loanId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val functionSig = "isLiquidatable(uint256)"
            val result = qubicSDK.callContract(
                QubicConfig.COLLATERAL_MANAGER_CONTRACT,
                functionSig,
                listOf(BigInteger(loanId))
            )
            
            result.removePrefix("0x").toIntOrNull(16) == 1
        } catch (e: Exception) {
            false
        }
    }
    
    // ===== HELPER METHODS =====
    
    private fun toWei(amount: BigDecimal): BigInteger {
        return amount.multiply(BigDecimal.TEN.pow(18)).toBigInteger()
    }
    
    private fun fromWei(amount: BigInteger): BigDecimal {
        return BigDecimal(amount).divide(BigDecimal.TEN.pow(18))
    }
    
    private fun encodeFunctionCall(signature: String, params: List<Any>): String {
        // Simplified encoding
        // In production, use proper Web3 library for ABI encoding
        val functionName = signature.substringBefore("(")
        return "0x${functionName.hashCode().toString(16)}"
    }
    
    private fun parseLoanDetails(result: String): LoanDetails {
        // Simplified parsing
        // In production, use proper ABI decoding
        return LoanDetails(
            loanId = "0",
            borrower = "0x0",
            collateralToken = "0x0",
            collateralAmount = BigDecimal.ZERO,
            loanAmount = BigDecimal.ZERO,
            paidAmount = BigDecimal.ZERO,
            interestRate = 0.0,
            duration = 0,
            status = "PENDING"
        )
    }
    
    private fun parseArrayResult(result: String): List<String> {
        // Simplified array parsing
        return emptyList()
    }
    
    data class LoanDetails(
        val loanId: String,
        val borrower: String,
        val collateralToken: String,
        val collateralAmount: BigDecimal,
        val loanAmount: BigDecimal,
        val paidAmount: BigDecimal,
        val interestRate: Double,
        val duration: Int,
        val status: String
    )
}

