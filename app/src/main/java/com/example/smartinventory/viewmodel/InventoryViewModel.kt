package com.example.smartinventory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.data.repository.InventoryRepository
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val repository: InventoryRepository,
    private val userId: Int
) : ViewModel() {

    // ─── Live lists and stats ─────────────────────────────────────────────────

    val items: LiveData<List<InventoryItem>> = repository.getItems(userId)
    val itemCount: LiveData<Int> = repository.getItemCount(userId)
    val totalValue: LiveData<Double> = repository.getTotalValue(userId)

    private val _operationState = MutableLiveData<OperationState>()
    val operationState: LiveData<OperationState> = _operationState

    // ─── Add item ─────────────────────────────────────────────────────────────

    fun addItem(
        name: String,
        description: String,
        quantity: Int,
        price: Double,
        category: String,
        lowStockThreshold: Int = 5
    ) {
        if (name.isBlank()) {
            _operationState.value = OperationState.Error("Item name is required")
            return
        }

        viewModelScope.launch {
            repository.addItem(
                InventoryItem(
                    userId = userId,
                    name = name,
                    description = description,
                    quantity = quantity,
                    price = price,
                    category = category,
                    lowStockThreshold = lowStockThreshold
                )
            )
            _operationState.value = OperationState.Success("Item added successfully")
        }
    }

    // ─── Update item ──────────────────────────────────────────────────────────

    fun updateItem(item: InventoryItem) {
        viewModelScope.launch {
            repository.updateItem(item.copy(updatedAt = System.currentTimeMillis()))
            _operationState.value = OperationState.Success("Item updated successfully")
        }
    }

    // ─── Delete item ──────────────────────────────────────────────────────────

    fun deleteItem(item: InventoryItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
            _operationState.value = OperationState.Success("Item deleted")
        }
    }

    // ─── Search ───────────────────────────────────────────────────────────────

    fun searchItems(query: String): LiveData<List<InventoryItem>> {
        return repository.searchItems(userId, query)
    }

    // ─── Reset state ──────────────────────────────────────────────────────────

    fun resetState() {
        _operationState.value = OperationState.Idle
    }

    // ─── All possible operation states ───────────────────────────────────────

    sealed class OperationState {
        object Idle : OperationState()
        data class Success(val message: String) : OperationState()
        data class Error(val message: String) : OperationState()
    }
}