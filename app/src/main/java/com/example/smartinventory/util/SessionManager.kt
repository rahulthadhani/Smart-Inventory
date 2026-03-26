package com.example.smartinventory.util

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(
        "inventory_session",
        Context.MODE_PRIVATE
    )

    // ─── Save session when user logs in ──────────────────────────────────────

    fun saveSession(userId: Int, username: String, email: String) {
        prefs.edit()
            .putInt("user_id", userId)
            .putString("username", username)
            .putString("email", email)
            .apply()
    }

    // ─── Getters ─────────────────────────────────────────────────────────────

    fun getUserId(): Int {
        return prefs.getInt("user_id", -1)
    }

    fun getUsername(): String? {
        return prefs.getString("username", null)
    }

    fun getEmail(): String? {
        return prefs.getString("email", null)
    }

    // ─── Check if someone is logged in ───────────────────────────────────────

    fun isLoggedIn(): Boolean {
        return getUserId() != -1
    }

    // ─── Clear session when user logs out ────────────────────────────────────

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}