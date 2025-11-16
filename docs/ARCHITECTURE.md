# Kivara Architecture

## Overview

Kivara follows **Clean Architecture** principles with **MVVM** pattern for the presentation layer, ensuring a scalable, maintainable, and testable codebase.

## Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Fragments   │  │  Activities  │  │  ViewModels  │      │
│  │   (Views)    │  │   (Views)    │  │   (Logic)    │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                       Domain Layer                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Use Cases   │  │Domain Models │  │  Repository  │      │
│  │ (Interactors)│  │   (Entities) │  │  Interfaces  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                        Data Layer                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Repositories │  │ Data Sources │  │ Data Models  │      │
│  │    (Impl)    │  │  (API, DB)   │  │   (DTOs)     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

## Layer Responsibilities

### Presentation Layer (`presentation/`)

**Responsibility**: Handle UI and user interactions

**Components**:
- **Views** (Activities/Fragments): Display UI and handle user input
- **ViewModels**: Hold UI state and handle UI logic
- **View State**: Represent UI state in a structured way

**Rules**:
- No business logic
- No direct data access
- Observe ViewModels via LiveData/StateFlow
- Handle only UI-related logic

**Example**:
```kotlin
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> showLoading()
                is UiState.Success -> showData(state.data)
                is UiState.Error -> showError(state.message)
            }
        }
    }
}
```

### Domain Layer (`domain/`)

**Responsibility**: Contain business logic and business rules

**Components**:
- **Use Cases**: Single-purpose business operations
- **Domain Models**: Business entities (pure Kotlin classes)
- **Repository Interfaces**: Contracts for data access

**Rules**:
- Pure Kotlin (no Android dependencies)
- Independent of frameworks
- Testable without Android
- Single Responsibility Principle

**Example**:
```kotlin
class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(userId: String): Result<User> = 
        withContext(ioDispatcher) {
            try {
                val user = userRepository.getUser(userId)
                Result.success(user)
            } catch (e: Exception) {
                Timber.e(e, "Failed to get user")
                Result.failure(e)
            }
        }
}
```

### Data Layer (`data/`)

**Responsibility**: Provide data to the domain layer

**Components**:
- **Repositories**: Coordinate data from different sources
- **Data Sources**: 
  - Remote (API clients)
  - Local (Database, SharedPreferences)
- **Data Models**: DTOs for API/Database

**Rules**:
- Implement repository interfaces from domain
- Handle data caching strategies
- Map DTOs to domain models
- Manage data consistency

**Example**:
```kotlin
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val mapper: UserMapper
) : UserRepository {
    override suspend fun getUser(userId: String): User {
        // Try cache first
        val cachedUser = userDao.getUserById(userId)
        if (cachedUser != null && !cachedUser.isExpired()) {
            return mapper.toDomain(cachedUser)
        }
        
        // Fetch from network
        val userDto = apiService.getUser(userId)
        val userEntity = mapper.toEntity(userDto)
        
        // Update cache
        userDao.insertUser(userEntity)
        
        return mapper.toDomain(userEntity)
    }
}
```

## Dependency Flow

```
Presentation → Domain ← Data
    ↓           ↓        ↓
 Android    Pure     Android
Framework   Kotlin   Framework
```

**Dependency Rule**: Inner layers don't depend on outer layers

## MVVM Pattern

### Components

1. **Model**: Domain models and business logic
2. **View**: Activities, Fragments (passive view)
3. **ViewModel**: UI logic and state management

### Data Flow

```
User Action → View → ViewModel → Use Case → Repository → Data Source
                ↓                                              ↓
           LiveData/StateFlow ←────────────────────────────────┘
```

## Dependency Injection

### Hilt Setup

```kotlin
// Application
@HiltAndroidApp
class KivaraApplication : Application()

// Activity/Fragment
@AndroidEntryPoint
class HomeFragment : Fragment()

// ViewModel
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel()

// Module
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = ...
}
```

## Package Structure

