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

class RegisterFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up ViewModel
        val db = AppDatabase.getInstance(requireContext())
        val repository = InventoryRepository(db.userDao(), db.itemDao())
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Get view references
        val etUsername = view.findViewById<TextInputEditText>(R.id.etUsername)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
        val etConfirmPassword = view.findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val tvError = view.findViewById<TextView>(R.id.tvError)
        val tvGoToLogin = view.findViewById<TextView>(R.id.tvGoToLogin)

        // Register button click
        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (password != confirmPassword) {
                tvError.visibility = View.VISIBLE
                tvError.text = "Passwords do not match"
                return@setOnClickListener
            }

            viewModel.register(username, email, password)
        }

        // Go back to login
        tvGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }

        // Observe auth state
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    btnRegister.isEnabled = false
                    tvError.visibility = View.GONE
                }
                is AuthViewModel.AuthState.RegisterSuccess -> {
                    // Go back to login after successful registration
                    findNavController().navigate(R.id.action_register_to_login)
                }
                is AuthViewModel.AuthState.Error -> {
                    btnRegister.isEnabled = true
                    tvError.visibility = View.VISIBLE
                    tvError.text = state.message
                }
                else -> {
                    btnRegister.isEnabled = true
                }
            }
        }
    }
}