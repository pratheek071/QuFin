package com.example.finapp.presentation.wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.finapp.R
import com.example.finapp.databinding.FragmentWalletDashboardBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalletDashboardFragment : Fragment() {
    
    private var _binding: FragmentWalletDashboardBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: WalletViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        viewModel.wallet.observe(viewLifecycleOwner) { wallet ->
            wallet?.let {
                // Truncate address for display
                val shortAddress = "${it.address.take(6)}...${it.address.takeLast(4)}"
                binding.tvWalletAddress.text = shortAddress
                binding.tvBalance.text = "${it.balance} QUBIC"
                
                // Mock USD value (in production, fetch from price oracle)
                val usdValue = it.balance.toDouble() * 20.0 // Assuming 1 QUBIC = $20
                binding.tvBalanceUSD.text = "≈ $${String.format("%.2f", usdValue)} USD"
                
                binding.tvQFINBalance.text = "${it.qfinBalance} QFIN"
            }
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        
        viewModel.successMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearSuccessMessage()
            }
        }
    }
    
    private fun setupClickListeners() {
        // Copy address to clipboard
        binding.tvWalletAddress.setOnClickListener {
            val address = viewModel.wallet.value?.address
            if (address != null) {
                copyToClipboard(address)
                Toast.makeText(requireContext(), "Address copied!", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnSend.setOnClickListener {
            showSendDialog()
        }
        
        binding.btnReceive.setOnClickListener {
            showReceiveDialog()
        }
        
        binding.btnRefresh.setOnClickListener {
            viewModel.refreshBalance()
        }
        
        binding.btnStake.setOnClickListener {
            Toast.makeText(requireContext(), "Staking coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnApplyLoan.setOnClickListener {
            // Navigate to loan application
            findNavController().navigate(R.id.action_walletDashboard_to_loanApplication)
        }
        
        binding.btnViewLoans.setOnClickListener {
            // Navigate to loans list
            findNavController().navigate(R.id.action_walletDashboard_to_clientDashboard)
        }
        
        binding.btnTransactionHistory.setOnClickListener {
            Toast.makeText(requireContext(), "Transaction history coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnSettings.setOnClickListener {
            showWalletSettings()
        }
    }
    
    private fun showSendDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_send_crypto, null)
        val etAddress = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etAddress)
        val etAmount = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etAmount)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Send QUBIC")
            .setView(dialogView)
            .setPositiveButton("Send") { _, _ ->
                val address = etAddress.text.toString()
                val amount = etAmount.text.toString()
                
                if (address.isNotEmpty() && amount.isNotEmpty()) {
                    viewModel.sendTransaction(address, amount)
                } else {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showReceiveDialog() {
        val address = viewModel.wallet.value?.address ?: return
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Receive QUBIC")
            .setMessage("Share this address to receive QUBIC:\n\n$address")
            .setPositiveButton("Copy Address") { _, _ ->
                copyToClipboard(address)
                Toast.makeText(requireContext(), "Address copied!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }
    
    private fun showWalletSettings() {
        val options = arrayOf(
            "View Private Key",
            "Disconnect Wallet",
            "Cancel"
        )
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Wallet Settings")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showPrivateKey()
                    1 -> disconnectWallet()
                }
            }
            .show()
    }
    
    private fun showPrivateKey() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("⚠️ Warning")
            .setMessage("Never share your private key with anyone! It gives full access to your wallet.")
            .setPositiveButton("Show Private Key") { _, _ ->
                viewModel.exportPrivateKey { privateKey ->
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Private Key")
                        .setMessage(privateKey)
                        .setPositiveButton("Copy") { _, _ ->
                            copyToClipboard(privateKey)
                            Toast.makeText(requireContext(), "Private key copied!", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Close", null)
                        .show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun disconnectWallet() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Disconnect Wallet")
            .setMessage("Are you sure you want to disconnect your wallet? Make sure you have backed up your private key!")
            .setPositiveButton("Disconnect") { _, _ ->
                viewModel.disconnectWallet()
                findNavController().navigate(R.id.action_walletDashboard_to_walletConnection)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Wallet Address", text)
        clipboard.setPrimaryClip(clip)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

