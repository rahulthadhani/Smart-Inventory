package com.example.smartinventory.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smartinventory.data.model.InventoryItem

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItem)

    @Update
    suspend fun updateItem(item: InventoryItem)

    @Delete
    suspend fun deleteItem(item: InventoryItem)

    @Query("SELECT * FROM inventory_items WHERE user_id = :userId ORDER BY created_at DESC")
    fun getItemsByUser(userId: Int): LiveData<List<InventoryItem>>

    @Query("SELECT COUNT(*) FROM inventory_items WHERE user_id = :userId")
    fun getItemCount(userId: Int): LiveData<Int>

    @Query("SELECT SUM(quantity * price) FROM inventory_items WHERE user_id = :userId")
    fun getTotalValue(userId: Int): LiveData<Double>

    @Query("SELECT * FROM inventory_items WHERE user_id = :userId AND name LIKE '%' || :query || '%'")
    fun searchItems(userId: Int, query: String): LiveData<List<InventoryItem>>
}