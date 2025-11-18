package com.horcruxsys.kivara.ui.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.horcruxsys.kivara.api.ApiService
import com.horcruxsys.kivara.api.SessionResponse
import com.horcruxsys.kivara.util.MainDispatcherRule
import com.horcruxsys.kivara.util.SessionManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for SplashViewModel
 */
@ExperimentalCoroutinesApi
class SplashViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SplashViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        sessionManager = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        viewModel = SplashViewModel(sessionManager, apiService)
    }

    @Test
    fun `checkSession navigates to login when user is not logged in`() = runTest {
        // Given - User is not logged in
        every { sessionManager.isLoggedIn() } returns false

        // When - Check session is called
        viewModel.checkSession()

        // Then - Should navigate to login
        val event = viewModel.navigationEvent.first()
        assertTrue(event is SplashNavigationEvent.NavigateToLogin)
    }

    @Test
    fun `checkSession navigates to login when session token is null`() = runTest {
        // Given - User is logged in but token is null
        every { sessionManager.isLoggedIn() } returns true
        every { sessionManager.getSessionToken() } returns null

        // When - Check session is called
        viewModel.checkSession()

        // Then - Should navigate to login
        val event = viewModel.navigationEvent.first()
        assertTrue(event is SplashNavigationEvent.NavigateToLogin)
    }

    @Test
    fun `checkSession navigates to main when session is valid`() = runTest {
        // Given - User is logged in with valid session
        every { sessionManager.isLoggedIn() } returns true
        every { sessionManager.getSessionToken() } returns "valid_token"
        coEvery { apiService.validateSession(any()) } returns SessionResponse(valid = true, userId = "123")

        // When - Check session is called
        viewModel.checkSession()

        // Then - Should navigate to main
        val event = viewModel.navigationEvent.first()
        assertTrue(event is SplashNavigationEvent.NavigateToMain)
    }

    @Test
    fun `checkSession navigates to login when session validation fails`() = runTest {
        // Given - User is logged in but session is invalid
        every { sessionManager.isLoggedIn() } returns true
        every { sessionManager.getSessionToken() } returns "invalid_token"
        coEvery { apiService.validateSession(any()) } returns SessionResponse(valid = false, userId = null)

        // When - Check session is called
        viewModel.checkSession()

        // Then - Should clear session and navigate to login
        verify { sessionManager.clearSession() }
        val event = viewModel.navigationEvent.first()
        assertTrue(event is SplashNavigationEvent.NavigateToLogin)
    }

    @Test
    fun `checkSession navigates to main when API call fails but local session exists`() = runTest {
        // Given - User is logged in but API call fails
        every { sessionManager.isLoggedIn() } returns true
        every { sessionManager.getSessionToken() } returns "token"
        coEvery { apiService.validateSession(any()) } throws Exception("Network error")

        // When - Check session is called
        viewModel.checkSession()

        // Then - Should trust local session and navigate to main
        val event = viewModel.navigationEvent.first()
        assertTrue(event is SplashNavigationEvent.NavigateToMain)
    }
}
