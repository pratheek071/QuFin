package com.example.finapp.presentation.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finapp.blockchain.QubicSDK
import com.example.finapp.blockchain.TransactionHandler
import com.example.finapp.blockchain.WalletManager
import com.example.finapp.data.model.TokenBalance
import com.example.finapp.data.model.Wallet
import com.example.finapp.data.model.WalletTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletManager: WalletManager,
    private val qubicSDK: QubicSDK,
    private val transactionHandler: TransactionHandler
) : ViewModel() {
    
    private val _wallet = MutableLiveData<Wallet>()
    val wallet: LiveData<Wallet> = _wallet
    
    private val _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean> = _isConnected
    
    private val _transactions = MutableLiveData<List<WalletTransaction>>()
    val transactions: LiveData<List<WalletTransaction>> = _transactions
    
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    
    init {
        checkWalletConnection()
    }
    
    private fun checkWalletConnection() {
        viewModelScope.launch {
            try {
                if (walletManager.hasWallet()) {
                    loadWalletData()
                    _isConnected.value = true
                } else {
                    _isConnected.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error checking wallet: ${e.message}"
            }
        }
    }
    
    fun createWallet() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val walletInfo = walletManager.createWallet()
                loadWalletData()
                _isConnected.value = true
                _successMessage.value = "Wallet created successfully!"
            } catch (e: Exception) {
                _error.value = "Failed to create wallet: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    
    fun importWallet(privateKey: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val walletInfo = walletManager.importWallet(privateKey)
                loadWalletData()
                _isConnected.value = true
                _successMessage.value = "Wallet imported successfully!"
            } catch (e: Exception) {
                _error.value = "Failed to import wallet: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    
    private suspend fun loadWalletData() {
        try {
            val address = walletManager.getWalletAddress() ?: return
            val balance = qubicSDK.getBalance(address)
            
            _wallet.value = Wallet(
                address = address,
                balance = balance,
                isConnected = true
            )
        } catch (e: Exception) {
            _error.value = "Failed to load wallet data: ${e.message}"
        }
    }
    
    fun refreshBalance() {
        viewModelScope.launch {
            _loading.value = true
            try {
                loadWalletData()
                _successMessage.value = "Balance refreshed"
            } catch (e: Exception) {
                _error.value = "Failed to refresh balance: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    
    fun sendTransaction(toAddress: String, amount: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val amountDecimal = BigDecimal(amount)
                val result = transactionHandler.sendNativeTransfer(toAddress, amountDecimal)
                
                when (result) {
                    is TransactionHandler.TransactionResult.Success -> {
                        _successMessage.value = "Transaction sent! Hash: ${result.txHash.take(10)}..."
                        loadWalletData()
                    }
                    is TransactionHandler.TransactionResult.Error -> {
                        _error.value = result.message
                    }
                }
            } catch (e: Exception) {
                _error.value = "Transaction failed: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    
    fun exportPrivateKey(callback: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val privateKey = walletManager.exportPrivateKey()
                callback(privateKey)
            } catch (e: Exception) {
                _error.value = "Failed to export private key: ${e.message}"
            }
        }
    }
    
    fun disconnectWallet() {
        walletManager.deleteWallet()
        _isConnected.value = false
        _wallet.value = null
        _successMessage.value = "Wallet disconnected"
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}

