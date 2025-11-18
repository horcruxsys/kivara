package com.horcruxsys.kivara.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.horcruxsys.kivara.MainActivity
import com.horcruxsys.kivara.databinding.ActivitySplashBinding
import com.horcruxsys.kivara.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash activity that checks user session and navigates accordingly.
 */
@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen API
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()
        checkSession()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.navigationEvent.collect { event ->
                when (event) {
                    is SplashNavigationEvent.NavigateToMain -> navigateToMain()
                    is SplashNavigationEvent.NavigateToLogin -> navigateToLogin()
                }
            }
        }
    }

    private fun checkSession() {
        lifecycleScope.launch {
            // Show splash with progress for at least 2 seconds for better UX
            delay(2000)
            viewModel.checkSession()
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
