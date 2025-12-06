package com.example.finapp.presentation.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finapp.blockchain.SmartContractInterface
import com.example.finapp.blockchain.TransactionHandler
import com.example.finapp.blockchain.WalletManager
import com.example.finapp.data.model.CryptoPayment
import com.example.finapp.data.model.PaymentStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CryptoPaymentViewModel @Inject constructor(
    private val smartContractInterface: SmartContractInterface,
    private val transactionHandler: TransactionHandler,
    private val walletManager: WalletManager
) : ViewModel() {
    
    private val _paymentAmount = MutableLiveData<BigDecimal>()
    val paymentAmount: LiveData<BigDecimal> = _paymentAmount
    
    private val _estimatedGas = MutableLiveData<BigDecimal>()
    val estimatedGas: LiveData<BigDecimal> = _estimatedGas
    
    private val _totalCost = MutableLiveData<BigDecimal>()
    val totalCost: LiveData<BigDecimal> = _totalCost
    
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    
    private val _transactionHash = MutableLiveData<String?>()
    val transactionHash: LiveData<String?> = _transactionHash
    
    private val _paymentStatus = MutableLiveData<PaymentStatus>()
    val paymentStatus: LiveData<PaymentStatus> = _paymentStatus
    
    fun setPaymentAmount(amount: String) {
        try {
            val amountDecimal = BigDecimal(amount)
            _paymentAmount.value = amountDecimal
            estimateTransactionCost(amountDecimal)
        } catch (e: Exception) {
            _error.value = "Invalid payment amount"
        }
    }
    
    private fun estimateTransactionCost(amount: BigDecimal) {
        viewModelScope.launch {
            try {
                // In production, get actual gas estimate from smart contract
                val estimatedGas = BigDecimal("0.001") // Mock value
                _estimatedGas.value = estimatedGas
                _totalCost.value = amount.add(estimatedGas)
            } catch (e: Exception) {
                _error.value = "Failed to estimate gas: ${e.message}"
            }
        }
    }
    
    fun makePayment(loanId: String) {
        viewModelScope.launch {
            _loading.value = true
            _paymentStatus.value = PaymentStatus.PENDING
            
            try {
                val amount = _paymentAmount.value
                    ?: throw IllegalStateException("No payment amount set")
                
                val walletAddress = walletManager.getWalletAddress()
                    ?: throw IllegalStateException("No wallet connected")
                
                // Check sufficient balance
                val balance = transactionHandler.getWalletBalance()
                val totalCost = _totalCost.value ?: amount
                
                if (balance < totalCost) {
                    throw IllegalStateException("Insufficient balance. Required: $totalCost, Available: $balance")
                }
                
                // Step 1: Make payment through smart contract
                _successMessage.value = "Submitting payment..."
                val txHash = smartContractInterface.makePayment(loanId, amount)
                
                _transactionHash.value = txHash
                _paymentStatus.value = PaymentStatus.CONFIRMING
                _successMessage.value = "Payment submitted! Waiting for confirmation..."
                
                // Step 2: Wait for transaction confirmation
                val confirmationResult = transactionHandler.waitForConfirmation(txHash, 1)
                
                when (confirmationResult) {
                    is TransactionHandler.ConfirmationStatus.Confirmed -> {
                        _paymentStatus.value = PaymentStatus.CONFIRMED
                        _successMessage.value = "Payment confirmed! ✅"
                        
                        // Step 3: Update payment record (in production, save to Firestore)
                        val payment = CryptoPayment(
                            loanId = loanId,
                            userId = walletAddress,
                            amount = amount,
                            txHash = txHash,
                            from = walletAddress,
                            to = com.example.finapp.blockchain.QubicConfig.LENDING_POOL_CONTRACT,
                            blockNumber = confirmationResult.blockNumber,
                            status = PaymentStatus.CONFIRMED,
                            gasUsed = confirmationResult.gasUsed,
                            confirmations = confirmationResult.confirmations
                        )
                        
                        // Note: Save to repository in production
                        // paymentRepository.recordPayment(payment)
                    }
                    is TransactionHandler.ConfirmationStatus.Failed -> {
                        _paymentStatus.value = PaymentStatus.FAILED
                        _error.value = "Payment failed: ${confirmationResult.reason}"
                    }
                    is TransactionHandler.ConfirmationStatus.Pending -> {
                        _paymentStatus.value = PaymentStatus.PENDING
                        _error.value = "Transaction is taking longer than expected"
                    }
                }
                
            } catch (e: Exception) {
                _paymentStatus.value = PaymentStatus.FAILED
                _error.value = "Payment failed: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    
    fun makeQuickPayment(loanId: String, dailyAmount: Double) {
        setPaymentAmount(dailyAmount.toString())
        makePayment(loanId)
    }
    
    fun verifyTransactionStatus(txHash: String) {
        viewModelScope.launch {
            try {
                val status = transactionHandler.getTransactionStatus(txHash)
                
                val paymentStatus = when (status) {
                    TransactionHandler.TransactionStatus.SUCCESS -> PaymentStatus.CONFIRMED
                    TransactionHandler.TransactionStatus.FAILED -> PaymentStatus.FAILED
                    TransactionHandler.TransactionStatus.PENDING -> PaymentStatus.CONFIRMING
                    else -> PaymentStatus.PENDING
                }
                
                _paymentStatus.value = paymentStatus
                _successMessage.value = "Status: ${paymentStatus.name}"
                
            } catch (e: Exception) {
                _error.value = "Failed to verify transaction: ${e.message}"
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}

