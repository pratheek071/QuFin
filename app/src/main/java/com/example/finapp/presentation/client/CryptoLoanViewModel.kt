package com.example.finapp.presentation.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finapp.blockchain.QubicConfig
import com.example.finapp.blockchain.SmartContractInterface
import com.example.finapp.blockchain.WalletManager
import com.example.finapp.data.model.Collateral
import com.example.finapp.data.model.CryptoLoan
import com.example.finapp.data.model.LoanStatus
import com.example.finapp.data.model.LoanType
import com.example.finapp.data.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CryptoLoanViewModel @Inject constructor(
    private val loanRepository: LoanRepository,
    private val smartContractInterface: SmartContractInterface,
    private val walletManager: WalletManager
) : ViewModel() {
    
    private val _selectedCollateral = MutableLiveData<QubicConfig.CollateralToken?>()
    val selectedCollateral: LiveData<QubicConfig.CollateralToken?> = _selectedCollateral
    
    private val _collateralAmount = MutableLiveData<BigDecimal>()
    val collateralAmount: LiveData<BigDecimal> = _collateralAmount
    
    private val _maxLoanAmount = MutableLiveData<Double>()
    val maxLoanAmount: LiveData<Double> = _maxLoanAmount
    
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    
    private val _transactionHash = MutableLiveData<String?>()
    val transactionHash: LiveData<String?> = _transactionHash
    
    val supportedCollaterals = QubicConfig.SUPPORTED_COLLATERAL
    
    fun selectCollateral(collateral: QubicConfig.CollateralToken) {
        _selectedCollateral.value = collateral
        calculateMaxLoan()
    }
    
    fun setCollateralAmount(amount: String) {
        try {
            val amountDecimal = BigDecimal(amount)
            _collateralAmount.value = amountDecimal
            calculateMaxLoan()
        } catch (e: Exception) {
            _error.value = "Invalid collateral amount"
        }
    }
    
    private fun calculateMaxLoan() {
        val collateral = _selectedCollateral.value ?: return
        val amount = _collateralAmount.value ?: return
        
        // Calculate max loan based on LTV ratio
        // Assume 1 collateral token = $100 for demo (in production, fetch from oracle)
        val collateralValueUSD = amount.toDouble() * 100.0
        val maxLoan = collateralValueUSD * collateral.ltvRatio
        
        _maxLoanAmount.value = maxLoan
    }
    
    fun createCryptoLoan(
        loanType: LoanType,
        loanAmount: Double,
        duration: Int
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val collateral = _selectedCollateral.value
                    ?: throw IllegalStateException("No collateral selected")
                val collateralAmt = _collateralAmount.value
                    ?: throw IllegalStateException("No collateral amount set")
                
                // Validate loan amount
                val maxLoan = _maxLoanAmount.value ?: 0.0
                if (loanAmount > maxLoan) {
                    throw IllegalArgumentException("Loan amount exceeds maximum ($maxLoan)")
                }
                
                // Step 1: Approve collateral token spending
                val approvalTx = smartContractInterface.approveToken(
                    tokenAddress = "0x${collateral.symbol}", // Mock address
                    spender = QubicConfig.LENDING_POOL_CONTRACT,
                    amount = collateralAmt
                )
                
                _successMessage.value = "Approval pending..."
                
                // Step 2: Create loan on blockchain
                val txHash = smartContractInterface.createLoan(
                    collateralToken = "0x${collateral.symbol}",
                    collateralAmount = collateralAmt,
                    loanAmount = BigDecimal(loanAmount),
                    duration = duration,
                    loanType = loanType.name
                )
                
                _transactionHash.value = txHash
                
                // Step 3: Save loan to Firestore (for UI tracking)
                val userId = walletManager.getWalletAddress()
                    ?: throw IllegalStateException("No wallet connected")
                
                val cryptoLoan = CryptoLoan(
                    userId = userId,
                    loanType = loanType,
                    principalAmount = loanAmount,
                    interestRate = calculateInterestRate(loanType, duration),
                    duration = duration,
                    totalAmount = calculateTotalAmount(loanAmount, duration, loanType),
                    dailyAmount = calculateDailyAmount(loanAmount, duration, loanType),
                    status = LoanStatus.PENDING,
                    collateralToken = collateral.symbol,
                    collateralAmount = collateralAmt,
                    collateralValueUSD = collateralAmt.multiply(BigDecimal(100)),
                    isCollateralized = true,
                    creationTxHash = txHash,
                    loanToValue = collateral.ltvRatio * 100
                )
                
                // Note: In production, save to Firestore via repository
                // loanRepository.createLoan(cryptoLoan)
                
                _successMessage.value = "Loan created successfully! TX: ${txHash.take(10)}..."
                
            } catch (e: Exception) {
                _error.value = "Failed to create loan: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    
    private fun calculateInterestRate(loanType: LoanType, duration: Int): Double {
        val baseRate = when (loanType) {
            LoanType.EDUCATION -> 5.0
            LoanType.PERSONAL -> 8.0
            LoanType.HOME -> 6.0
            LoanType.CAR -> 7.0
            LoanType.COLLATERALIZED -> 7.5
            LoanType.FLASH_LOAN -> 0.1 // Flash loans have minimal interest
        }
        
        // Adjust for duration
        val durationMultiplier = 1.0 + (duration / 365.0) * 0.5
        return baseRate * durationMultiplier
    }
    
    private fun calculateTotalAmount(principal: Double, duration: Int, loanType: LoanType): Double {
        val interestRate = calculateInterestRate(loanType, duration)
        val interest = principal * (interestRate / 100.0)
        return principal + interest
    }
    
    private fun calculateDailyAmount(principal: Double, duration: Int, loanType: LoanType): Double {
        val total = calculateTotalAmount(principal, duration, loanType)
        return total / duration
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}

