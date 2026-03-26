package com.example.smartinventory.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.smartinventory.R
import com.example.smartinventory.data.db.AppDatabase
import com.example.smartinventory.data.repository.InventoryRepository
import com.example.smartinventory.viewmodel.AuthViewModel
import com.example.smartinventory.viewmodel.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText

class LoginFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up ViewModel
        val db = AppDatabase.getInstance(requireContext())
        val repository = InventoryRepository(db.userDao(), db.itemDao())
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Get view references
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val tvError = view.findViewById<TextView>(R.id.tvError)
        val tvGoToRegister = view.findViewById<TextView>(R.id.tvGoToRegister)

        // Login button click
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        // Go to register screen
        tvGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        // Observe auth state
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    btnLogin.isEnabled = false
                    tvError.visibility = View.GONE
                }
                is AuthViewModel.AuthState.LoginSuccess -> {
                    // Save session
                    val sessionManager = com.example.smartinventory.util.SessionManager(requireContext())
                    sessionManager.saveSession(
                        state.user.id,
                        state.user.username,
                        state.user.email
                    )
                    // Go to dashboard
                    findNavController().navigate(R.id.action_login_to_dashboard)
                }
                is AuthViewModel.AuthState.Error -> {
                    btnLogin.isEnabled = true
                    tvError.visibility = View.VISIBLE
                    tvError.text = state.message
                }
                else -> {
                    btnLogin.isEnabled = true
                }
            }
        }
    }
}
