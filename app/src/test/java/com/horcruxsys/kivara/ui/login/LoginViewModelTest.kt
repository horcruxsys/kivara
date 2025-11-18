package com.horcruxsys.kivara.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.horcruxsys.kivara.api.ApiService
import com.horcruxsys.kivara.api.LoginRequest
import com.horcruxsys.kivara.api.LoginResponse
import com.horcruxsys.kivara.util.MainDispatcherRule
import com.horcruxsys.kivara.util.SessionManager
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for LoginViewModel
 */
@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: LoginViewModel
    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        apiService = mockk(relaxed = true)
        sessionManager = mockk(relaxed = true)
        viewModel = LoginViewModel(apiService, sessionManager)
    }

    @Test
    fun `login with valid credentials should succeed`() = runTest {
        // Given - Valid credentials and successful API response
        val email = "test@example.com"
        val password = "password123"
        val token = "valid_token"
        coEvery { apiService.login(LoginRequest(email, password)) } returns 
            LoginResponse(success = true, token = token, message = null)

        // When - Login is called
        viewModel.loginState.test {
            assertEquals(LoginState.Idle, awaitItem())
            
            viewModel.login(email, password)
            
            // Then - Should transition through states correctly
            assertEquals(LoginState.Loading, awaitItem())
            assertEquals(LoginState.Success, awaitItem())
            
            // And - Should save session token
            verify { sessionManager.saveSessionToken(token) }
        }
    }

    @Test
    fun `login with invalid credentials should fail`() = runTest {
        // Given - Invalid credentials
        val email = "test@example.com"
        val password = "wrongpassword"
        val errorMessage = "Invalid credentials"
        coEvery { apiService.login(LoginRequest(email, password)) } returns 
            LoginResponse(success = false, token = null, message = errorMessage)

        // When - Login is called
        viewModel.loginState.test {
            assertEquals(LoginState.Idle, awaitItem())
            
            viewModel.login(email, password)
            
            // Then - Should transition to error state
            assertEquals(LoginState.Loading, awaitItem())
            val errorState = awaitItem()
            assertTrue(errorState is LoginState.Error)
            assertEquals(errorMessage, (errorState as LoginState.Error).message)
        }
    }

    @Test
    fun `login with network error should fail`() = runTest {
        // Given - Network error
        val email = "test@example.com"
        val password = "password123"
        coEvery { apiService.login(any()) } throws Exception("Network error")

        // When - Login is called
        viewModel.loginState.test {
            assertEquals(LoginState.Idle, awaitItem())
            
            viewModel.login(email, password)
            
            // Then - Should show error state
            assertEquals(LoginState.Loading, awaitItem())
            val errorState = awaitItem()
            assertTrue(errorState is LoginState.Error)
        }
    }

    @Test
    fun `initial state should be Idle`() = runTest {
        // When - ViewModel is created
        viewModel.loginState.test {
            // Then - Initial state should be Idle
            assertEquals(LoginState.Idle, awaitItem())
        }
    }
}
