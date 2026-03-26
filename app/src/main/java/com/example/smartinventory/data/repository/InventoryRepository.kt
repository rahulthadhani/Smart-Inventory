package com.example.smartinventory.data.repository

import androidx.lifecycle.LiveData
import com.example.smartinventory.data.db.ItemDao
import com.example.smartinventory.data.db.UserDao
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.data.model.User
import org.mindrot.jbcrypt.BCrypt

class InventoryRepository(
    private val userDao: UserDao,
    private val itemDao: ItemDao
) {

    // ─── Auth ────────────────────────────────────────────────────────────────

    suspend fun registerUser(
        username: String,
        email: String,
        password: String
    ): Result<Long> {
        return try {
            val hash = BCrypt.hashpw(password, BCrypt.gensalt())
            val id = userDao.insertUser(
                User(username = username, email = email, passwordHash = hash)
            )
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): User? {
        val user = userDao.getUserByEmail(email) ?: return null
        return if (BCrypt.checkpw(password, user.passwordHash)) user else null
    }

    suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }

    // ─── Items ───────────────────────────────────────────────────────────────

    fun getItems(userId: Int): LiveData<List<InventoryItem>> {
        return itemDao.getItemsByUser(userId)
    }

    fun getItemCount(userId: Int): LiveData<Int> {
        return itemDao.getItemCount(userId)
    }

    fun getTotalValue(userId: Int): LiveData<Double> {
        return itemDao.getTotalValue(userId)
    }

    fun searchItems(userId: Int, query: String): LiveData<List<InventoryItem>> {
        return itemDao.searchItems(userId, query)
    }

    suspend fun addItem(item: InventoryItem) {
        itemDao.insertItem(item)
    }

    suspend fun updateItem(item: InventoryItem) {
        itemDao.updateItem(item)
    }

    suspend fun deleteItem(item: InventoryItem) {
        itemDao.deleteItem(item)
    }
}