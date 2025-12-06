package com.example.finapp.blockchain

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wallet Manager - Handles wallet creation, storage, and cryptographic operations
 * Securely stores private keys using Android KeyStore
 */
@Singleton
class WalletManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val KEYSTORE_ALIAS = "QubicWalletKey"
        private const val PREFS_NAME = "wallet_prefs"
        private const val KEY_ENCRYPTED_PRIVATE_KEY = "encrypted_private_key"
        private const val KEY_PUBLIC_ADDRESS = "public_address"
        private const val KEY_WALLET_CREATED = "wallet_created"
    }
    
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Check if wallet exists
     */
    fun hasWallet(): Boolean {
        return prefs.getBoolean(KEY_WALLET_CREATED, false)
    }
    
    /**
     * Create a new wallet
     */
    suspend fun createWallet(): WalletInfo = withContext(Dispatchers.IO) {
        // Generate random private key (32 bytes)
        val privateKey = generatePrivateKey()
        val publicAddress = deriveAddress(privateKey)
        
        // Encrypt and store private key
        val encryptedKey = encryptPrivateKey(privateKey)
        
        prefs.edit().apply {
            putString(KEY_ENCRYPTED_PRIVATE_KEY, encryptedKey)
            putString(KEY_PUBLIC_ADDRESS, publicAddress)
            putBoolean(KEY_WALLET_CREATED, true)
            apply()
        }
        
        WalletInfo(
            address = publicAddress,
            isImported = false
        )
    }
    
    /**
     * Import existing wallet from private key
     */
    suspend fun importWallet(privateKeyHex: String): WalletInfo = withContext(Dispatchers.IO) {
        val privateKey = hexToBytes(privateKeyHex)
        val publicAddress = deriveAddress(privateKey)
        
        // Encrypt and store private key
        val encryptedKey = encryptPrivateKey(privateKey)
        
        prefs.edit().apply {
            putString(KEY_ENCRYPTED_PRIVATE_KEY, encryptedKey)
            putString(KEY_PUBLIC_ADDRESS, publicAddress)
            putBoolean(KEY_WALLET_CREATED, true)
            apply()
        }
        
        WalletInfo(
            address = publicAddress,
            isImported = true
        )
    }
    
    /**
     * Get wallet address
     */
    fun getWalletAddress(): String? {
        return prefs.getString(KEY_PUBLIC_ADDRESS, null)
    }
    
    /**
     * Export private key (for backup)
     */
    suspend fun exportPrivateKey(): String = withContext(Dispatchers.IO) {
        val encryptedKey = prefs.getString(KEY_ENCRYPTED_PRIVATE_KEY, null)
            ?: throw IllegalStateException("No wallet found")
        
        val privateKey = decryptPrivateKey(encryptedKey)
        bytesToHex(privateKey)
    }
    
    /**
     * Sign transaction
     */
    suspend fun signTransaction(
        to: String,
        value: BigInteger,
        data: String,
        nonce: BigInteger,
        gasPrice: BigInteger,
        gasLimit: BigInteger
    ): String = withContext(Dispatchers.IO) {
        val encryptedKey = prefs.getString(KEY_ENCRYPTED_PRIVATE_KEY, null)
            ?: throw IllegalStateException("No wallet found")
        
        val privateKey = decryptPrivateKey(encryptedKey)
        
        // Build transaction
        val transaction = buildTransaction(to, value, data, nonce, gasPrice, gasLimit)
        
        // Sign transaction
        val signature = signData(transaction, privateKey)
        
        // Return signed transaction
        buildSignedTransaction(transaction, signature)
    }
    
    /**
     * Sign arbitrary message
     */
    suspend fun signMessage(message: String): String = withContext(Dispatchers.IO) {
        val encryptedKey = prefs.getString(KEY_ENCRYPTED_PRIVATE_KEY, null)
            ?: throw IllegalStateException("No wallet found")
        
        val privateKey = decryptPrivateKey(encryptedKey)
        val messageBytes = message.toByteArray()
        val signature = signData(messageBytes, privateKey)
        
        bytesToHex(signature)
    }
    
    /**
     * Delete wallet (logout)
     */
    fun deleteWallet() {
        prefs.edit().clear().apply()
        // Also delete from KeyStore
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            keyStore.deleteEntry(KEYSTORE_ALIAS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // Private helper methods
    
    private fun generatePrivateKey(): ByteArray {
        val random = SecureRandom()
        val privateKey = ByteArray(32)
        random.nextBytes(privateKey)
        return privateKey
    }
    
    private fun deriveAddress(privateKey: ByteArray): String {
        // Simplified address derivation
        // In production, use proper elliptic curve cryptography (secp256k1)
        val publicKey = derivePublicKey(privateKey)
        val hash = sha256(publicKey)
        return "0x" + bytesToHex(hash.takeLast(20).toByteArray())
    }
    
    private fun derivePublicKey(privateKey: ByteArray): ByteArray {
        // Simplified - in production, use proper ECC
        // This would use secp256k1 curve multiplication
        return sha256(privateKey)
    }
    
    private fun encryptPrivateKey(privateKey: ByteArray): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = getOrCreateSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        
        val iv = cipher.iv
        val encrypted = cipher.doFinal(privateKey)
        
        // Combine IV and encrypted data
        val combined = iv + encrypted
        return bytesToHex(combined)
    }
    
    private fun decryptPrivateKey(encryptedHex: String): ByteArray {
        val combined = hexToBytes(encryptedHex)
        val iv = combined.take(12).toByteArray()
        val encrypted = combined.drop(12).toByteArray()
        
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = getOrCreateSecretKey()
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        
        return cipher.doFinal(encrypted)
    }
    
    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        
        if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
            
            val spec = KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            
            keyGenerator.init(spec)
            keyGenerator.generateKey()
        }
        
        return keyStore.getKey(KEYSTORE_ALIAS, null) as SecretKey
    }
    
    private fun buildTransaction(
        to: String,
        value: BigInteger,
        data: String,
        nonce: BigInteger,
        gasPrice: BigInteger,
        gasLimit: BigInteger
    ): ByteArray {
        // Simplified transaction building
        // In production, use RLP encoding as per Ethereum/Qubic specs
        val txData = """
            {
                "nonce": "${nonce.toString(16)}",
                "gasPrice": "${gasPrice.toString(16)}",
                "gasLimit": "${gasLimit.toString(16)}",
                "to": "$to",
                "value": "${value.toString(16)}",
                "data": "$data"
            }
        """.trimIndent()
        
        return txData.toByteArray()
    }
    
    private fun signData(data: ByteArray, privateKey: ByteArray): ByteArray {
        // Simplified signing
        // In production, use ECDSA with secp256k1
        val hash = sha256(data)
        // This would be actual ECDSA signature
        return hash + privateKey.take(32).toByteArray()
    }
    
    private fun buildSignedTransaction(transaction: ByteArray, signature: ByteArray): String {
        // Simplified signed transaction format
        val combined = transaction + signature
        return "0x" + bytesToHex(combined)
    }
    
    private fun sha256(data: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(data)
    }
    
    private fun hexToBytes(hex: String): ByteArray {
        val cleanHex = hex.removePrefix("0x")
        return cleanHex.chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }
    
    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    data class WalletInfo(
        val address: String,
        val isImported: Boolean
    )
}

