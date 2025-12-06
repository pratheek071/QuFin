# 🔗 Qubic Network Setup - Official Documentation

## 📚 Official Documentation Links

### **Primary Resources:**
- **Main Documentation:** https://docs.qubic.org
- **RPC API Documentation:** https://docs.qubic.org/api/rpc
- **Developer Portal:** https://docs.qubic.org/integration

---

## ⚙️ Official Network Configuration

### **Testnet (Use this for development):**
```
Network Name: Qubic Testnet
RPC URL: https://testnet-rpc.qubic.org
Chain ID: [Check https://docs.qubic.org/api/rpc]
Currency Symbol: QUBIC
Block Explorer: https://testnet.qubic.org
```

### **Mainnet (Production only):**
```
Network Name: Qubic Mainnet
RPC URL: https://rpc.qubic.org
Chain ID: [Check https://docs.qubic.org/api/rpc]
Currency Symbol: QUBIC
Block Explorer: https://explorer.qubic.org
```

---

## 🦊 MetaMask Configuration

### **Add Qubic Testnet to MetaMask:**

1. Open MetaMask extension
2. Click network dropdown (top left)
3. Click "Add network" → "Add a network manually"
4. Enter details:
   - **Network Name:** Qubic Testnet
   - **New RPC URL:** https://testnet-rpc.qubic.org
   - **Chain ID:** [Get from docs.qubic.org/api/rpc]
   - **Currency Symbol:** QUBIC
   - **Block Explorer URL:** https://testnet.qubic.org
5. Click "Save"
6. Switch to "Qubic Testnet"

---

## 💰 Getting Testnet Tokens

### **Option 1: Official Faucet**
- Check: https://docs.qubic.org (look for "Faucet" or "Testnet")
- Follow the instructions provided

### **Option 2: Discord Faucet**
1. Join official Qubic Discord (link on docs.qubic.org)
2. Find `#testnet-faucet` or `#faucet` channel
3. Request tokens with your wallet address
4. Wait for bot to send testnet QUBIC

### **Option 3: Developer Program**
- Check if Qubic has a developer grant program
- Apply for testnet tokens for hackathon

---

## 🔍 What to Look For in Documentation

### **Before Deploying Contracts:**
1. **Chain ID:** Exact number for testnet/mainnet
2. **Gas Configuration:**
   - Recommended gas limit
   - Gas price (in Gwei)
   - Transaction fees
3. **Smart Contract Requirements:**
   - Solidity version compatibility
   - Compiler settings
   - Deployment tools (Hardhat/Truffle config)

### **For API Integration:**
1. **Endpoints:**
   - REST API base URL
   - WebSocket URLs (for real-time data)
   - GraphQL endpoint (if available)
2. **Rate Limits:**
   - Requests per second/minute
   - Authentication requirements
3. **Supported Methods:**
   - eth_getBalance
   - eth_sendTransaction
   - eth_call (for contracts)

---

## 📱 Update Your App Configuration

Your `QubicConfig.kt` has been updated with official URLs:

```kotlin
object QubicConfig {
    // Official Qubic Endpoints
    const val QUBIC_TESTNET_RPC = "https://testnet-rpc.qubic.org"
    const val QUBIC_MAINNET_RPC = "https://rpc.qubic.org"
    
    const val CURRENT_NETWORK = QUBIC_TESTNET_RPC
    const val IS_TESTNET = true
}
```

### **After Checking Docs, Also Update:**
1. Chain ID (currently set to example values)
2. Gas limits and prices
3. Block confirmation times
4. Any network-specific parameters

---

## 🔗 Additional Resources

### **GitHub Repositories:**
- **Qubic Wallet:** https://github.com/qubic/wallet
  - Reference implementation for wallet interactions
  - See how they use RPC endpoints
- **Smart Contracts:** Check Qubic GitHub for examples

### **Community & Support:**
- **Official Website:** https://qubic.org
- **Discord:** Link on official website
- **Twitter/X:** Follow for updates
- **Medium/Blog:** Technical articles and announcements

---

## ✅ Setup Verification Checklist

```
Documentation Review:
[ ] Visited https://docs.qubic.org/api/rpc
[ ] Noted exact Chain ID for testnet
[ ] Noted exact Chain ID for mainnet
[ ] Checked gas price recommendations
[ ] Found faucet information

MetaMask Setup:
[ ] Installed MetaMask
[ ] Created wallet (seed phrase saved!)
[ ] Added Qubic Testnet network
[ ] Switched to Qubic Testnet
[ ] Copied wallet address

Testnet Tokens:
[ ] Requested tokens from faucet
[ ] Received testnet QUBIC
[ ] Verified balance in MetaMask
[ ] Tested sending small transaction

App Configuration:
[ ] Updated QubicConfig.kt with official values
[ ] Updated Chain ID
[ ] Updated gas configuration
[ ] Tested RPC connection
[ ] App compiles without errors

Smart Contracts (Next Steps):
[ ] Reviewed deployment guide in docs
[ ] Prepared Hardhat/Truffle config
[ ] Ready to deploy contracts
[ ] Know where to find contract addresses
```

---

## 🆘 Troubleshooting

### **Can't Find Information in Docs?**
1. Use site search: docs.qubic.org (search box)
2. Check "Getting Started" or "Quick Start" sections
3. Look in "Developer" or "Integration" sections
4. Ask in Discord #dev-help channel

### **RPC Connection Issues?**
1. Verify URL is correct (no typos)
2. Check network status: https://status.qubic.org (if available)
3. Try alternative RPC endpoints (docs may list multiple)
4. Check your internet/firewall settings

### **Faucet Not Working?**
1. Wait 24 hours (some faucets have cooldown)
2. Try different faucet (check Discord)
3. Verify wallet address is correct
4. Check if you're on correct network in MetaMask

---

## 🎯 Quick Actions

### **Right Now:**
1. **Visit:** https://docs.qubic.org/api/rpc
2. **Find:** Exact Chain ID for Qubic Testnet
3. **Update:** `QubicConfig.kt` with correct Chain ID
4. **Get:** Testnet tokens from faucet
5. **Test:** Connection in your app

### **Next:**
1. Deploy smart contracts to testnet
2. Update contract addresses in config
3. Test all app features
4. Prepare for hackathon demo

---

## 📞 Need Help?

- **Documentation:** https://docs.qubic.org
- **API Reference:** https://docs.qubic.org/api/rpc
- **Discord:** Official Qubic Discord (link on website)
- **Email:** Check official website for contact

---

## 🎉 Your Project Status

✅ **GitHub Repo:** https://github.com/pratheek071/QuFin
✅ **Code Pushed:** All files committed and pushed
✅ **Config Updated:** Using official Qubic RPC URLs
⏭️ **Next Step:** Get Chain ID from docs and set up MetaMask

**You're on the right track! Keep going!** 🚀

