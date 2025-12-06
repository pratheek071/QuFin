# Qubic Finance - DeFi Lending Protocol
## Qubic Hackathon Submission - Track 1: Nostromo Launchpad

---

## 🚀 Project Overview

**Qubic Finance** is a decentralized lending protocol built on the Qubic Network that enables users to obtain collateralized loans using cryptocurrency as collateral. Our mobile-first approach makes DeFi accessible to everyone, anywhere.

### Problem Statement
Traditional loan systems are:
- Centralized and require extensive paperwork
- Have high barriers to entry
- Lack transparency
- Are slow and inefficient
- Limited accessibility (banking hours, geographical restrictions)

### Our Solution
A blockchain-powered, mobile-first DeFi lending platform that:
- ✅ Enables instant, collateralized loans using crypto
- ✅ Provides full transparency through on-chain transactions
- ✅ Offers competitive interest rates with automated calculations
- ✅ Supports multiple collateral types (QBTC, QETH, QUSD, QBN)
- ✅ Features automatic liquidation protection
- ✅ Rewards users with QFIN governance tokens
- ✅ Works 24/7 from any mobile device

---

## 🎯 Key Features

### For Borrowers
1. **Collateralized Loans**: Secure loans by depositing crypto collateral
2. **Multiple Loan Types**: Education, Personal, Home, Car, and Collateralized loans
3. **Real-time Health Factor**: Monitor your loan's collateralization ratio
4. **Flexible Repayment**: Pay anytime via blockchain transactions
5. **QFIN Rewards**: Earn governance tokens for participation
6. **Mobile Native**: Complete DeFi experience on Android

### For Lenders/Liquidity Providers
1. **Earn Interest**: Provide liquidity and earn competitive APY
2. **QFIN Staking**: Stake tokens for 12% APY
3. **Governance Rights**: Vote on protocol parameters
4. **Liquidation Bonuses**: Earn 5% bonus for liquidating risky loans

### For Admins
1. **Loan Approval System**: Review and approve loan requests
2. **Risk Management**: Monitor collateralization ratios
3. **Treasury Management**: Track protocol earnings
4. **Analytics Dashboard**: View protocol statistics

---

## 🏗️ Architecture

### Technology Stack

**Blockchain Layer:**
- Smart Contracts (Solidity 0.8.20)
- Qubic Network Integration
- Web3j for blockchain interactions

**Backend:**
- Firebase Firestore (off-chain data tracking)
- Firebase Authentication (user management)
- Firebase Cloud Messaging (notifications)

**Mobile App:**
- Kotlin (100%)
- MVVM Architecture
- Hilt Dependency Injection
- Coroutines for async operations
- Material Design 3

### Smart Contracts

1. **QFINToken.sol**
   - ERC20 governance token
   - 100M total supply
   - Staking mechanism (12% APY)
   - Reward distribution

2. **LendingPool.sol**
   - Core lending logic
   - Multi-collateral support
   - Interest rate calculation
   - Loan lifecycle management
   - Liquidation engine

3. **CollateralManager.sol**
   - Price oracle integration
   - Collateral valuation
   - Health factor calculation
   - LTV ratio management

### Mobile Architecture

```
┌─────────────────────────────────────────┐
│         Presentation Layer               │
│  (Fragments, ViewModels, UI)            │
├─────────────────────────────────────────┤
│         Blockchain Layer                 │
│  QubicSDK │ WalletManager │ SmartContracts│
├─────────────────────────────────────────┤
│         Data Layer                       │
│  Repositories │ Models │ Firebase        │
└─────────────────────────────────────────┘
```

---

## 💡 Innovation & Unique Value

### 1. Mobile-First DeFi
- **First truly mobile DeFi lending app** on Qubic
- Complete wallet management on Android
- No browser extension required
- One-tap transactions

### 2. Hybrid Storage Model
- On-chain: Critical financial data (loans, payments, collateral)
- Off-chain: User preferences, notifications, cached data
- Best of both worlds: Security + UX

### 3. Credit Score NFT (Roadmap)
- On-chain reputation system
- Portable credit history
- Better rates for good borrowers
- Build DeFi credit across platforms

### 4. Multi-Loan Type Support
- Traditional loan types (Education, Home, Car)
- DeFi-native collateralized loans
- Flash loans (coming soon)
- Flexible terms and amounts

### 5. Real-time Risk Management
- Automatic health factor monitoring
- Early liquidation warnings
- Push notifications for at-risk loans
- Transparent collateralization ratios

---

## 📊 Protocol Parameters

### Collateral Configuration

| Token | Name | LTV Ratio | Liquidation Threshold |
|-------|------|-----------|----------------------|
| QBTC  | Qubic Bitcoin | 70% | 130% |
| QETH  | Qubic Ethereum | 65% | 130% |
| QUSD  | Qubic USD | 90% | 130% |
| QBN   | Qubic BNB | 60% | 130% |

### Interest Rates (Base)

| Loan Type | APR |
|-----------|-----|
| Education | 5% |
| Personal  | 8% |
| Home      | 6% |
| Car       | 7% |
| Collateralized | 7.5% |

### QFIN Tokenomics

- **Total Supply**: 100,000,000 QFIN
- **Staking APY**: 12%
- **Borrower Rewards**: 1% of loan amount
- **Utility**: Governance, fee discounts, staking rewards

---

## 🎬 User Journey

### Borrower Flow

1. **Connect Wallet**
   - Create new wallet or import existing
   - Securely stored using Android KeyStore

2. **Select Loan Type**
   - Choose from 5 loan types
   - Enter desired amount and duration

3. **Add Collateral**
   - Select collateral token
   - System calculates max loan based on LTV
   - Approve token spending

