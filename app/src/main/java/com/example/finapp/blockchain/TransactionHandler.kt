package com.example.finapp.blockchain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Transaction Handler - Manages transaction lifecycle, monitoring, and status
 */
@Singleton
class TransactionHandler @Inject constructor(
    private val qubicSDK: QubicSDK,
    private val walletManager: WalletManager
) {
    
    companion object {
        private const val MAX_CONFIRMATIONS_WAIT = 60 // 60 blocks
        private const val POLL_INTERVAL_MS = 3000L // 3 seconds
    }
    
    /**
     * Send native token (QUBIC) transfer
     */
    suspend fun sendNativeTransfer(
        to: String,
        amount: BigDecimal
    ): TransactionResult = withContext(Dispatchers.IO) {
        try {
            val walletAddress = walletManager.getWalletAddress()
                ?: return@withContext TransactionResult.Error("No wallet connected")
            
            // Get transaction parameters
            val nonce = qubicSDK.getTransactionCount(walletAddress)
            val gasPrice = qubicSDK.getGasPrice()
            val gasLimit = BigInteger.valueOf(21000) // Standard ETH transfer
            
            // Convert amount to Wei
            val valueInWei = toWei(amount)
            
            // Sign transaction
            val signedTx = walletManager.signTransaction(
                to = to,
                value = valueInWei,
                data = "0x",
                nonce = nonce,
                gasPrice = gasPrice,
                gasLimit = gasLimit
            )
            
            // Send transaction
            val txHash = qubicSDK.sendRawTransaction(signedTx)
            
            TransactionResult.Success(
                txHash = txHash,
                from = walletAddress,
                to = to,
                amount = amount
            )
        } catch (e: Exception) {
            TransactionResult.Error(e.message ?: "Transaction failed")
        }
    }
    
    /**
     * Wait for transaction confirmation
     */
    suspend fun waitForConfirmation(
        txHash: String,
        requiredConfirmations: Int = 1
    ): ConfirmationStatus = withContext(Dispatchers.IO) {
        var attempts = 0
        val maxAttempts = MAX_CONFIRMATIONS_WAIT
        
        while (attempts < maxAttempts) {
            val receipt = qubicSDK.getTransactionReceipt(txHash)
            
            if (receipt != null) {
                if (receipt.status) {
                    val currentBlock = qubicSDK.getBlockNumber()
                    val txBlock = BigInteger(receipt.blockNumber.removePrefix("0x"), 16)
                    val confirmations = currentBlock - txBlock
                    
                    if (confirmations >= BigInteger.valueOf(requiredConfirmations.toLong())) {
                        return@withContext ConfirmationStatus.Confirmed(
                            txHash = txHash,
                            blockNumber = receipt.blockNumber,
                            confirmations = confirmations.toInt(),
                            gasUsed = receipt.gasUsed
                        )
                    }
                } else {
                    return@withContext ConfirmationStatus.Failed(
                        txHash = txHash,
                        reason = "Transaction reverted"
                    )
                }
            }
            
            delay(POLL_INTERVAL_MS)
            attempts++
        }
        
        ConfirmationStatus.Pending(txHash)
    }
    
    /**
     * Get transaction status
     */
    suspend fun getTransactionStatus(txHash: String): TransactionStatus = withContext(Dispatchers.IO) {
        try {
            val receipt = qubicSDK.getTransactionReceipt(txHash)
            
            if (receipt == null) {
                return@withContext TransactionStatus.PENDING
            }
            
            if (receipt.status) {
                TransactionStatus.SUCCESS
            } else {
                TransactionStatus.FAILED
            }
        } catch (e: Exception) {
            TransactionStatus.UNKNOWN
        }
    }
    
    /**
     * Estimate transaction cost in native token
     */
    suspend fun estimateTransactionCost(
        to: String,
        data: String = "0x",
        value: BigDecimal = BigDecimal.ZERO
    ): BigDecimal = withContext(Dispatchers.IO) {
        try {
            val walletAddress = walletManager.getWalletAddress()
                ?: return@withContext BigDecimal.ZERO
            
            val gasLimit = qubicSDK.estimateGas(
                from = walletAddress,
                to = to,
                data = data,
                value = "0x${toWei(value).toString(16)}"
            )
            
            val gasPrice = qubicSDK.getGasPrice()
            val totalCost = gasLimit * gasPrice
            
            fromWei(totalCost)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }
    
    /**
     * Get wallet balance
     */
    suspend fun getWalletBalance(): BigDecimal = withContext(Dispatchers.IO) {
        try {
            val address = walletManager.getWalletAddress() ?: return@withContext BigDecimal.ZERO
            qubicSDK.getBalance(address)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }
    
    /**
     * Batch transaction status check
     */
    suspend fun getBatchTransactionStatus(txHashes: List<String>): Map<String, TransactionStatus> {
        return txHashes.associateWith { hash ->
            getTransactionStatus(hash)
        }
    }
    
    /**
     * Calculate optimal gas price
     */
    suspend fun getOptimalGasPrice(): GasPriceEstimate = withContext(Dispatchers.IO) {
        try {
            val basePrice = qubicSDK.getGasPrice()
            
            GasPriceEstimate(
                slow = fromWei(basePrice.multiply(BigInteger.valueOf(90)).divide(BigInteger.valueOf(100))),
                standard = fromWei(basePrice),
                fast = fromWei(basePrice.multiply(BigInteger.valueOf(120)).divide(BigInteger.valueOf(100)))
            )
        } catch (e: Exception) {
            GasPriceEstimate(
                slow = BigDecimal("0.00001"),
                standard = BigDecimal("0.00002"),
                fast = BigDecimal("0.00003")
            )
        }
    }
    
    // Helper methods
    
    private fun toWei(amount: BigDecimal): BigInteger {
        return amount.multiply(BigDecimal.TEN.pow(18)).toBigInteger()
    }
    
    private fun fromWei(amount: BigInteger): BigDecimal {
        return BigDecimal(amount).divide(BigDecimal.TEN.pow(18))
    }
    
    // Data classes
    
    sealed class TransactionResult {
        data class Success(
            val txHash: String,
            val from: String,
            val to: String,
            val amount: BigDecimal
        ) : TransactionResult()
        
        data class Error(val message: String) : TransactionResult()
    }
    
    sealed class ConfirmationStatus {
        data class Confirmed(
            val txHash: String,
            val blockNumber: String,
            val confirmations: Int,
            val gasUsed: String
        ) : ConfirmationStatus()
        
        data class Pending(val txHash: String) : ConfirmationStatus()
        
        data class Failed(
            val txHash: String,
            val reason: String
        ) : ConfirmationStatus()
    }
    
    enum class TransactionStatus {
        PENDING,
        SUCCESS,
        FAILED,
        UNKNOWN
    }
    
    data class GasPriceEstimate(
        val slow: BigDecimal,
        val standard: BigDecimal,
        val fast: BigDecimal
    )
}

