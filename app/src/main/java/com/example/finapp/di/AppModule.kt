package com.example.finapp.di

import android.content.Context
import androidx.work.WorkManager
import com.example.finapp.blockchain.QubicSDK
import com.example.finapp.blockchain.SmartContractInterface
import com.example.finapp.blockchain.TransactionHandler
import com.example.finapp.blockchain.WalletManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
    
    // Blockchain Dependencies
    
    @Provides
    @Singleton
    fun provideWalletManager(@ApplicationContext context: Context): WalletManager {
        return WalletManager(context)
    }
    
    @Provides
    @Singleton
    fun provideQubicSDK(@ApplicationContext context: Context): QubicSDK {
        return QubicSDK(context)
    }
    
    @Provides
    @Singleton
    fun provideTransactionHandler(
        qubicSDK: QubicSDK,
        walletManager: WalletManager
    ): TransactionHandler {
        return TransactionHandler(qubicSDK, walletManager)
    }
    
    @Provides
    @Singleton
    fun provideSmartContractInterface(
        qubicSDK: QubicSDK,
        walletManager: WalletManager
    ): SmartContractInterface {
        return SmartContractInterface(qubicSDK, walletManager)
    }
}


