package com.example.rememberme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class DeleteListReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val listName = intent.getStringExtra("list_name")
        val notificationId = intent.getIntExtra("notification_id", 0)
        
        val dbHelper = DatabaseHelper(context)
        
        // Trova e elimina la lista dal database
        val lists = dbHelper.getAllLists()
        val listToDelete = lists.find { it.name == listName }
        
        listToDelete?.let { list ->
            dbHelper.deleteList(list.id)
        }
        
        // Cancella la notifica
        NotificationManagerCompat.from(context).cancel(notificationId)
        
        // Mostra notifica di conferma
        NotificationService(context).showDeletionConfirmation(listName ?: "Lista")
    }
}
