package com.example.rememberme

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class DailyCleanupWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    
    override fun doWork(): Result {
        val dbHelper = DatabaseHelper(applicationContext)
        
        // Svuota liste vecchie di 24h
        dbHelper.deleteOldLists()
        
        // Cancella tutte le notifiche
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.cancelAll()
        
        return Result.success()
    }
}
