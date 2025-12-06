# ⚡ Quick Reference Card
## Qubic Finance - Commands & Info At A Glance

---

## 🚀 Quick Start Commands

### Run App (Demo Mode)
```bash
./gradlew installDebug
```

### Deploy Contracts
```bash
cd contracts
npm install
export PRIVATE_KEY="your_key"
npx hardhat run scripts/deploy.js --network qubicTestnet
```

### Build APK
```bash
./gradlew clean assembleDebug
```

### View Logs
```bash
adb logcat | grep FinApp
```

---

## 📂 Key Files to Edit

### Configure Blockchain
```
app/src/main/java/com/example/finapp/blockchain/QubicConfig.kt
```
Update: Contract addresses, RPC URL, Chain ID

### Deploy Contracts
```
contracts/hardhat.config.js
```
Update: Private key, network settings

### App Entry Point
```
app/src/main/AndroidManifest.xml
```

---

## 📱 App Structure

```
Wallet → Create/Import wallet
  ├── WalletConnectionFragment
  └── WalletDashboardFragment

Loans → Apply & manage loans
  ├── CryptoLoanViewModel
  └── LoanApplicationFragment

Payments → Make crypto payments
  └── CryptoPaymentViewModel

Analytics → View stats
  ├── AnalyticsViewModel
  └── AnalyticsFragment
```

---

## 🔗 Contract Addresses (Update After Deploy)

```kotlin
// In QubicConfig.kt
const val QFIN_TOKEN = "0x..."
const val LENDING_POOL = "0x..."
const val COLLATERAL_MANAGER = "0x..."
```

---

## 🎯 Important URLs

- Qubic Testnet: `https://testnet.qubic.network/rpc`
- EasyConnect: `https://easyconnect.qubic.org`
- Qubic Explorer: `https://explorer.qubic.org`

---

## 🔑 Key Features

### Collateral Types
- QBTC (Bitcoin on Qubic)
- QETH (Ethereum on Qubic)
- QUSD (Stablecoin)
- QBN (Qubic Native)

### Loan Types
- Education (3% interest)
- Personal (5% interest)
- Home (2.5% interest)
- Car (4% interest)
- Collateralized (1.5% interest)

### Risk Parameters
- Liquidation Threshold: 120%
- Healthy: >150%
- Warning: 120-150%
- Critical: <120%

---

## 🛠️ Troubleshooting

### Build Fails
```bash
./gradlew clean
rm -rf app/build/
./gradlew assembleDebug
```

### Contract Deploy Fails
```bash
# Check balance
# Verify network
# Check gas price
npx hardhat run scripts/deploy.js --network qubicTestnet --verbose
```

### App Crashes
```bash
adb logcat | grep -E "FinApp|AndroidRuntime"
```

### Wallet Won't Connect
1. Check contract addresses in QubicConfig.kt
2. Verify RPC endpoint is accessible
3. Restart app

---

## 📊 Documentation Map

| Need | Read This |
|------|----------|
| **Quick start** | [SIMPLE_EXECUTION_GUIDE.md](./SIMPLE_EXECUTION_GUIDE.md) |
| **Full deploy** | [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md) |
| **Hackathon submit** | [QUBIC_HACKATHON_SUBMISSION.md](./QUBIC_HACKATHON_SUBMISSION.md) |
| **EasyConnect** | [EASYCONNECT_INTEGRATION.md](./EASYCONNECT_INTEGRATION.md) |
| **Technical specs** | [PROJECT_SUMMARY.md](./PROJECT_SUMMARY.md) |
| **Project overview** | [START_HERE.md](./START_HERE.md) |

---

## 🎬 2-Minute Demo Script

```
0:00 - Intro: "Qubic Finance - Mobile DeFi Lending"
0:15 - Show: Wallet creation
0:30 - Show: Loan application with collateral
0:45 - Show: Transaction on blockchain
1:00 - Show: Analytics dashboard
1:15 - Show: Discord notification (if EasyConnect)
1:30 - Summary: "Complete solution, both tracks"
1:45 - Questions
```

---

## ⚙️ Environment Variables

```bash
# Required for deployment
export PRIVATE_KEY="your_testnet_private_key"

# Optional for local testing
export QUBIC_RPC_URL="https://testnet.qubic.network/rpc"
```

---

## 📈 Smart Contract Events

### For EasyConnect Integration

```solidity
LoanCreatedDetailed     // New loan created
PaymentMadeDetailed     // Payment processed
HealthFactorAlert       // Risk warning
MilestoneReached        // User achievement
WhaleLoanActivity       // Large loan activity
```

---

## 🎁 EasyConnect Workflows

Location: `easyconnect-templates/`

1. **loan-approval-notification.json** - Discord alerts
2. **payment-reminder.json** - Daily reminders
3. **liquidation-alert.json** - Multi-channel warnings
4. **analytics-tracker.json** - Google Sheets auto-update

To use: Import in EasyConnect dashboard

---

## 🧪 Test Checklist

```
✅ App
  [ ] Builds successfully
  [ ] Wallet creation works
  [ ] Transactions work
  [ ] Analytics displays

✅ Contracts
  [ ] Deploy successfully
  [ ] Functions work
  [ ] Events emit

✅ EasyConnect
  [ ] Workflow active
  [ ] Notifications arrive
  [ ] Tested
```

---

## 🎯 Submission Checklist

```
✅ Code
  [ ] All files committed
  [ ] Builds successfully
  [ ] No critical errors

✅ Docs
  [ ] README complete
  [ ] Guides included
  [ ] Examples provided

✅ Demo
  [ ] Script prepared
  [ ] APK ready
  [ ] Tested
```

---

## 💡 Pro Tips

### For Speed
- Use demo mode first (no blockchain needed)
- Deploy contracts once, reuse addresses
- Keep testnet private key safe

### For Demo
- Test on real device (more impressive)
- Record screen as backup
- Have Discord open (show notifications)

### For Winning
- Emphasize completeness
- Show both tracks integrated
- Highlight mobile-first approach

---

## 🆘 Emergency Commands

### App won't run
```bash
adb devices                    # Check device connected
adb uninstall com.example.finapp  # Remove old version
./gradlew installDebug         # Fresh install
```

### Need to reset
```bash
./gradlew clean
rm -rf ~/.gradle/caches/
./gradlew assembleDebug
```

### View all logs
```bash
adb logcat -c                  # Clear logs
adb logcat *:E                 # Errors only
```

---

## 📞 Quick Links

- **Qubic Docs**: https://docs.qubic.org
- **EasyConnect**: https://easyconnect.qubic.org
- **Hardhat Docs**: https://hardhat.org/docs
- **Android Docs**: https://developer.android.com

---

## ✅ Final Check Before Demo

```
Hardware:
  [ ] Device/emulator running
  [ ] Battery charged
  [ ] Good internet connection

Software:
  [ ] App installed and tested
  [ ] Contracts deployed
  [ ] Config updated

Demo:
  [ ] Script memorized
  [ ] Screenshots ready
  [ ] Backup plan prepared
```

---

## 🎊 Success!

You're ready to present. Just:
1. Practice demo twice
2. Test all features
3. Have confidence
4. Show what you built!

**Go win! 🏆**

---

<div align="center">

**Qubic Finance - Built with ❤️**

[Full Guide](./SIMPLE_EXECUTION_GUIDE.md) • [Deploy](./DEPLOYMENT_GUIDE.md) • [Submit](./QUBIC_HACKATHON_SUBMISSION.md)

</div>

