package com.example.finapp.presentation.analytics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.finapp.databinding.FragmentAnalyticsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class AnalyticsFragment : Fragment() {
    
    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AnalyticsViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        viewModel.protocolStats.observe(viewLifecycleOwner) { stats ->
            updateProtocolStats(stats)
        }
        
        viewModel.userPortfolio.observe(viewLifecycleOwner) { portfolio ->
            updateUserPortfolio(portfolio)
        }
        
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.scrollView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnRefresh.setOnClickListener {
            viewModel.refreshAnalytics()
        }
    }
    
    private fun updateProtocolStats(stats: com.example.finapp.data.model.DeFiStats) {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        
        binding.tvTvl.text = formatter.format(stats.totalValueLocked.toDouble())
        binding.tvActiveLoans.text = stats.totalActiveLoans.toString()
        binding.tvTotalBorrowed.text = formatter.format(stats.totalBorrowed.toDouble())
        binding.tvUtilizationRate.text = "${String.format("%.1f", stats.utilizationRate)}%"
        binding.tvAverageApy.text = "${String.format("%.2f", stats.averageAPY)}%"
        binding.tvUniqueBorrowers.text = stats.uniqueBorrowers.toString()
    }
    
    private fun updateUserPortfolio(portfolio: com.example.finapp.data.model.UserDeFiPortfolio) {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        
        binding.tvUserBorrowed.text = formatter.format(portfolio.totalBorrowed.toDouble())
        binding.tvUserCollateral.text = formatter.format(portfolio.totalCollateralValue.toDouble())
        binding.tvHealthFactor.text = String.format("%.2f", portfolio.averageHealthFactor)
        binding.tvCreditScore.text = "${portfolio.creditScore}/1000"
        binding.tvQfinBalance.text = "${portfolio.qfinBalance} QFIN"
        binding.tvRewards.text = "${portfolio.totalRewardsEarned} QFIN"
        
        // Set health factor color
        val healthColor = when {
            portfolio.averageHealthFactor >= 1.5 -> android.graphics.Color.GREEN
            portfolio.averageHealthFactor >= 1.2 -> android.graphics.Color.parseColor("#FFA500")
            else -> android.graphics.Color.RED
        }
        binding.tvHealthFactor.setTextColor(healthColor)
        
        // Set health status text
        val healthStatus = when {
            portfolio.averageHealthFactor >= 1.5 -> "✓ Healthy"
            portfolio.averageHealthFactor >= 1.2 -> "⚠ Warning"
            else -> "⚠ Critical"
        }
        binding.tvHealthStatus.text = healthStatus
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

