# Qubic Finance Smart Contracts

This directory contains the Solidity smart contracts for the Qubic Finance DeFi lending protocol.

## Contracts Overview

### 1. QFINToken.sol
The governance and utility token for the Qubic Finance ecosystem.

**Features:**
- ERC20 standard token
- Staking mechanism with APY rewards
- Burnable supply
- 100M total supply

**Key Functions:**
- `stake(amount)` - Stake QFIN tokens to earn rewards
- `unstake(amount)` - Unstake tokens
- `claimRewards()` - Claim staking rewards
- `pendingRewards(user)` - View pending rewards

### 2. LendingPool.sol
Core lending protocol contract managing loans and collateral.

**Features:**
- Collateralized loan creation
- Multi-collateral support
- Automated liquidation
- Admin approval system
- Interest rate calculation

**Key Functions:**
- `createLoan(collateralToken, collateralAmount, loanAmount, duration, loanType)` - Create new loan
- `approveLoan(loanId)` - Approve loan (admin)
- `rejectLoan(loanId)` - Reject loan (admin)
- `makePayment(loanId)` - Make payment towards loan
- `liquidateLoan(loanId)` - Liquidate undercollateralized loan
- `isLiquidatable(loanId)` - Check liquidation status

### 3. CollateralManager.sol
Manages collateral pricing and liquidation parameters.

**Features:**
- Price oracle integration
- Collateral configuration
- Health factor calculation
- Liquidation threshold management

**Key Functions:**
- `updatePrice(token, priceUSD)` - Update token price (oracle)
- `getCollateralValue(token, amount)` - Get USD value of collateral
- `getMaxLoanAmount(token, collateralAmount)` - Calculate max loan
- `isLiquidatable(collateralToken, collateralAmount, debtAmount)` - Check liquidation
- `calculateHealthFactor(...)` - Calculate position health

## Deployment

### Prerequisites
```bash
npm install --save-dev hardhat @openzeppelin/contracts
```

### Deployment Steps

1. **Deploy QFINToken**
```bash
npx hardhat run scripts/deploy-qfin.js --network qubic-testnet
```

2. **Deploy CollateralManager**
```bash
npx hardhat run scripts/deploy-collateral-manager.js --network qubic-testnet
```

3. **Deploy LendingPool**
```bash
npx hardhat run scripts/deploy-lending-pool.js --network qubic-testnet
```

4. **Configure Contracts**
```bash
# Add supported collateral tokens
# Set price oracle
# Configure LTV ratios
```

### Deployment Script Example

```javascript
// deploy.js
const { ethers } = require("hardhat");

async function main() {
    const [deployer] = await ethers.getSigners();
    console.log("Deploying contracts with:", deployer.address);

    // Deploy QFIN Token
    const QFINToken = await ethers.getContractFactory("QFINToken");
    const qfin = await QFINToken.deploy(deployer.address);
    await qfin.deployed();
    console.log("QFIN Token:", qfin.address);

    // Deploy Collateral Manager
    const CollateralManager = await ethers.getContractFactory("CollateralManager");
    const collateralManager = await CollateralManager.deploy(deployer.address);
    await collateralManager.deployed();
    console.log("Collateral Manager:", collateralManager.address);

    // Deploy Lending Pool
    const LendingPool = await ethers.getContractFactory("LendingPool");
    const lendingPool = await LendingPool.deploy(
        deployer.address,
        qfin.address,
        deployer.address // treasury
    );
    await lendingPool.deployed();
    console.log("Lending Pool:", lendingPool.address);
}

main().catch((error) => {
    console.error(error);
    process.exitCode = 1;
});
```

## Testing

```bash
npx hardhat test
```

## Configuration

Update the deployed contract addresses in:
- `app/src/main/java/com/example/finapp/blockchain/QubicConfig.kt`

```kotlin
const val LENDING_POOL_CONTRACT = "0x..." // Your deployed address
const val QFIN_TOKEN_CONTRACT = "0x..."
const val COLLATERAL_MANAGER_CONTRACT = "0x..."
```

## Security Considerations

1. **Audits**: Get contracts audited before mainnet deployment
2. **Price Oracle**: Integrate with Chainlink or similar oracle
3. **Access Control**: Properly configure admin roles
4. **Testing**: Comprehensive unit and integration tests
5. **Gas Optimization**: Review and optimize gas usage

## Network Configuration

### Qubic Testnet
```
RPC URL: https://testapi.qubic.org
Chain ID: [To be confirmed]
```

### Qubic Mainnet
```
RPC URL: https://mainnet.qubic.org
Chain ID: [To be confirmed]
```

## Support

For questions or issues:
- Check Qubic Documentation: https://docs.qubic.org
- Qubic Discord: [Link]
- GitHub Issues: [Your Repo]

