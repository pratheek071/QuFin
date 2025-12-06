# 🚀 Complete Setup Guide - From Zero to Demo
## Qubic Finance: Everything You Need to Run Both Tracks

> **Starting from scratch? Perfect!** This guide assumes you have nothing installed and walks you through everything step-by-step.

---

## 📋 Table of Contents

1. [Prerequisites Installation](#1-prerequisites-installation)
2. [Project Setup](#2-project-setup)
3. [Quick Demo (No Blockchain)](#3-quick-demo-5-minutes)
4. [Full Blockchain Setup](#4-full-blockchain-setup-30-minutes)
5. [EasyConnect Integration](#5-easyconnect-integration-15-minutes)
6. [Testing Everything](#6-testing-everything)
7. [Demo Preparation](#7-demo-preparation)
8. [Troubleshooting](#8-troubleshooting)

---

## 1. Prerequisites Installation

### Step 1.1: Install Node.js (Required for Smart Contracts)

**Windows:**
1. Go to https://nodejs.org/
2. Download "LTS" version (v18 or higher)
3. Run installer, click "Next" through all steps
4. Verify installation:
```bash
node --version
npm --version
```
Should show v18.x.x or higher

**Mac:**
```bash
# Install Homebrew first (if not installed)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Node.js
brew install node

# Verify
node --version
npm --version
```

**Linux:**
```bash
# Ubuntu/Debian
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Verify
node --version
npm --version
```

---

### Step 1.2: Install Android Studio (Required for Mobile App)

**All Platforms:**
1. Go to https://developer.android.com/studio
2. Download Android Studio
3. Run installer
4. During setup, install:
   - Android SDK
   - Android SDK Platform (API 34)
   - Android Virtual Device (AVD)

5. Open Android Studio → More Actions → SDK Manager
6. Install:
   - Android SDK Platform 34
   - Android SDK Build-Tools 34.0.0
   - Android Emulator

7. Create an emulator:
   - Tools → Device Manager → Create Device
   - Select Pixel 5 or similar
   - Select System Image: Android 14 (API 34)
   - Finish

---

### Step 1.3: Install Git (If not installed)

**Windows:**
1. Download from https://git-scm.com/download/win
2. Run installer with default options

**Mac:**
```bash
brew install git
```

**Linux:**
```bash
sudo apt-get install git
```

Verify:
```bash
git --version
```

---

### Step 1.4: Get Qubic Testnet Wallet & Tokens

**Option A: Use MetaMask (Recommended)**
1. Install MetaMask browser extension
2. Create new wallet (SAVE YOUR SEED PHRASE!)
3. Add Qubic Testnet:
   - Network Name: Qubic Testnet
   - RPC URL: https://testnet.qubic.network/rpc
   - Chain ID: 12345 (example - check Qubic docs)
   - Currency: QUBIC
4. Copy your wallet address
5. Get testnet tokens from Qubic Discord faucet

**Option B: Create Wallet in App**
- You can also create a wallet directly in the app later

---

## 2. Project Setup

### Step 2.1: Navigate to Your Project

```bash
# Open terminal/command prompt
# Navigate to your project folder
cd "C:\Users\PratheekRaj(G10XIND)\AndroidStudioProjects\FinApp - Copy"

# Verify you're in the right place
dir   # Windows
ls    # Mac/Linux
```

You should see folders like: `app`, `contracts`, `gradle`, etc.

---

### Step 2.2: Install Contract Dependencies

```bash
# Go to contracts folder
cd contracts

# Install dependencies
npm install

# This will take 1-2 minutes
# You'll see progress bars

# Verify installation
npx hardhat --version
```

Should show: "Hardhat version 2.x.x"

---

## 3. Quick Demo (5 Minutes)

> **Want to see the UI immediately? Start here!**  
> No blockchain needed - perfect for quick testing.

### Step 3.1: Open Project in Android Studio

1. Open Android Studio
2. File → Open
3. Navigate to your project folder: `FinApp - Copy`
4. Click "OK"
5. Wait for Gradle sync (bottom right shows progress)
6. This takes 2-5 minutes first time

---

### Step 3.2: Start Emulator

1. In Android Studio, click device dropdown (top toolbar)
2. Select your emulator (e.g., "Pixel 5 API 34")
3. Click Run button (green play icon) or press Shift+F10
4. Emulator starts (takes 1-2 minutes first time)

**Alternative: Use Real Device**
1. Enable Developer Options on your Android phone:
   - Settings → About Phone → Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Settings → Developer Options → USB Debugging ON
3. Connect phone to computer via USB
4. Select your device in Android Studio

---

### Step 3.3: Run the App

```bash
# In project root (not in contracts folder)
cd ..

# Clean build (first time)
./gradlew clean

# Build and install
./gradlew installDebug
```

**Windows PowerShell:**
```powershell
.\gradlew.bat clean
.\gradlew.bat installDebug
```

**What happens:**
- App builds (2-3 minutes first time)
- Installs on emulator/device
- App opens automatically

---

### Step 3.4: Explore the Demo

The app runs with **mock data** - no blockchain needed!

**Try these features:**
1. **Home Screen** - See app layout
2. **Create Wallet** - Generates test wallet (not on blockchain)
3. **Loans** - View sample loan data
4. **Analytics** - See dashboard with demo stats
5. **Navigation** - Explore all screens

**✅ Success!** You've seen the complete UI!

---

## 4. Full Blockchain Setup (30 Minutes)

> **Now let's connect to real blockchain!**

### Step 4.1: Get Your Private Key

**From MetaMask:**
1. Open MetaMask
2. Click menu (3 dots) → Account Details
3. Click "Export Private Key"
4. Enter password
5. **COPY THE PRIVATE KEY** (starts with 0x...)
6. ⚠️ **NEVER SHARE THIS KEY!**

**Or Generate New Key:**
```bash
cd contracts
npx hardhat run scripts/generateKey.js
```
Save the output.

---

### Step 4.2: Configure Environment

**Windows:**
```powershell
# Set private key (replace with YOUR key)
$env:PRIVATE_KEY="0xYOUR_PRIVATE_KEY_HERE"

# Verify it's set
echo $env:PRIVATE_KEY
```

**Mac/Linux:**
```bash
# Set private key (replace with YOUR key)
export PRIVATE_KEY="0xYOUR_PRIVATE_KEY_HERE"

# Verify it's set
echo $PRIVATE_KEY
```

**Or create .env file:**
```bash
# In contracts folder, create .env file
cd contracts
echo "PRIVATE_KEY=0xYOUR_PRIVATE_KEY_HERE" > .env
```

---

### Step 4.3: Deploy Smart Contracts

```bash
# Make sure you're in contracts folder
cd contracts

# Compile contracts
npx hardhat compile

# Should see: "Compiled X Solidity files successfully"

# Deploy to Qubic Testnet
npx hardhat run scripts/deploy.js --network qubicTestnet
```

**⏱️ Takes 2-3 minutes**

**Expected Output:**
```
Deploying contracts...
QFIN Token deployed to: 0xABC123...
Collateral Manager deployed to: 0xDEF456...
Lending Pool deployed to: 0xGHI789...

✅ Deployment complete!
```

**📝 COPY THESE ADDRESSES!** You'll need them next.

---

### Step 4.4: Update App Configuration

1. **Open in Android Studio:**
   - File → Project
   - Navigate to: `app/src/main/java/com/example/finapp/blockchain/QubicConfig.kt`

2. **Find these lines (around line 5-10):**
```kotlin
const val QFIN_TOKEN_ADDRESS = "0x..."
const val LENDING_POOL_ADDRESS = "0x..."
const val COLLATERAL_MANAGER_ADDRESS = "0x..."
```

3. **Replace with YOUR deployed addresses:**
```kotlin
const val QFIN_TOKEN_ADDRESS = "0xABC123..."  // Your QFIN address
const val LENDING_POOL_ADDRESS = "0xGHI789..."  // Your LendingPool address
const val COLLATERAL_MANAGER_ADDRESS = "0xDEF456..."  // Your CollateralManager address
```

4. **Save the file** (Ctrl+S / Cmd+S)

---

### Step 4.5: Rebuild and Run

```bash
# Clean previous build
./gradlew clean

# Build with new configuration
./gradlew assembleDebug

# Install on device
./gradlew installDebug
```

**Windows:**
```powershell
.\gradlew.bat clean
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

---

### Step 4.6: Test Blockchain Features

**In the app:**

1. **Create/Import Wallet**
   - If you have MetaMask wallet, import it
   - Or create new wallet (save private key!)

2. **Get Testnet Tokens**
   - Copy your wallet address
   - Go to Qubic Discord
   - Request testnet tokens in faucet channel

3. **Check Balance**
   - Should show your QUBIC balance
   - Click refresh if needed

4. **Apply for Loan**
   - Go to "Loans" tab
   - Click "Apply for Loan"
   - Select collateral type (QBTC)
   - Enter amounts
   - Submit!
   - See transaction hash

5. **View Analytics**
   - Go to "Analytics" tab
   - See real protocol stats
   - View your portfolio

**✅ Success!** You're now on the blockchain!

---

## 5. EasyConnect Integration (15 Minutes)

> **Add automated notifications and analytics!**

### Step 5.1: Sign Up for EasyConnect

1. **Go to:** https://easyconnect.qubic.org
2. **Click:** "Sign Up" or "Connect Wallet"
3. **Options:**
   - Connect with MetaMask wallet, OR
   - Create account with email
4. **Save your API key** (shown after signup)

---

### Step 5.2: Add Your Contract

1. **In EasyConnect Dashboard:**
   - Click "+ New Integration"
   - Select "Qubic Blockchain"

2. **Enter Details:**
   - **Network:** Qubic Testnet
   - **Contract Address:** YOUR_LENDING_POOL_ADDRESS (from Step 4.3)
   - **Contract Name:** "Qubic Finance Lending Pool"

3. **Select Events to Monitor:**
   - ✅ LoanCreated
   - ✅ LoanApproved
   - ✅ PaymentMade
   - ✅ LoanCreatedDetailed (Enhanced)
   - ✅ PaymentMadeDetailed (Enhanced)
   - ✅ HealthFactorAlert (Enhanced)

4. **Click:** "Add Integration"

---

### Step 5.3: Setup Discord Notifications

**A. Create Discord Server (if you don't have one)**
1. Open Discord
2. Click "+"
3. "Create My Own"
4. "For me and my friends"
5. Name it "Qubic Finance"

**B. Create Webhook**
1. Right-click your server → Server Settings
2. Integrations → Webhooks
3. Click "New Webhook"
4. Name: "Qubic Finance Bot"
5. Select channel: #general (or create #notifications)
6. **Click "Copy Webhook URL"**
7. Save → Done

**C. Add to EasyConnect**
1. In EasyConnect, go to "Workflows"
2. Click "Create New Workflow"
3. Or click "Import Workflow"

---

### Step 5.4: Import Pre-Made Workflows

**Method 1: Import JSON Files**

1. **In EasyConnect Dashboard:**
   - Click "Workflows" → "Import"

2. **Import these files one by one:**
   - `easyconnect-templates/loan-approval-notification.json`
   - `easyconnect-templates/payment-reminder.json`
   - `easyconnect-templates/liquidation-alert.json`

3. **For each workflow:**
   - Click "Import"
   - Browse to file
   - Click "Open"
   - **IMPORTANT:** Update these fields:
     - `LENDING_POOL_ADDRESS` → Your actual address
     - `YOUR_DISCORD_WEBHOOK_URL` → Your webhook from Step 5.3
     - `YOUR_TELEGRAM_BOT_TOKEN` → (Optional, leave blank for now)
   - Click "Save"
   - Toggle to "Active"

---

**Method 2: Create Manually**

**Workflow 1: Loan Approval Notification**

1. **Click:** "Create Workflow"
2. **Name:** "Loan Approval Notification"
3. **Trigger:**
   - Type: Blockchain Event
   - Contract: YOUR_LENDING_POOL_ADDRESS
   - Event: LoanApproved
4. **Action:**
   - Type: Discord Webhook
   - Webhook URL: YOUR_DISCORD_WEBHOOK_URL
   - Message Template:
   ```json
   {
     "embeds": [{
       "title": "🎉 Loan Approved!",
       "description": "A loan has been approved",
       "color": 3066993,
       "fields": [
         { "name": "Loan ID", "value": "${event.loanId}", "inline": true },
         { "name": "Amount", "value": "${event.amount} QUBIC", "inline": true }
       ]
     }]
   }
   ```
5. **Save & Activate**

---

### Step 5.5: Setup Google Sheets (Optional)

**A. Create Google Sheet**
1. Go to Google Sheets
2. Create new sheet: "Qubic Finance Analytics"
3. Add headers in Row 1:
   - A1: Timestamp
   - B1: Event Type
   - C1: Loan ID
   - D1: Amount
   - E1: Status

**B. Get Sheet ID**
- From URL: `https://docs.google.com/spreadsheets/d/SHEET_ID_HERE/edit`
- Copy the SHEET_ID_HERE part

**C. Connect to EasyConnect**
1. In EasyConnect: Settings → Integrations
2. Add Google Sheets
3. Authorize with Google account
4. Enter Sheet ID

**D. Import Analytics Workflow**
- Import: `easyconnect-templates/analytics-tracker.json`
- Update Sheet ID
- Activate

---

### Step 5.6: Setup Telegram (Optional)

**A. Create Telegram Bot**
1. Open Telegram
2. Search for "@BotFather"
3. Send: `/newbot`
4. Follow prompts:
   - Bot name: "Qubic Finance Bot"
   - Username: "qubic_finance_bot" (or similar)
5. **Copy the bot token** (looks like: 123456:ABC-DEF...)

**B. Get Your Chat ID**
1. Search for "@userinfobot"
2. Send: `/start`
3. Copy your chat ID (number)

**C. Add to EasyConnect**
- In workflows, add Telegram action
- Bot Token: Your bot token
- Chat ID: Your chat ID

---

### Step 5.7: Test Everything!

**Test 1: Loan Approval Notification**
1. In EasyConnect, go to workflow
2. Click "Test"
3. Use sample data:
```json
{
  "loanId": "123",
  "amount": "5000"
}
```
4. Click "Run Test"
5. **Check Discord** - you should see notification!

**Test 2: Live Test**
1. In your app, create a test loan
2. Wait 10-30 seconds
3. **Check Discord** - notification arrives!
4. **Check Google Sheets** - new row added!
5. **Check EasyConnect logs** - event received!

---

## 6. Testing Everything

### Test Checklist

**Mobile App:**
```
✅ Wallet
  [ ] App opens without crashing
  [ ] Can create new wallet
  [ ] Can import existing wallet
  [ ] Balance shows correctly
  [ ] Wallet address displays

✅ Loans
  [ ] Can navigate to loan screen
  [ ] Can select collateral type
  [ ] Can enter loan amounts
  [ ] Submit button works
  [ ] Transaction hash displays

✅ Analytics
  [ ] Analytics screen loads
  [ ] Protocol stats display
  [ ] User portfolio shows
  [ ] Health factor visible
  [ ] Refresh button works

✅ Navigation
  [ ] All tabs work
  [ ] Back button functions
  [ ] No crashes
```

**Blockchain:**
```
✅ Smart Contracts
  [ ] Contracts deployed successfully
  [ ] Addresses saved and updated in app
  [ ] Can call contract functions
  [ ] Transactions confirm
  [ ] Events emit correctly
```

**EasyConnect:**
```
✅ Integration
  [ ] EasyConnect account created
  [ ] Contract added to dashboard
  [ ] At least 1 workflow active
  [ ] Test notification received
  [ ] Live notification received
  [ ] Logs show events
```

---

## 7. Demo Preparation

### 7.1: Prepare Your Device

**Before Demo:**
1. **Charge device/laptop** to 100%
2. **Clear app data** for fresh demo
3. **Clear Discord channel** (delete old messages)
4. **Prepare MetaMask** with testnet funds
5. **Test internet connection**

---

### 7.2: 2-Minute Demo Script

**[0:00-0:20] Introduction**
```
"This is Qubic Finance - a complete DeFi lending platform for mobile.

It's unique because it's mobile-first on the Qubic network, and integrates 
both Nostromo Launchpad (Track 1) and EasyConnect automation (Track 2)."
```

**[0:20-0:40] Show Wallet**
```
[Open app]
"Here's the wallet dashboard. Users can securely create or import wallets 
using Android KeyStore encryption."

[Click Create Wallet / Show existing]
"Wallet address, QUBIC balance, and quick actions - all accessible."
```

**[0:40-1:00] Demonstrate Loan**
```
[Navigate to Loans → Apply for Loan]
"Users can apply for loans with crypto collateral. Choose collateral type 
- Bitcoin, Ethereum, stablecoins - enter amounts..."

[Submit transaction]
"Transaction sent to Qubic blockchain. Here's the transaction hash."
```

**[1:00-1:20] Show Analytics**
```
[Navigate to Analytics tab]
"Real-time analytics dashboard shows protocol stats - Total Value Locked,
active loans, utilization rate...

And user-specific metrics - health factor, collateral value, credit score,
QFIN rewards earned."
```

**[1:20-1:40] EasyConnect (if set up)**
```
[Switch to Discord on another screen/device]
"And here's the EasyConnect integration - automated notification just 
appeared in Discord.

This same event also updates Google Sheets, can send Telegram alerts,
emails - all no-code automation."
```

**[1:40-2:00] Summary**
```
"So, complete DeFi lending on mobile (Track 1), with full no-code automation 
(Track 2), production-ready code, comprehensive documentation.

Questions?"
```

---

### 7.3: Backup Plan

**Prepare these in case of issues:**

1. **Screen Recording**
   - Record working demo in advance
   - Use if live demo fails

2. **Screenshots**
   - Take screenshots of every screen
   - Put in PowerPoint/Google Slides

3. **Multiple Devices**
   - Test on 2 devices
   - Use backup if primary fails

4. **APK File**
   - Build APK: `./gradlew assembleDebug`
   - Share via USB/Airdrop
   - Found in: `app/build/outputs/apk/debug/app-debug.apk`

---

## 8. Troubleshooting

### Problem: `./gradlew: Permission denied`

**Solution (Mac/Linux):**
```bash
chmod +x gradlew
./gradlew clean
```

---

### Problem: `ANDROID_HOME not set`

**Solution:**

**Windows:**
```powershell
$env:ANDROID_HOME="C:\Users\YourName\AppData\Local\Android\Sdk"
```

**Mac:**
```bash
export ANDROID_HOME=$HOME/Library/Android/sdk
```

**Linux:**
```bash
export ANDROID_HOME=$HOME/Android/Sdk
```

Add to permanent PATH (Google: "set ANDROID_HOME permanently [your OS]")

---

### Problem: Contract deployment fails

**Check:**
1. **Private key set?**
   ```bash
   echo $PRIVATE_KEY  # Should show your key
   ```

2. **Wallet has funds?**
   - Check balance in MetaMask
   - Get testnet tokens from faucet

3. **Network configured?**
   - Check `contracts/hardhat.config.js`
   - Verify RPC URL is correct

**Try with verbose logging:**
```bash
npx hardhat run scripts/deploy.js --network qubicTestnet --verbose
```

---

### Problem: App won't build

**Solution:**
```bash
# Clean everything
./gradlew clean
rm -rf app/build/
rm -rf .gradle/

# Rebuild
./gradlew assembleDebug
```

**If still fails:**
1. File → Invalidate Caches (in Android Studio)
2. Restart Android Studio
3. Try again

---

### Problem: App crashes on launch

**Check logs:**
```bash
adb logcat | grep -E "FinApp|AndroidRuntime"
```

**Common causes:**
- Missing dependency (check build.gradle.kts)
- Incorrect configuration (check QubicConfig.kt)
- Missing permissions (check AndroidManifest.xml)

---

### Problem: EasyConnect not triggering

**Check:**
1. **Workflow is Active?**
   - Toggle should be green/on
2. **Contract address correct?**
   - Copy-paste carefully
3. **Event name matches?**
   - Check spelling exactly
4. **Network correct?**
   - Should be "Qubic Testnet"

**Test manually:**
- Use "Test" button with sample data
- Check logs for errors

---

### Problem: No Discord notifications

**Check:**
1. **Webhook URL correct?**
   - Should start with `https://discord.com/api/webhooks/`
2. **Channel exists?**
   - Webhook channel wasn't deleted?
3. **Bot has permissions?**
   - Check server settings

**Test webhook directly:**
```bash
curl -X POST "YOUR_WEBHOOK_URL" \
  -H "Content-Type: application/json" \
  -d '{"content":"Test message"}'
```

Should see "Test message" in Discord.

---

### Problem: Emulator is slow

**Solutions:**
1. **Increase RAM:**
   - AVD Manager → Edit device → Advanced
   - RAM: 4096 MB or higher

2. **Enable hardware acceleration:**
   - BIOS settings → Enable VT-x/AMD-V

3. **Use real device instead:**
   - Much faster!

---

## 9. Quick Command Reference

### Essential Commands

```bash
# Navigate to project
cd "C:\Users\PratheekRaj(G10XIND)\AndroidStudioProjects\FinApp - Copy"

# Deploy contracts
cd contracts
npx hardhat run scripts/deploy.js --network qubicTestnet

# Build and install app
cd ..
./gradlew clean assembleDebug installDebug

# View logs
adb logcat | grep FinApp

# Check connected devices
adb devices
```

---

## 10. What You've Accomplished! 🎉

After following this guide, you have:

✅ **Complete DeFi lending platform**
- Mobile app running
- Smart contracts deployed
- Real blockchain transactions

✅ **Track 1 - Nostromo Launchpad**
- DeFi protocol ✓
- Mobile app ✓
- Documentation ✓

✅ **Track 2 - EasyConnect**
- Automated workflows ✓
- Multi-platform notifications ✓
- No-code integration ✓

✅ **Ready to win!**
- Working demo ✓
- Complete solution ✓
- Both tracks ✓

---

## 🎯 Final Checklist Before Submission

```
📱 Mobile App
  [✓] Builds successfully
  [✓] Wallet works
  [✓] Loans work
  [✓] Analytics displays
  [✓] Navigation smooth

⛓️ Blockchain
  [✓] Contracts deployed
  [✓] Addresses configured
  [✓] Transactions work
  [✓] Events emit

🔗 EasyConnect
  [✓] Account created
  [✓] Contract integrated
  [✓] 1+ workflow active
  [✓] Notifications work

📝 Demo
  [✓] Script prepared
  [✓] Device ready
  [✓] Backup plan ready
  [✓] Confident!
```

---

## 🏆 You're Ready to Win!

Everything is set up. Everything works. You have:
- Complete mobile DeFi platform ✓
- Real blockchain integration ✓
- Automated notifications ✓
- Both hackathon tracks ✓
- Professional documentation ✓

**Now go present and WIN! 🚀**

---

## 📞 Need More Help?

### Documentation
- **Quick commands:** [QUICK_REFERENCE.md](./QUICK_REFERENCE.md)
- **Project overview:** [START_HERE.md](./START_HERE.md)
- **Detailed deployment:** [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md)
- **For judges:** [QUBIC_HACKATHON_SUBMISSION.md](./QUBIC_HACKATHON_SUBMISSION.md)

### Still Stuck?
- Re-read relevant section above
- Check troubleshooting section
- Google specific error messages
- All answers are in the documentation!

---

<div align="center">

# 🎉 SETUP COMPLETE! 🎉

**You're ready to demo and win the hackathon!**

**Built with ❤️ for Qubic Ecosystem**

*Good luck! You've got this!* 🚀

</div>

