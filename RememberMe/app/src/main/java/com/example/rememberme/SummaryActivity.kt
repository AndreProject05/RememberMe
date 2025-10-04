package com.example.rememberme

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SummaryActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var btnBack: Button
    private lateinit var btnClearAll: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)
        
        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        btnBack = findViewById(R.id.btnBack)
        btnClearAll = findViewById(R.id.btnClearAll)
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        loadLists()
        
        btnBack.setOnClickListener {
            finish()
        }
        
        btnClearAll.setOnClickListener {
            showClearAllDialog()
        }
    }
    
    private fun loadLists() {
        val lists = dbHelper.getAllLists()
        adapter = ListAdapter(lists) { list ->
            showListOptionsDialog(list)
        }
        recyclerView.adapter = adapter
    }
    
    private fun showListOptionsDialog(list: RememberList) {
        val options = arrayOf("Modifica", "Elimina", "Annulla")
        
        AlertDialog.Builder(this)
            .setTitle("Lista: ${list.name}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog(list) // Modifica
                    1 -> showDeleteDialog(list) // Elimina
                    // 2 -> Annulla (non fa nulla)
                }
            }
            .show()
    }
    
    private fun showEditDialog(list: RememberList) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_list, null)
        val editListName = dialogView.findViewById<EditText>(R.id.editListName)
        val editListItems = dialogView.findViewById<EditText>(R.id.editListItems)
        
        editListName.setText(list.name)
        editListItems.setText(list.items.joinToString(", "))
        
        AlertDialog.Builder(this)
            .setTitle("Modifica Lista")
            .setView(dialogView)
            .setPositiveButton("Salva") { _, _ ->
                val newName = editListName.text.toString().trim()
                val newItemsText = editListItems.text.toString().trim()
                val newItems = newItemsText.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                
                if (newName.isNotEmpty() && newItems.isNotEmpty()) {
                    // Cancella la notifica vecchia
                    NotificationManagerCompat.from(this).cancel(list.id.toInt())
                    
                    // Aggiorna il database
                    dbHelper.updateList(list.id, newName, newItems)
                    
                    // Crea nuova notifica
                    NotificationService.showListNotification(this, newName, newItems)
                    
                    loadLists()
                }
            }
            .setNegativeButton("Annulla", null)
            .show()
    }
    
    private fun showDeleteDialog(list: RememberList) {
        AlertDialog.Builder(this)
            .setTitle("Elimina Lista")
            .setMessage("Vuoi eliminare la lista '${list.name}'?")
            .setPositiveButton("Elimina") { _, _ ->
                dbHelper.deleteList(list.id)
                loadLists()
                // Cancella la notifica
                NotificationManagerCompat.from(this).cancel(list.id.toInt())
            }
            .setNegativeButton("Annulla", null)
            .show()
    }
    
    private fun showClearAllDialog() {
        AlertDialog.Builder(this)
            .setTitle("Svuota Tutto")
            .setMessage("Vuoi eliminare tutte le liste?")
            .setPositiveButton("Svuota") { _, _ ->
                dbHelper.clearAllLists()
                loadLists()
                // Cancella tutte le notifiche
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }
            .setNegativeButton("Annulla", null)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        loadLists()
    }
}
