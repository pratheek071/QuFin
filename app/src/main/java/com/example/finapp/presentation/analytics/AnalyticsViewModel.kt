package com.example.finapp.presentation.analytics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finapp.blockchain.QubicSDK
import com.example.finapp.blockchain.SmartContractInterface
import com.example.finapp.blockchain.WalletManager
import com.example.finapp.data.model.DeFiStats
import com.example.finapp.data.model.UserDeFiPortfolio
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val smartContractInterface: SmartContractInterface,
    private val qubicSDK: QubicSDK,
    private val walletManager: WalletManager
) : ViewModel() {
    
    private val _protocolStats = MutableLiveData<DeFiStats>()
    val protocolStats: LiveData<DeFiStats> = _protocolStats
    
    private val _userPortfolio = MutableLiveData<UserDeFiPortfolio>()
    val userPortfolio: LiveData<UserDeFiPortfolio> = _userPortfolio
    
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    init {
        loadAnalytics()
    }
    
    fun loadAnalytics() {
        viewModelScope.launch {
            _loading.value = true
            try {
                loadProtocolStats()
                loadUserPortfolio()
            } catch (e: Exception) {
                _error.value = "Failed to load analytics: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    
    private suspend fun loadProtocolStats() {
        // In production, fetch from smart contract events via EasyConnect
        // For demo, use realistic mock data
        _protocolStats.value = DeFiStats(
            totalValueLocked = BigDecimal("1250000"), // $1.25M
            totalLoansIssued = 156,
            totalActiveLoans = 89,
            totalBorrowed = BigDecimal("890000"),
            totalRepaid = BigDecimal("234000"),
            averageAPY = 7.5,
            utilizationRate = 71.2,
            totalCollateralLocked = BigDecimal("1875000"),
            uniqueBorrowers = 142
        )
    }
    
    private suspend fun loadUserPortfolio() {
        val address = walletManager.getWalletAddress() ?: return
        
        // Get user's QFIN balance
        val qfinBalance = try {
            smartContractInterface.getQFINBalance(address)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
        
        // Mock user portfolio (in production, fetch from EasyConnect analytics)
        _userPortfolio.value = UserDeFiPortfolio(
            userId = address,
            walletAddress = address,
            totalBorrowed = BigDecimal("5000"),
            totalRepaid = BigDecimal("1200"),
            activeLoans = 2,
            averageHealthFactor = 1.65,
            totalCollateralValue = BigDecimal("12500"),
            qfinBalance = qfinBalance,
            totalRewardsEarned = BigDecimal("125.50"),
            pendingRewards = BigDecimal("12.30"),
            creditScore = 750,
            totalTransactions = 23,
            onTimePayments = 21,
            latePayments = 2,
            defaultedLoans = 0,
            maxBorrowingCapacity = BigDecimal("8750")
        )
    }
    
    fun refreshAnalytics() {
        loadAnalytics()
    }
    
    fun clearError() {
        _error.value = null
    }
}