```
com.horcruxsys.kivara/
├── di/                          # Dependency Injection modules
│   ├── AppModule.kt
│   ├── NetworkModule.kt
│   └── DatabaseModule.kt
├── data/                        # Data Layer
│   ├── local/                   # Local data sources
│   │   ├── database/
│   │   │   ├── dao/
│   │   │   ├── entity/
│   │   │   └── KivaraDatabase.kt
│   │   └── preferences/
│   ├── remote/                  # Remote data sources
│   │   ├── api/
│   │   ├── dto/
│   │   └── interceptor/
│   ├── repository/              # Repository implementations
│   │   └── UserRepositoryImpl.kt
│   └── mapper/                  # Data mappers
├── domain/                      # Domain Layer
│   ├── model/                   # Domain models
│   │   └── User.kt
│   ├── repository/              # Repository interfaces
│   │   └── UserRepository.kt
│   └── usecase/                 # Use cases
│       ├── GetUserUseCase.kt
│       └── SaveUserUseCase.kt
├── presentation/                # Presentation Layer
│   ├── ui/                      # UI components
│   │   ├── home/
│   │   │   ├── HomeFragment.kt
│   │   │   ├── HomeViewModel.kt
│   │   │   └── HomeUiState.kt
│   │   ├── dashboard/
│   │   └── notifications/
│   ├── common/                  # Common UI components
│   │   ├── adapter/
│   │   └── widget/
│   └── util/                    # UI utilities
└── util/                        # Common utilities
    ├── extension/
    ├── constant/
    └── network/
```

## Navigation

### Navigation Component

```kotlin
// Navigation graph
<navigation>
    <fragment
        android:id="@+id/homeFragment"
        android:name="com.horcruxsys.kivara.presentation.ui.home.HomeFragment">
        <action
            android:id="@+id/action_home_to_detail"
            app:destination="@id/detailFragment" />
    </fragment>
</navigation>

// Navigation in code
findNavController().navigate(
    R.id.action_home_to_detail,
    bundleOf("userId" to userId)
)
```

## State Management

### UI State Pattern

```kotlin
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
}
```

### ViewModel State

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {
    
    private val _uiState = MutableLiveData<UiState<User>>(UiState.Idle)
    val uiState: LiveData<UiState<User>> = _uiState
    
    fun loadUser(userId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getUserUseCase(userId)
                .onSuccess { user ->
                    _uiState.value = UiState.Success(user)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
```

## Error Handling

### Result Wrapper

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}
```

### Repository Error Handling

```kotlin
override suspend fun getUser(userId: String): Result<User> {
    return try {
        val user = apiService.getUser(userId)
        Result.Success(user)
    } catch (e: IOException) {
        Timber.e(e, "Network error")
        Result.Error(NetworkException("Network error", e))
    } catch (e: Exception) {
        Timber.e(e, "Unknown error")
        Result.Error(UnknownException("Unknown error", e))
    }
}
```

## Testing Strategy

### Unit Tests
- **Domain Layer**: Test use cases with mock repositories
- **ViewModel**: Test with mock use cases
- **Repository**: Test with mock data sources

### Integration Tests
- **Repository**: Test with real database and mock API
- **Use Case**: Test with real repository

### UI Tests
- **Fragments**: Test with Espresso
- **Navigation**: Test navigation flows
- **End-to-End**: Test complete user journeys

## Best Practices

### Do's ✅
- Single Responsibility Principle
- Dependency Injection everywhere
- Use sealed classes for state
- Handle errors gracefully
- Write comprehensive tests
- Document complex logic
- Use coroutines for async operations
- Follow naming conventions
- Keep ViewModels lifecycle-aware
- Use repository pattern for data access

### Don'ts ❌
- Don't put business logic in Views
- Don't use static instances
- Don't pass Context to ViewModels
- Don't leak Activities/Fragments
- Don't use GlobalScope for coroutines
- Don't skip error handling
- Don't hardcode values
- Don't create god classes
- Don't ignore memory leaks
- Don't mix concerns across layers

## Future Enhancements

### Multi-Module Architecture
```
app/
feature/
  ├── feature-home/
  ├── feature-profile/
  └── feature-settings/
core/
  ├── core-ui/
  ├── core-network/
  ├── core-database/
  └── core-common/
```

### Benefits
- Parallel development
- Faster build times (incremental builds)
- Better separation of concerns
- Code reusability
- Feature isolation
- Dynamic feature delivery

## References

- [Android Architecture Guide](https://developer.android.com/jetpack/guide)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [MVVM Pattern](https://developer.android.com/jetpack/guide#recommended-app-arch)
- [Hilt Documentation](https://dagger.dev/hilt/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
