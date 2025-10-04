package com.example.rememberme

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.LinearLayout
import android.widget.EditText
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    private lateinit var notificationToggle: Switch
    private lateinit var btnSummary: Button
    private lateinit var listsContainer: LinearLayout
    private var listCount = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Inizializzazione view
        notificationToggle = findViewById(R.id.notificationToggle)
        btnSummary = findViewById(R.id.btnSummary)
        listsContainer = findViewById(R.id.listsContainer)
        
        // Toggle notifiche
        notificationToggle.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationPreference(isChecked)
        }
        
        // Pulsante riepilogo
        btnSummary.setOnClickListener {
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
        }
        
        // Carica preferenze
        loadNotificationPreference()
        // Aggiungi una lista iniziale
        addListInput()
    }
    
    private fun addListInput() {
        val listLayout = LayoutInflater.from(this).inflate(R.layout.list_input_layout, null) as ViewGroup
        val listTitle = listLayout.findViewById<EditText>(R.id.listTitle)
        val listItems = listLayout.findViewById<EditText>(R.id.listItems)
        val btnRemoveList = listLayout.findViewById<ImageButton>(R.id.btnRemoveList)
        val btnSaveList = listLayout.findViewById<Button>(R.id.btnSaveList)
        
        listCount++
        listTitle.hint = "Inserisci nome lista"
        listItems.hint = "Inserisci lista"
        
        btnRemoveList.setOnClickListener {
            if (listCount > 1) {
                listsContainer.removeView(listLayout)
                listCount--
            }
        }
        
        btnSaveList.setOnClickListener {
            saveList(listLayout)
        }
        
        listsContainer.addView(listLayout)
    }
    
    private fun saveList(listLayout: ViewGroup) {
        val listTitle = listLayout.findViewById<EditText>(R.id.listTitle)
        val listItems = listLayout.findViewById<EditText>(R.id.listItems)
        
        val listName = listTitle.text.toString().trim()
        val itemsText = listItems.text.toString().trim()
        
        // Converti il testo in lista di elementi
        val items = itemsText.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        
        if (listName.isNotEmpty() && items.isNotEmpty()) {
            val dbHelper = DatabaseHelper(this)
            dbHelper.addList(listName, items)
            
            // Mostra notifica se le notifiche sono abilitate
            if (notificationToggle.isChecked) {
                NotificationService.showListNotification(this, listName, items)
            }
            
            // Reset della lista
            listTitle.text.clear()
            listItems.text.clear()
        } else {
            // Mostra errore
            if (listName.isEmpty()) {
                listTitle.error = "Inserisci un nome per la lista"
            }
            if (items.isEmpty()) {
                listItems.error = "Inserisci almeno un elemento"
            }
        }
    }
    
    private fun saveNotificationPreference(isEnabled: Boolean) {
        val sharedPref = getSharedPreferences("RememberMePrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("notifications_enabled", isEnabled)
            apply()
        }
    }
    
    private fun loadNotificationPreference() {
        val sharedPref = getSharedPreferences("RememberMePrefs", MODE_PRIVATE)
        notificationToggle.isChecked = sharedPref.getBoolean("notifications_enabled", true)
    }
}
