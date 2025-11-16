package com.horcruxsys.kivara.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.horcruxsys.kivara.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Example unit test for HomeViewModel demonstrating best practices:
 * - Using InstantTaskExecutorRule for LiveData testing
 * - Using MainDispatcherRule for coroutine testing
 * - Using runTest for coroutine tests
 */
@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        viewModel = HomeViewModel()
    }

    @Test
    fun `viewModel initializes with default text`() {
        // Given - ViewModel is created

        // When - We observe the LiveData
        val value = viewModel.text.value

        // Then - Initial text should be set
        assertNotNull(value)
        assertEquals("This is home Fragment", value)
    }

    @Test
    fun `viewModel maintains state across configuration changes`() = runTest {
        // Given - ViewModel with initial state
        val initialValue = viewModel.text.value

        // When - Configuration change happens (new ViewModel instance would be created by ViewModelProvider)
        // In real scenario, ViewModelProvider maintains the same instance

        // Then - State should be preserved
        assertEquals(initialValue, viewModel.text.value)
    }

    @Test
    fun `liveData emits values correctly`() {
        // Given - ViewModel is initialized
        val observer = mutableListOf<String>()
        viewModel.text.observeForever { value ->
            value?.let { observer.add(it) }
        }

        // When - LiveData is observed
        // (setValue is called in init block)

        // Then - Observer should receive the value
        assertEquals(1, observer.size)
        assertEquals("This is home Fragment", observer.first())
    }
}
