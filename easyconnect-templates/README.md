# EasyConnect Workflow Templates

Pre-configured automation workflows for Qubic Finance.

## 📋 Available Templates

1. **loan-approval-notification.json** - Discord notifications for loan approvals
2. **payment-reminder.json** - Daily payment reminders via Telegram
3. **liquidation-alert.json** - Multi-channel urgent alerts
4. **analytics-tracker.json** - Auto-update Google Sheets
5. **whale-monitor.json** - Track large loan activities
6. **milestone-celebration.json** - Celebrate user achievements

## 🚀 How to Use

### Method 1: Import in EasyConnect Dashboard

1. Log in to [EasyConnect](https://easyconnect.qubic.org)
2. Click "Import Workflow"
3. Upload JSON file
4. Configure your webhook URLs/tokens
5. Save & Activate

### Method 2: Manual Setup

Copy the configuration and create manually in EasyConnect UI.

## ⚙️ Configuration Required

Before activating, update these placeholders:

- `LENDING_POOL_ADDRESS` - Your deployed LendingPool contract
- `YOUR_DISCORD_WEBHOOK_URL` - Get from Discord server settings
- `YOUR_TELEGRAM_BOT_TOKEN` - Get from @BotFather
- `YOUR_SPREADSHEET_ID` - From Google Sheets URL
- Email/SMS credentials (if using)

## 📊 Template Details

### Loan Approval Notification
**Trigger**: When admin approves loan  
**Action**: Send Discord embed message  
**Setup Time**: 2 minutes  

### Payment Reminder
**Trigger**: Daily at 9:00 AM  
**Action**: Query active loans, send Telegram messages  
**Setup Time**: 5 minutes  

### Liquidation Alert
**Trigger**: Health factor drops below 1.3  
**Action**: Email + Discord + SMS alerts  
**Setup Time**: 10 minutes  

### Analytics Tracker
**Trigger**: Any loan event  
**Action**: Update Google Sheets  
**Setup Time**: 5 minutes  

## 🧪 Testing

Each template includes test data. Use EasyConnect's test feature:

```json
{
  "loanId": "123",
  "borrower": "0x1234...5678",
  "amount": "5000",
  "healthFactor": "1.25"
}
```

## 🎯 Recommended Setup Order

1. **Start with**: loan-approval-notification.json (easiest)
2. **Then add**: analytics-tracker.json (useful data)
3. **Important**: liquidation-alert.json (safety)
4. **Nice to have**: payment-reminder.json (engagement)

## 💡 Tips

- Test each workflow before activating
- Start with one notification channel, add more later
- Monitor EasyConnect logs for errors
- Join EasyConnect Discord for support

## 🔗 Resources

- [EasyConnect Docs](https://docs.easyconnect.qubic.org)
- [Full Integration Guide](../EASYCONNECT_INTEGRATION.md)
- [Discord Webhook Guide](https://support.discord.com/hc/en-us/articles/228383668)
- [Telegram Bot Guide](https://core.telegram.org/bots)

---

**Questions?** Check [EASYCONNECT_INTEGRATION.md](../EASYCONNECT_INTEGRATION.md) for detailed setup instructions.

