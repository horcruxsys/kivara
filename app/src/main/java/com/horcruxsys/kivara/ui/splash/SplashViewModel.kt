package com.horcruxsys.kivara.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horcruxsys.kivara.api.ApiService
import com.horcruxsys.kivara.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for splash screen that handles session validation.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val apiService: ApiService
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<SplashNavigationEvent>()
    val navigationEvent: SharedFlow<SplashNavigationEvent> = _navigationEvent.asSharedFlow()

    /**
     * Check if user has an active session
     */
    fun checkSession() {
        viewModelScope.launch {
            try {
                if (sessionManager.isLoggedIn()) {
                    val token = sessionManager.getSessionToken()
                    if (token != null) {
                        // Validate session with backend
                        validateSessionWithApi(token)
                    } else {
                        navigateToLogin()
                    }
                } else {
                    navigateToLogin()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error checking session")
                // If there's an error, navigate to login for safety
                navigateToLogin()
            }
        }
    }

    private suspend fun validateSessionWithApi(token: String) {
        try {
            val response = apiService.validateSession("Bearer $token")
            if (response.valid) {
                navigateToMain()
            } else {
                sessionManager.clearSession()
                navigateToLogin()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error validating session with API")
            // For now, if API call fails, we'll trust local session
            // In production, you might want different behavior
            navigateToMain()
        }
    }

    private suspend fun navigateToMain() {
        _navigationEvent.emit(SplashNavigationEvent.NavigateToMain)
    }

    private suspend fun navigateToLogin() {
        _navigationEvent.emit(SplashNavigationEvent.NavigateToLogin)
    }
}

/**
 * Navigation events for splash screen
 */
sealed class SplashNavigationEvent {
    object NavigateToMain : SplashNavigationEvent()
    object NavigateToLogin : SplashNavigationEvent()
}
