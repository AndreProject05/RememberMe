package com.example.rememberme

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "RememberMe.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_LISTS = "lists"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_ITEMS = "items"
        private const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_LISTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_ITEMS TEXT NOT NULL,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LISTS")
        onCreate(db)
    }

    fun addList(name: String, items: List<String>): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_ITEMS, JSONArray(items).toString())
        }
        return db.insert(TABLE_LISTS, null, values)
    }

    fun updateList(id: Long, name: String, items: List<String>): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_ITEMS, JSONArray(items).toString())
        }
        return db.update(TABLE_LISTS, values, "$COLUMN_ID = ?", arrayOf(id.toString())) > 0
    }

    fun getListById(id: Long): RememberList? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_LISTS,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_ITEMS, COLUMN_CREATED_AT),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                val listId = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME))
                val itemsJson = it.getString(it.getColumnIndexOrThrow(COLUMN_ITEMS))
                val createdAt = it.getString(it.getColumnIndexOrThrow(COLUMN_CREATED_AT))
                
                val items = mutableListOf<String>()
                val jsonArray = JSONArray(itemsJson)
                for (i in 0 until jsonArray.length()) {
                    items.add(jsonArray.getString(i))
                }
                
                RememberList(listId, name, items, createdAt)
            } else {
                null
            }
        }
    }

    fun getAllLists(): List<RememberList> {
        val lists = mutableListOf<RememberList>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_LISTS,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_ITEMS, COLUMN_CREATED_AT),
            null, null, null, null,
            "$COLUMN_CREATED_AT DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME))
                val itemsJson = it.getString(it.getColumnIndexOrThrow(COLUMN_ITEMS))
                val createdAt = it.getString(it.getColumnIndexOrThrow(COLUMN_CREATED_AT))
                
                val items = mutableListOf<String>()
                val jsonArray = JSONArray(itemsJson)
                for (i in 0 until jsonArray.length()) {
                    items.add(jsonArray.getString(i))
                }
                
                lists.add(RememberList(id, name, items, createdAt))
            }
        }
        return lists
    }

    fun deleteList(id: Long): Boolean {
        val db = this.writableDatabase
        return db.delete(TABLE_LISTS, "$COLUMN_ID = ?", arrayOf(id.toString())) > 0
    }

    fun clearAllLists() {
        val db = this.writableDatabase
        db.delete(TABLE_LISTS, null, null)
    }

    fun deleteOldLists() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_LISTS WHERE $COLUMN_CREATED_AT < datetime('now', '-1 day')")
    }
}
