package com.example.smartinventory.ui.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartinventory.R
import com.example.smartinventory.data.db.AppDatabase
import com.example.smartinventory.data.repository.InventoryRepository
import com.example.smartinventory.util.SessionManager
import com.example.smartinventory.viewmodel.InventoryViewModel
import com.example.smartinventory.viewmodel.ViewModelFactory

class ItemListFragment : Fragment() {

    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()

        // Set up ViewModel
        val db = AppDatabase.getInstance(requireContext())
        val repository = InventoryRepository(db.userDao(), db.itemDao())
        val factory = ViewModelFactory(repository, userId)
        viewModel = ViewModelProvider(this, factory)[InventoryViewModel::class.java]

        // Get view references
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val tvEmptyState = view.findViewById<TextView>(R.id.tvEmptyState)
        val btnAddNewItem = view.findViewById<Button>(R.id.btnAddNewItem)
        val searchView = view.findViewById<SearchView>(R.id.searchView)

        // Set up RecyclerView
        adapter = ItemAdapter(
            onItemClick = { item ->
                // Navigate to edit screen with item ID
                val bundle = bundleOf("itemId" to item.id)
                findNavController().navigate(R.id.action_itemList_to_addEditItem, bundle)
            },
            onItemLongClick = { item ->
                // Show delete confirmation dialog
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete ${item.name}?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteItem(item)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Observe items
        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            tvEmptyState.visibility = if (items.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        // Search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    viewModel.items.observe(viewLifecycleOwner) { items ->
                        adapter.submitList(items)
                    }
                } else {
                    viewModel.searchItems(newText).observe(viewLifecycleOwner) { items ->
                        adapter.submitList(items)
                    }
                }
                return true
            }
        })

        // Add new item button
        btnAddNewItem.setOnClickListener {
            findNavController().navigate(R.id.action_itemList_to_addEditItem)
        }
    }
}