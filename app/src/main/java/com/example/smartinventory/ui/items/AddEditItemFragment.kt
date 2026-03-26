package com.example.smartinventory.ui.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.smartinventory.R
import com.example.smartinventory.data.db.AppDatabase
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.data.repository.InventoryRepository
import com.example.smartinventory.util.SessionManager
import com.example.smartinventory.viewmodel.InventoryViewModel
import com.example.smartinventory.viewmodel.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText

class AddEditItemFragment : Fragment() {

    private lateinit var viewModel: InventoryViewModel
    private var existingItem: InventoryItem? = null

    private val categories = listOf(
        "Uncategorized", "Electronics", "Food", "Clothing", "Tools", "Other"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_edit_item, container, false)
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
        val tvFormTitle = view.findViewById<TextView>(R.id.tvFormTitle)
        val etItemName = view.findViewById<TextInputEditText>(R.id.etItemName)
        val etDescription = view.findViewById<TextInputEditText>(R.id.etDescription)
        val etQuantity = view.findViewById<TextInputEditText>(R.id.etQuantity)
        val etPrice = view.findViewById<TextInputEditText>(R.id.etPrice)
        val etLowStockThreshold = view.findViewById<TextInputEditText>(R.id.etLowStockThreshold)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val tvError = view.findViewById<TextView>(R.id.tvError)

        // Set up category spinner
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = spinnerAdapter

        // Check if we are editing an existing item
        val itemId = arguments?.getInt("itemId", -1) ?: -1
        if (itemId != -1) {
            tvFormTitle.text = "Edit Item"
            btnSave.text = "Update Item"

            // Find the item from the live list
            viewModel.items.observe(viewLifecycleOwner) { items ->
                val found = items?.find { it.id == itemId }
                if (found != null && existingItem == null) {
                    existingItem = found
                    // Populate fields
                    etItemName.setText(found.name)
                    etDescription.setText(found.description)
                    etQuantity.setText(found.quantity.toString())
                    etPrice.setText(found.price.toString())
                    etLowStockThreshold.setText(found.lowStockThreshold.toString())
                    val categoryIndex = categories.indexOf(found.category)
                    if (categoryIndex >= 0) spinnerCategory.setSelection(categoryIndex)
                }
            }
        }

        // Save button
        btnSave.setOnClickListener {
            val name = etItemName.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val quantityText = etQuantity.text.toString().trim()
            val priceText = etPrice.text.toString().trim()
            val thresholdText = etLowStockThreshold.text.toString().trim()
            val category = spinnerCategory.selectedItem.toString()

            // Validate
            if (name.isBlank()) {
                tvError.visibility = View.VISIBLE
                tvError.text = "Item name is required"
                return@setOnClickListener
            }
            if (quantityText.isBlank()) {
                tvError.visibility = View.VISIBLE
                tvError.text = "Quantity is required"
                return@setOnClickListener
            }
            if (priceText.isBlank()) {
                tvError.visibility = View.VISIBLE
                tvError.text = "Price is required"
                return@setOnClickListener
            }

            tvError.visibility = View.GONE
            val quantity = quantityText.toIntOrNull() ?: 0
            val price = priceText.toDoubleOrNull() ?: 0.0
            val threshold = thresholdText.toIntOrNull() ?: 5

            if (existingItem != null) {
                // Update existing item
                viewModel.updateItem(
                    existingItem!!.copy(
                        name = name,
                        description = description,
                        quantity = quantity,
                        price = price,
                        category = category,
                        lowStockThreshold = threshold
                    )
                )
            } else {
                // Add new item
                viewModel.addItem(name, description, quantity, price, category, threshold)
            }

            findNavController().popBackStack()
        }

        // Cancel button
        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}