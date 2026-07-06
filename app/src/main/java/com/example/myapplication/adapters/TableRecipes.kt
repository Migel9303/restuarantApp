package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.db.Database
import com.example.myapplication.db.Recipe
import com.example.myapplication.db.getAllNames

class TableRecipes(
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<TableRecipes.ViewHolder>() {

    private val items = mutableListOf<Recipe>()

    fun submitList(newList: MutableList<Recipe>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_users, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName:   TextView = itemView.findViewById(R.id.tvName)
    }
}
