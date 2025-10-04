package com.example.rememberme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(
    private var lists: List<RememberList>,
    private val onListClick: (RememberList) -> Unit
) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listName: TextView = itemView.findViewById(R.id.tvListName)
        val listItems: TextView = itemView.findViewById(R.id.tvListItems)
        val listDate: TextView = itemView.findViewById(R.id.tvListDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = lists[position]
        holder.listName.text = list.name
        holder.listItems.text = list.items.joinToString("\n• ", "• ")
        holder.listDate.text = "Creata: ${list.createdAt}"
        
        holder.itemView.setOnClickListener {
            onListClick(list)
        }
    }

    override fun getItemCount(): Int = lists.size

    fun updateLists(newLists: List<RememberList>) {
        lists = newLists
        notifyDataSetChanged()
    }
}
