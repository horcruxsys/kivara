# Testing Guide

This guide provides instructions for writing and running tests in the Kivara project.

## Test Types

### 1. Unit Tests
Located in `app/src/test/`
- Fast, isolated tests
- No Android dependencies
- Run on JVM

### 2. Instrumentation Tests
Located in `app/src/androidTest/`
- Test Android framework components
- Run on device or emulator
- Test UI and integration

## Running Tests

### Run All Unit Tests
```bash
./gradlew test
```

### Run Specific Test Class
```bash
./gradlew test --tests HomeViewModelTest
```

### Run All Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

### Generate Coverage Report
```bash
./gradlew testDebugUnitTest jacocoTestReport
```

View report: `app/build/reports/jacoco/index.html`

## Writing Unit Tests

### ViewModel Test Example

```kotlin
@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var mockGetUserUseCase: GetUserUseCase

    @Before
    fun setup() {
        mockGetUserUseCase = mockk()
        viewModel = HomeViewModel(mockGetUserUseCase)
    }

    @Test
    fun `loadUser emits loading state initially`() = runTest {
        // Given
        val userId = "123"
        coEvery { mockGetUserUseCase(userId) } returns Result.success(mockUser)

        // When
        viewModel.loadUser(userId)

        // Then
        verify { viewModel.uiState.value is UiState.Loading }
    }
}
```

### Repository Test Example

```kotlin
@ExperimentalCoroutinesApi
class UserRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: UserRepositoryImpl
    private lateinit var mockApiService: ApiService
    private lateinit var mockUserDao: UserDao

    @Before
    fun setup() {
        mockApiService = mockk()
        mockUserDao = mockk()
        repository = UserRepositoryImpl(mockApiService, mockUserDao)
    }

    @Test
    fun `getUser returns cached user when available`() = runTest {
        // Given
        val userId = "123"
        val cachedUser = UserEntity(userId, "John", System.currentTimeMillis())
        coEvery { mockUserDao.getUserById(userId) } returns cachedUser

        // When
        val result = repository.getUser(userId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.id).isEqualTo(userId)
        coVerify(exactly = 0) { mockApiService.getUser(any()) }
    }

    @Test
    fun `getUser fetches from network when cache is expired`() = runTest {
        // Given
        val userId = "123"
        val expiredUser = UserEntity(userId, "John", 0) // Expired
        val freshUser = UserDto(userId, "John Updated")
        
        coEvery { mockUserDao.getUserById(userId) } returns expiredUser
        coEvery { mockApiService.getUser(userId) } returns freshUser
        coEvery { mockUserDao.insertUser(any()) } just Runs

        // When
        val result = repository.getUser(userId)

        // Then
        assertThat(result.isSuccess).isTrue()
        coVerify { mockApiService.getUser(userId) }
        coVerify { mockUserDao.insertUser(any()) }
    }
}
```

### Use Case Test Example

```kotlin
@ExperimentalCoroutinesApi
class GetUserUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var useCase: GetUserUseCase
    private lateinit var mockRepository: UserRepository

    @Before
    fun setup() {
        mockRepository = mockk()
        useCase = GetUserUseCase(mockRepository, mainDispatcherRule.testDispatcher)
    }

    @Test
    fun `invoke returns success when repository succeeds`() = runTest {
        // Given
        val userId = "123"
        val expectedUser = User(userId, "John Doe")
        coEvery { mockRepository.getUser(userId) } returns Result.success(expectedUser)

        // When
        val result = useCase(userId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedUser)
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Given
        val userId = "123"
        val exception = NetworkException("Network error")
        coEvery { mockRepository.getUser(userId) } returns Result.failure(exception)

        // When
        val result = useCase(userId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(NetworkException::class.java)
    }
}
```

## Writing Instrumentation Tests

### Fragment Test Example

