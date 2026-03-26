package com.example.smartinventory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartinventory.data.model.User
import com.example.smartinventory.data.repository.InventoryRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: InventoryRepository) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    // ─── Register ─────────────────────────────────────────────────────────────

    fun register(username: String, email: String, password: String) {
        // Basic validation before hitting the database
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.registerUser(username, email, password)
                .onSuccess {
                    _authState.value = AuthState.RegisterSuccess
                }
                .onFailure {
                    _authState.value = AuthState.Error("Email already in use")
                }
        }
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = repository.loginUser(email, password)
            if (user != null) {
                _authState.value = AuthState.LoginSuccess(user)
            } else {
                _authState.value = AuthState.Error("Invalid email or password")
            }
        }
    }

    // ─── Reset state (call this after handling a result) ─────────────────────

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    // ─── All possible auth states ─────────────────────────────────────────────

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object RegisterSuccess : AuthState()
        data class LoginSuccess(val user: User) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}