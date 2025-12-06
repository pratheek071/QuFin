package com.example.finapp.presentation.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.finapp.R
import com.example.finapp.databinding.FragmentWalletConnectionBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalletConnectionFragment : Fragment() {
    
    private var _binding: FragmentWalletConnectionBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: WalletViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletConnectionBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        viewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                // Navigate to wallet dashboard
                findNavController().navigate(R.id.action_walletConnection_to_walletDashboard)
            }
        }
        
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnCreateWallet.isEnabled = !isLoading
            binding.btnImportWallet.isEnabled = !isLoading
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
        binding.btnCreateWallet.setOnClickListener {
            showCreateWalletConfirmation()
        }
        
        binding.btnImportWallet.setOnClickListener {
            showImportWalletDialog()
        }
    }
    
    private fun showCreateWalletConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Create New Wallet")
            .setMessage("A new wallet will be created. Make sure to backup your private key!")
            .setPositiveButton("Create") { _, _ ->
                viewModel.createWallet()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showImportWalletDialog() {
        val input = android.widget.EditText(requireContext())
        input.hint = "Enter private key (0x...)"
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Import Wallet")
            .setMessage("Enter your private key to import existing wallet")
            .setView(input)
            .setPositiveButton("Import") { _, _ ->
                val privateKey = input.text.toString().trim()
                if (privateKey.isNotEmpty()) {
                    viewModel.importWallet(privateKey)
                } else {
                    Toast.makeText(requireContext(), "Private key cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

