package com.example.smartinventory.ui.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartinventory.R
import com.example.smartinventory.data.model.InventoryItem

class ItemAdapter(
    private val onItemClick: (InventoryItem) -> Unit,
    private val onItemLongClick: (InventoryItem) -> Unit
) : ListAdapter<InventoryItem, ItemAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvItemName = itemView.findViewById<TextView>(R.id.tvItemName)
        private val tvItemCategory = itemView.findViewById<TextView>(R.id.tvItemCategory)
        private val tvItemPrice = itemView.findViewById<TextView>(R.id.tvItemPrice)
        private val tvItemQuantity = itemView.findViewById<TextView>(R.id.tvItemQuantity)
        private val categoryTag = itemView.findViewById<View>(R.id.categoryTag)

        fun bind(item: InventoryItem) {
            tvItemName.text = item.name
            tvItemCategory.text = item.category
            tvItemPrice.text = "$%.2f".format(item.price)
            tvItemQuantity.text = item.quantity.toString()

            // Change quantity badge color to red if low stock
            if (item.quantity <= item.lowStockThreshold) {
                tvItemQuantity.setBackgroundResource(R.drawable.quantity_badge_low)
            } else {
                tvItemQuantity.setBackgroundResource(R.drawable.quantity_badge_bg)
            }

            // Change category tag color based on category
            val tagColor = when (item.category) {
                "Electronics" -> "#9C27B0"
                "Food" -> "#FF9800"
                "Clothing" -> "#E91E63"
                "Tools" -> "#607D8B"
                else -> "#2196F3"
            }
            categoryTag.setBackgroundColor(android.graphics.Color.parseColor(tagColor))

            // Click listeners
            itemView.setOnClickListener { onItemClick(item) }
            itemView.setOnLongClickListener {
                onItemLongClick(item)
                true
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<InventoryItem>() {
        override fun areItemsTheSame(oldItem: InventoryItem, newItem: InventoryItem) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: InventoryItem, newItem: InventoryItem) =
            oldItem == newItem
    }
}