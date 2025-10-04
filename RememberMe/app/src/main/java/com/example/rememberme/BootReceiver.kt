package com.example.rememberme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val dbHelper = DatabaseHelper(context)
            
            // Svuota liste vecchie di 24h
            dbHelper.deleteOldLists()
            
            // Mostra notifica con le liste rimanenti
            val lists = dbHelper.getAllLists()
            if (lists.isNotEmpty()) {
                NotificationService.showAllListsNotification(context, lists)
            }
        }
    }
}
