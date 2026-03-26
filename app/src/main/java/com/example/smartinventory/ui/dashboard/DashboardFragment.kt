package com.example.smartinventory.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.smartinventory.R
import com.example.smartinventory.data.db.AppDatabase
import com.example.smartinventory.data.repository.InventoryRepository
import com.example.smartinventory.util.SessionManager
import com.example.smartinventory.viewmodel.InventoryViewModel
import com.example.smartinventory.viewmodel.ViewModelFactory

class DashboardFragment : Fragment() {

    private lateinit var viewModel: InventoryViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()

        // Set up ViewModel
        val db = AppDatabase.getInstance(requireContext())
        val repository = InventoryRepository(db.userDao(), db.itemDao())
        val factory = ViewModelFactory(repository, userId)
        viewModel = ViewModelProvider(this, factory)[InventoryViewModel::class.java]

        // Get view references
        val tvUsername = view.findViewById<TextView>(R.id.tvUsername)
        val tvTotalItems = view.findViewById<TextView>(R.id.tvTotalItems)
        val tvTotalValue = view.findViewById<TextView>(R.id.tvTotalValue)
        val cardLowStock = view.findViewById<CardView>(R.id.cardLowStock)
        val tvLowStockMessage = view.findViewById<TextView>(R.id.tvLowStockMessage)
        val btnViewInventory = view.findViewById<Button>(R.id.btnViewInventory)
        val btnAddItem = view.findViewById<Button>(R.id.btnAddItem)

        // Set username
        tvUsername.text = sessionManager.getUsername() ?: "User"

        // Observe item count
        viewModel.itemCount.observe(viewLifecycleOwner) { count ->
            tvTotalItems.text = count?.toString() ?: "0"
        }

        // Observe total value
        viewModel.totalValue.observe(viewLifecycleOwner) { value ->
            tvTotalValue.text = "$%.2f".format(value ?: 0.0)
        }

        // Observe items for low stock warning
        viewModel.items.observe(viewLifecycleOwner) { items ->
            val lowStockItems = items?.filter { it.quantity <= it.lowStockThreshold }
            if (!lowStockItems.isNullOrEmpty()) {
                cardLowStock.visibility = View.VISIBLE
                tvLowStockMessage.text = "${lowStockItems.size} item(s) are running low: " +
                        lowStockItems.joinToString(", ") { it.name }
            } else {
                cardLowStock.visibility = View.GONE
            }
        }

        // Navigate to item list
        btnViewInventory.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_itemList)
        }

        // Navigate to add item
        btnAddItem.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_addItem)
        }
    }
}