```kotlin
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @get:Rule
    val fragmentScenario = launchFragmentInContainer<HomeFragment>()

    @Test
    fun testFragmentIsDisplayed() {
        onView(withId(R.id.text_home))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testTextIsCorrect() {
        onView(withId(R.id.text_home))
            .check(matches(withText("This is home Fragment")))
    }
}
```

### Navigation Test Example

```kotlin
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @Test
    fun testNavigationFromHomeToDetail() {
        // Start on HomeFragment
        val scenario = launchFragmentInContainer<HomeFragment>()

        // Click button to navigate
        onView(withId(R.id.button_detail))
            .perform(click())

        // Verify navigation happened
        onView(withId(R.id.detail_container))
            .check(matches(isDisplayed()))
    }
}
```

## Testing Best Practices

### Do's ✅
- Write tests first (TDD when possible)
- Test behavior, not implementation
- Use meaningful test names
- Follow Given-When-Then pattern
- Mock external dependencies
- Test edge cases and error scenarios
- Use coroutine test dispatchers
- Clean up resources in @After
- Use InstantTaskExecutorRule for LiveData
- Use Truth or AssertJ for fluent assertions

### Don'ts ❌
- Don't test framework code
- Don't create brittle tests
- Don't ignore failing tests
- Don't test implementation details
- Don't use real network calls
- Don't hardcode test data
- Don't skip error cases
- Don't use Thread.sleep() in tests
- Don't test multiple concerns in one test

## Test Structure

### AAA Pattern (Arrange-Act-Assert)

```kotlin
@Test
fun `descriptive test name`() = runTest {
    // Arrange (Given): Set up test conditions
    val userId = "123"
    val expectedUser = User(userId, "John")
    coEvery { mockRepository.getUser(userId) } returns Result.success(expectedUser)

    // Act (When): Execute the code under test
    val result = useCase(userId)

    // Assert (Then): Verify the outcome
    assertThat(result.isSuccess).isTrue()
    assertThat(result.getOrNull()).isEqualTo(expectedUser)
}
```

## Mocking

### MockK Examples

```kotlin
// Create mock
val mockRepository = mockk<UserRepository>()

// Stub method
coEvery { mockRepository.getUser(any()) } returns Result.success(user)

// Verify call
coVerify { mockRepository.getUser("123") }

// Verify call count
coVerify(exactly = 2) { mockRepository.getUser(any()) }

// Verify no interaction
coVerify(exactly = 0) { mockRepository.deleteUser(any()) }

// Stub with exception
coEvery { mockRepository.getUser(any()) } throws NetworkException()

// Relaxed mock (returns default values)
val mockRepository = mockk<UserRepository>(relaxed = true)
```

## Test Coverage

### View Coverage Report
```bash
./gradlew testDebugUnitTest jacocoTestReport
open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

### Coverage Goals
- **Overall**: 70%+
- **Use Cases**: 90%+
- **ViewModels**: 80%+
- **Repositories**: 70%+
- **UI**: 50%+

## Continuous Integration

Tests run automatically on:
- Pull requests
- Pushes to main/develop
- Release builds

See `.github/workflows/android-ci.yml` for CI configuration.

## Troubleshooting

### Tests Not Running
```bash
# Clean and rebuild
./gradlew clean test

# Invalidate caches in Android Studio
File > Invalidate Caches / Restart
```

### Flaky Tests
- Use `runTest` for coroutine tests
- Avoid `Thread.sleep()`
- Use `advanceUntilIdle()` for coroutine testing
- Mock time-dependent operations

### Out of Memory
```bash
# Increase memory in gradle.properties
org.gradle.jvmargs=-Xmx4096m
```

## Resources

- [Android Testing Fundamentals](https://developer.android.com/training/testing/fundamentals)
- [Testing Coroutines](https://developer.android.com/kotlin/coroutines/test)
- [MockK Documentation](https://mockk.io/)
- [Truth Assertions](https://truth.dev/)
- [Espresso UI Testing](https://developer.android.com/training/testing/espresso)
