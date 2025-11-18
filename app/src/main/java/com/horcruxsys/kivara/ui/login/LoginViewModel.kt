package com.horcruxsys.kivara.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horcruxsys.kivara.api.ApiService
import com.horcruxsys.kivara.api.LoginRequest
import com.horcruxsys.kivara.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for login screen that handles authentication.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    /**
     * Attempt to login with provided credentials
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                
                val response = apiService.login(LoginRequest(email, password))
                
                if (response.success && response.token != null) {
                    // Save session token
                    sessionManager.saveSessionToken(response.token)
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error(
                        response.message ?: "Login failed. Please check your credentials."
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error during login")
                _loginState.value = LoginState.Error(
                    "Login failed. Please check your credentials."
                )
            }
        }
    }
}

/**
 * Login state sealed class
 */
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
