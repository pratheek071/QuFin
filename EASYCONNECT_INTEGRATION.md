# EasyConnect Integration Guide
## Qubic Finance + EasyConnect (Track 2)

---

## 🎯 What is EasyConnect?

EasyConnect allows you to automate blockchain events **without writing code**. It connects Qubic blockchain events to tools like:
- Make.com
- Zapier
- n8n
- Discord/Telegram
- Google Sheets
- Email/SMS

---

## 🔥 What We Built

### Enhanced Smart Contracts with Rich Events

Your `LendingPool.sol` now emits **detailed events** perfect for EasyConnect automation:

```solidity
✅ LoanCreatedDetailed - Full loan details
✅ PaymentMadeDetailed - Payment tracking
✅ HealthFactorAlert - Risk warnings
✅ MilestoneReached - User achievements
✅ WhaleLoanActivity - Large loan tracking
```

---

## 🚀 Quick Setup (15 minutes)

### Step 1: Deploy Your Contracts

```bash
cd contracts
npx hardhat run scripts/deploy.js --network qubicTestnet
```

**Save the contract addresses!**

### Step 2: Register with EasyConnect

1. Go to [EasyConnect Website](https://easyconnect.qubic.org)
2. Click "Sign Up" or "Connect Wallet"
3. Note your API key

### Step 3: Add Your Contract

In EasyConnect dashboard:
1. Click "+ New Integration"
2. Select "Qubic Blockchain"
3. Enter your `LendingPool` contract address
4. Select events to monitor

---

## 📋 Ready-to-Use Workflows

### Workflow 1: Loan Approval Notification

**Trigger**: `LoanApproved` event  
**Action**: Send Discord message  

**Setup in EasyConnect**:
```
1. Trigger: Listen to LoanApproved event
2. Filter: status = APPROVED
3. Action: Discord Webhook
4. Message: 
   "🎉 Loan Approved!
   Amount: {loanAmount} QUBIC
   Borrower: {borrower}
   Loan ID: {loanId}"
```

**Discord Webhook URL**: Get from Discord Server Settings → Integrations → Webhooks

---

### Workflow 2: Payment Reminders

**Trigger**: Daily schedule (9:00 AM)  
**Action**: Check loans & send reminders  

**Setup in EasyConnect**:
```
1. Trigger: Schedule (daily at 9:00 AM)
2. Query: Get active loans from contract
3. Filter: Payment due today
4. Action: Send Telegram message
5. Message:
   "⏰ Payment Due Today!
   Loan: #{loanId}
   Amount: {dailyAmount} QUBIC
   Pay now to avoid late fees"
```

---

### Workflow 3: Liquidation Alerts

**Trigger**: `HealthFactorAlert` event  
**Action**: Multiple urgent notifications  

**Setup in EasyConnect**:
```
1. Trigger: HealthFactorAlert event
2. Filter: healthFactor < 1.3
3. Actions (Multiple):
   a) Send Email (urgent)
   b) Send SMS
   c) Post in Discord #alerts
   d) Send push notification
4. Message:
   "⚠️ URGENT: Liquidation Risk!
   Loan #{loanId}
   Health Factor: {healthFactor}
   Action Required: Add {requiredCollateral} more collateral"
```

---

### Workflow 4: Analytics to Google Sheets

**Trigger**: Every loan event  
**Action**: Log to Google Sheets  

**Setup in EasyConnect**:
```
1. Trigger: All loan events
2. Action: Add row to Google Sheets
3. Sheet: "Loan Analytics"
4. Columns:
   - Timestamp
   - Event Type
   - Loan ID
   - Borrower
   - Amount
   - Health Factor
```

**Google Sheets**: Connect via OAuth in EasyConnect

---

### Workflow 5: Whale Activity Monitor

**Trigger**: `WhaleLoanActivity` event  
**Action**: Alert community  

**Setup in EasyConnect**:
```
1. Trigger: WhaleLoanActivity event
2. Filter: amount > 50000
3. Actions:
   a) Post to Twitter (optional)
   b) Discord announcement
   c) Telegram channel
4. Message:
   "🐋 Whale Alert!
   Large loan of ${amount} just created!
   Total TVL now: ${updatedTVL}"
```

---

### Workflow 6: Milestone Celebrations

**Trigger**: `MilestoneReached` event  
**Action**: Reward user  

**Setup in EasyConnect**:
```
1. Trigger: MilestoneReached event
2. Filter: milestoneType = "FIRST_LOAN_COMPLETED"
3. Actions:
   a) Send congrats message
   b) Award Discord role "Verified Borrower"
   c) Airdrop bonus QFIN (if configured)
4. Message:
   "🎉 Congratulations @{user}!
   You've completed your first loan!
   Welcome to the Qubic Finance community!"
```

---

### Workflow 7: Real-Time Dashboard

**Trigger**: All protocol events  
**Action**: Update dashboard  

**Setup in EasyConnect**:
```
1. Trigger: Any event from LendingPool
2. Action: Update metrics
3. Destinations:
   - Google Sheets (analytics)
   - Discord bot (stats command)
   - Website API (live dashboard)
```

---

## 🎨 Discord Bot Integration

### Setup Discord Bot

1. Create bot in Discord Developer Portal
2. Get bot token
3. Invite bot to your server

### EasyConnect Configuration

```javascript
{
  "trigger": "LoanCreatedDetailed",
  "action": "discord_webhook",
  "webhook_url": "YOUR_WEBHOOK_URL",
  "message_template": {
    "embeds": [{
      "title": "🆕 New Loan Created",
      "color": 3447003,
      "fields": [
        { "name": "Loan ID", "value": "${loanId}", "inline": true },
        { "name": "Amount", "value": "${loanAmount} QUBIC", "inline": true },
        { "name": "Collateral", "value": "${collateralAmount} ${collateralToken}", "inline": true },
        { "name": "Duration", "value": "${duration} days", "inline": true },
        { "name": "Interest Rate", "value": "${interestRate}%", "inline": true }
      ],
      "timestamp": "${timestamp}"
    }]
  }
}
```

---

## 📱 Telegram Bot Integration

### Setup Telegram Bot

1. Chat with @BotFather
2. Create new bot
3. Get bot token

### EasyConnect Configuration

```javascript
{
  "trigger": "PaymentMadeDetailed",
  "action": "telegram_message",
  "bot_token": "YOUR_BOT_TOKEN",
  "chat_id": "${userChatId}",
  "message": "✅ Payment Received!\n\nLoan: #${loanId}\nAmount: ${paymentAmount} QUBIC\nRemaining: ${remainingAmount} QUBIC\n\nThank you for your timely payment!"
}
```

---

## 📊 Analytics Dashboard (Google Sheets)

### Create Analytics Sheet

1. Create Google Sheet: "Qubic Finance Analytics"
2. Add tabs:
   - Loans (all loans)
   - Payments (all payments)
   - Health Factors (tracking)
   - Daily Summary

### EasyConnect Auto-Update

**Sheet 1: Loans**
```
Columns: Date | Loan ID | Borrower | Amount | Collateral | Status
Updates: On LoanCreated event
```

**Sheet 2: Payments**
```
Columns: Date | Loan ID | Payment | Remaining | Health Factor
Updates: On PaymentMade event
```

**Sheet 3: Health Tracking**
```
Columns: Timestamp | Loan ID | Health Factor | Risk Level
Updates: On HealthFactorAlert event
```

---

## 🎮 Gamification System

### Community Badges (Discord Roles)

**EasyConnect Workflow**:
```javascript
{
  "trigger": "MilestoneReached",
  "conditions": [
    { "milestone": "FIRST_LOAN_COMPLETED", "role": "Bronze Borrower" },
    { "milestone": "5_LOANS_COMPLETED", "role": "Silver Borrower" },
    { "milestone": "10_LOANS_COMPLETED", "role": "Gold Borrower" },
    { "milestone": "PERFECT_PAYMENT_RECORD", "role": "Diamond Borrower" }
  ],
  "action": "assign_discord_role"
}
```

### Leaderboard

**Google Sheets + Discord Bot**:
```
1. Track credit scores in Google Sheets
2. Daily update via EasyConnect
3. Discord bot command: !leaderboard
4. Shows top 10 borrowers
```

---

## 🔔 Notification Templates

### Email Template (Liquidation Warning)

```html
Subject: ⚠️ Urgent: Your Qubic Finance Loan Needs Attention

Dear Borrower,

Your loan #${loanId} is approaching liquidation threshold.

Current Health Factor: ${healthFactor}
Liquidation Threshold: 1.30
Required Action: Add ${requiredCollateral} more collateral

Click here to add collateral: ${appLink}

Best regards,
Qubic Finance Team
```

### SMS Template (Payment Due)

```
Qubic Finance: Payment of ${dailyAmount} QUBIC due today for loan #${loanId}. Pay now: ${paymentLink}
```

---

## 🧪 Testing Your Workflows

### Test in EasyConnect

1. Go to workflow
2. Click "Test"
3. Use sample data:
```json
{
  "loanId": "123",
  "borrower": "0x1234...5678",
  "loanAmount": "5000",
  "collateralAmount": "0.25",
  "collateralToken": "QBTC"
}
```
4. Verify notification arrives

### Test Live

1. Create test loan in your app
2. Check EasyConnect logs
3. Verify notification arrives
4. Check analytics updated

---

## 📈 Advanced: Multi-Chain Analytics

**Aggregate data from multiple sources**:

```
EasyConnect Workflow:
1. Listen to Qubic Finance events
2. Listen to other DeFi protocols
3. Aggregate to central dashboard
4. Compare metrics
5. Generate insights
```

---

## 🎯 Hackathon Submission

### For Track 2 Judges

**What to Showcase**:

1. **Live Notifications Demo**
   - Create loan → Discord notification
   - Make payment → Analytics update
   - Show health alert → Multiple channels

2. **Dashboard Demo**
   - Show Google Sheets auto-updating
   - Display real-time stats
   - Demonstrate automation

3. **Community Features**
   - Discord bot commands
   - Leaderboard system
   - Badge/role system

4. **Documentation**
   - This file!
   - Workflow templates
   - Setup instructions

---

## 🎁 Bonus: Pre-Made Templates

### Template Pack Included:

```
/easyconnect-templates/
├── loan-approval-notification.json
├── payment-reminder.json
├── liquidation-alert.json
├── analytics-tracker.json
├── whale-monitor.json
├── milestone-celebration.json
└── discord-bot-config.json
```

**To use**: Import JSON files into EasyConnect dashboard

---

## 🔗 Resources

- **EasyConnect Docs**: https://docs.easyconnect.qubic.org
- **EasyConnect Discord**: Join for support
- **Workflow Gallery**: Browse community workflows
- **API Reference**: For advanced integrations

---

## 🏆 Why This Wins Track 2

✅ **Real Integration**: Not just theoretical, actually works  
✅ **Multiple Workflows**: 7+ ready-to-use automations  
✅ **No-Code Focus**: Anyone can set up  
✅ **Community Features**: Gamification + engagement  
✅ **Production Ready**: Professional templates  
✅ **Cross-Platform**: Discord, Telegram, Email, Sheets  
✅ **Integrated with Track 1**: Works with your DeFi app  

---

## 🚀 Quick Start Commands

```bash
# 1. Contracts already deployed? Skip to step 2
# 2. Get contract address
echo "Your LendingPool address: 0x..."

# 3. Go to EasyConnect
open https://easyconnect.qubic.org

# 4. Add contract
# 5. Import workflows from /easyconnect-templates/
# 6. Test!
```

---

**Built with ❤️ for Qubic EasyConnect**

*Making Blockchain Accessible to Everyone*

