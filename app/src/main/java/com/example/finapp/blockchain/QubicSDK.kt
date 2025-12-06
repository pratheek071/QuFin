package com.example.finapp.blockchain

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.math.BigDecimal
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Qubic SDK - Core interface for interacting with Qubic blockchain
 * Handles RPC calls, transaction signing, and contract interactions
 */
@Singleton
class QubicSDK @Inject constructor(
    private val context: Context
) {
    private val rpcEndpoint = QubicConfig.CURRENT_NETWORK
    
    /**
     * Get account balance for a given address
     */
    suspend fun getBalance(address: String): BigDecimal = withContext(Dispatchers.IO) {
        try {
            val response = makeRpcCall("qubic_getBalance", listOf(address))
            val balanceHex = response.getString("result")
            BigDecimal(BigInteger(balanceHex.removePrefix("0x"), 16))
                .divide(BigDecimal.TEN.pow(18)) // Convert from Wei
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }
    
    /**
     * Get current gas price
     */
    suspend fun getGasPrice(): BigInteger = withContext(Dispatchers.IO) {
        try {
            val response = makeRpcCall("qubic_gasPrice", emptyList())
            val gasPriceHex = response.getString("result")
            BigInteger(gasPriceHex.removePrefix("0x"), 16)
        } catch (e: Exception) {
            BigInteger.valueOf(QubicConfig.DEFAULT_GAS_PRICE)
        }
    }
    
    /**
     * Get transaction count (nonce) for address
     */
    suspend fun getTransactionCount(address: String): BigInteger = withContext(Dispatchers.IO) {
        try {
            val response = makeRpcCall("qubic_getTransactionCount", listOf(address, "latest"))
            val nonceHex = response.getString("result")
            BigInteger(nonceHex.removePrefix("0x"), 16)
        } catch (e: Exception) {
            BigInteger.ZERO
        }
    }
    
    /**
     * Send a raw transaction to the network
     */
    suspend fun sendRawTransaction(signedTx: String): String = withContext(Dispatchers.IO) {
        val response = makeRpcCall("qubic_sendRawTransaction", listOf(signedTx))
        response.getString("result")
    }
    
    /**
     * Get transaction receipt
     */
    suspend fun getTransactionReceipt(txHash: String): TransactionReceipt? = withContext(Dispatchers.IO) {
        try {
            val response = makeRpcCall("qubic_getTransactionReceipt", listOf(txHash))
            val result = response.optJSONObject("result") ?: return@withContext null
            
            TransactionReceipt(
                transactionHash = result.getString("transactionHash"),
                blockNumber = result.getString("blockNumber"),
                status = result.getString("status") == "0x1",
                gasUsed = result.getString("gasUsed"),
                from = result.getString("from"),
                to = result.getString("to")
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Call a contract function (read-only)
     */
    suspend fun callContract(
        contractAddress: String,
        functionSignature: String,
        params: List<Any> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        val data = encodeFunctionCall(functionSignature, params)
        val callParams = mapOf(
            "to" to contractAddress,
            "data" to data
        )
        val response = makeRpcCall("qubic_call", listOf(callParams, "latest"))
        response.getString("result")
    }
    
    /**
     * Estimate gas for a transaction
     */
    suspend fun estimateGas(
        from: String,
        to: String,
        data: String,
        value: String = "0x0"
    ): BigInteger = withContext(Dispatchers.IO) {
        try {
            val params = mapOf(
                "from" to from,
                "to" to to,
                "data" to data,
                "value" to value
            )
            val response = makeRpcCall("qubic_estimateGas", listOf(params))
            val gasHex = response.getString("result")
            BigInteger(gasHex.removePrefix("0x"), 16)
        } catch (e: Exception) {
            BigInteger.valueOf(QubicConfig.DEFAULT_GAS_LIMIT)
        }
    }
    
    /**
     * Get block by number
     */
    suspend fun getBlockNumber(): BigInteger = withContext(Dispatchers.IO) {
        try {
            val response = makeRpcCall("qubic_blockNumber", emptyList())
            val blockHex = response.getString("result")
            BigInteger(blockHex.removePrefix("0x"), 16)
        } catch (e: Exception) {
            BigInteger.ZERO
        }
    }
    
    /**
     * Make RPC call to Qubic network
     */
    private fun makeRpcCall(method: String, params: List<Any>): JSONObject {
        val url = URL(rpcEndpoint)
        val connection = url.openConnection() as HttpURLConnection
        
        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            
            val requestBody = JSONObject().apply {
                put("jsonrpc", "2.0")
                put("method", method)
                put("params", params)
                put("id", 1)
            }
            
            connection.outputStream.use { os ->
                os.write(requestBody.toString().toByteArray())
            }
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                return JSONObject(response)
            } else {
                throw Exception("HTTP error code: $responseCode")
            }
        } finally {
            connection.disconnect()
        }
    }
    
    /**
     * Encode function call with parameters
     * Simplified version - in production, use proper ABI encoding
     */
    private fun encodeFunctionCall(signature: String, params: List<Any>): String {
        // This is a simplified version
        // In production, you'd use a proper Web3 library for ABI encoding
        val functionHash = signature.substring(0, 10) // First 4 bytes of keccak256
        // TODO: Implement proper parameter encoding based on types
        return functionHash
    }
    
    data class TransactionReceipt(
        val transactionHash: String,
        val blockNumber: String,
        val status: Boolean,
        val gasUsed: String,
        val from: String,
        val to: String
    )
}