4. **Submit Loan**
   - Smart contract creates loan
   - Admin reviews and approves
   - Funds disbursed to wallet

5. **Make Payments**
   - Crypto payments via blockchain
   - Automatic loan balance updates
   - Earn QFIN rewards

6. **Loan Completion**
   - Collateral automatically returned
   - Credit score NFT updated

---

## 📱 Screenshots & Demo

### Key Screens

1. **Wallet Connection**
   - Create/Import wallet interface
   - Security warnings and best practices

2. **Wallet Dashboard**
   - Balance overview
   - QFIN token display
   - Quick actions (Send, Receive, Stake)

3. **Loan Application**
   - Collateral selection
   - Amount calculator with LTV
   - Interest rate preview

4. **Active Loans**
   - List of user's loans
   - Health factor indicator
   - Payment button

5. **Payment Interface**
   - Amount input with gas estimation
   - Transaction confirmation
   - Real-time status updates

6. **Admin Dashboard**
   - Pending loan requests
   - Active loans monitoring
   - Protocol statistics

---

## 🔐 Security Features

1. **Private Key Storage**
   - Android KeyStore integration
   - AES-256 encryption
   - Biometric authentication support

2. **Smart Contract Security**
   - ReentrancyGuard on critical functions
   - Access control (Ownable)
   - Input validation
   - SafeMath operations

3. **Transaction Safety**
   - Gas estimation before sending
   - Balance checks
   - Confirmation requirements
   - Transaction status tracking

4. **Liquidation Protection**
   - Real-time health factor monitoring
   - Early warning notifications
   - Grace period for top-ups

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest)
- Node.js & npm (for smart contracts)
- Hardhat (for deployment)
- Qubic Testnet account

### Installation

```bash
# Clone repository
git clone <repository-url>
cd FinApp

# Install dependencies
./gradlew clean build

# Deploy smart contracts
cd contracts
npm install
npx hardhat compile
npx hardhat run scripts/deploy.js --network qubic-testnet

# Update contract addresses in QubicConfig.kt
# Run the Android app
./gradlew installDebug
```

### Configuration

1. Update `QubicConfig.kt` with deployed contract addresses
2. Configure Firebase (google-services.json)
3. Set up price oracle (for production)

---

## 📈 Future Roadmap

### Phase 1 - MVP (Current)
- ✅ Core lending functionality
- ✅ Wallet integration
- ✅ Smart contracts
- ✅ Mobile app

### Phase 2 - Enhancement (Q2 2025)
- [ ] Credit Score NFT
- [ ] Flash loans
- [ ] Liquidity pools
- [ ] Cross-chain bridges
- [ ] Advanced analytics

### Phase 3 - Scale (Q3 2025)
- [ ] iOS app
- [ ] Web interface
- [ ] Governance DAO
- [ ] Insurance fund
- [ ] Multi-language support

### Phase 4 - Ecosystem (Q4 2025)
- [ ] Developer SDK
- [ ] API for integrations
- [ ] White-label solutions
- [ ] Institutional features
- [ ] Real-world asset tokenization

---

## 💼 Business Model

### Revenue Streams

1. **Origination Fees**: 0.5% of loan amount
2. **Interest Spread**: Protocol keeps 10% of interest
3. **Liquidation Fees**: 5% of liquidated collateral
4. **Flash Loan Fees**: 0.09% per flash loan
5. **Premium Features**: Advanced analytics, priority support

### Token Utility (QFIN)

1. **Governance**: Vote on protocol parameters
2. **Fee Discounts**: Up to 50% off fees
3. **Staking Rewards**: 12% APY
4. **Liquidity Mining**: Extra rewards for LPs
5. **Collateral**: Use QFIN as collateral

---

## 🎯 Market Opportunity

### Target Market
- **Primary**: Crypto holders needing liquidity
- **Secondary**: DeFi users seeking yield
- **Tertiary**: Mobile-first users in emerging markets

### Market Size
- Global P2P lending: $460B (2024)
- DeFi lending TVL: $15B (2024)
- Mobile DeFi users: Growing 150% YoY

### Competitive Advantage
1. **Mobile-First**: Only true mobile DeFi lending
2. **Qubic Speed**: Fast, cheap transactions
3. **User Experience**: Simplified DeFi for masses
4. **Multi-Collateral**: More options than competitors
5. **Integrated Rewards**: Native token incentives

---

## 👥 Team

**Built for Qubic Hackathon**

### Skills Demonstrated
- Smart Contract Development (Solidity)
- Mobile Development (Kotlin/Android)
- Blockchain Integration (Web3)
- UI/UX Design (Material Design)
- System Architecture
- Security Best Practices

---

## 📞 Contact & Links

- **GitHub**: [Repository Link]
- **Demo Video**: [YouTube Link]
- **Presentation**: [Slides Link]
- **Contract Addresses**: See contracts/README.md
- **Email**: [Contact Email]

---

## 🏆 Hackathon Track

**Track 1 – Nostromo Launchpad: DeFi & Finance**

This project demonstrates:
- ✅ Innovative DeFi protocol on Qubic
- ✅ Real utility and user value
- ✅ Technical excellence and completeness
- ✅ Scalability and production-readiness
- ✅ Clear path to ecosystem integration

---

## 📄 License

This project is developed for the Qubic Hackathon.
See LICENSE file for details.

---

## 🙏 Acknowledgments

- Qubic Network team for the amazing infrastructure
- Nostromo Launchpad for the opportunity
- OpenZeppelin for secure contract libraries
- The DeFi community for inspiration

---

**Built with ❤️ for the Qubic Ecosystem**

