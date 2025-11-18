package com.horcruxsys.kivara.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.horcruxsys.kivara.MainActivity
import com.horcruxsys.kivara.R
import com.horcruxsys.kivara.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Login activity for user authentication.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (validateInput(email, password)) {
                viewModel.login(email, password)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            return false
        }
        
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            return false
        }
        
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        return true
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Idle -> {
                        hideLoading()
                    }
                    is LoginState.Loading -> {
                        showLoading()
                    }
                    is LoginState.Success -> {
                        hideLoading()
                        navigateToMain()
                    }
                    is LoginState.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBarLogin.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBarLogin.visibility = View.GONE
        binding.btnLogin.isEnabled = true
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
