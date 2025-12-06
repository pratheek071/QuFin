package com.example.finapp.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finapp.MainActivity
import com.example.finapp.R
import com.example.finapp.data.repository.LoanRepository
import com.example.finapp.data.repository.PaymentRepository
import com.example.finapp.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val loanRepository = LoanRepository(FirebaseFirestore.getInstance())
    private val paymentRepository = PaymentRepository(
        FirebaseFirestore.getInstance(),
        loanRepository
    )
    
    override suspend fun doWork(): Result {
        return try {
            // Check if today is between 1st and 10th
            val calendar = Calendar.getInstance()
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            
            // Only send reminders on days 1-10
            if (dayOfMonth < 1 || dayOfMonth > 10) {
                Log.d("DailyReminderWorker", "Not a payment reminder day ($dayOfMonth), skipping")
                return Result.success()
            }
            
            Log.d("DailyReminderWorker", "Checking payments for day $dayOfMonth")
            
            // Get all approved (active) loans
            val activeLoans = loanRepository.getApprovedLoans().getOrNull() ?: emptyList()
            
            activeLoans.forEach { loan ->
                // Check if payment is made this month for this loan
                val monthlyPayment = paymentRepository.getMonthlyPaymentForLoan(loan.id).getOrNull()
                
                if (monthlyPayment == null) {
                    // Send reminder notification
                    sendReminderNotification(
                        userId = loan.userId,
                        userName = loan.userName,
                        amount = loan.monthlyAmount,
                        dayOfMonth = dayOfMonth
                    )
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
    
    private fun sendReminderNotification(userId: String, userName: String, amount: Double, dayOfMonth: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        // Customize message based on day
        val (title, message) = when {
            dayOfMonth == 10 -> {
                "⚠️ Final Reminder - Payment Due Today!" to 
                "Hi $userName, today is the LAST DAY to pay ₹$amount. Pay before midnight!"
            }
            dayOfMonth >= 8 -> {
                "⏰ Payment Reminder" to 
                "Hi $userName, your monthly payment of ₹$amount is due in ${11 - dayOfMonth} days!"
            }
            else -> {
                "💰 Monthly Payment Reminder" to 
                "Hi $userName, your monthly payment of ₹$amount is due by the 10th of this month."
            }
        }
        
        val notification = NotificationCompat.Builder(applicationContext, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(userId.hashCode(), notification)
    }
}

