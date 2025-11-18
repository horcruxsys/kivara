# Contributing to Kivara

Thank you for considering contributing to Kivara! This document provides guidelines and instructions for contributing.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Commit Messages](#commit-messages)
- [Pull Request Process](#pull-request-process)

## Code of Conduct

This project adheres to a code of conduct. By participating, you are expected to uphold this code. Please report unacceptable behavior to the project maintainers.

## Getting Started

1. **Fork the repository**
   ```bash
   # Click the "Fork" button on GitHub
   ```

2. **Clone your fork**
   ```bash
   git clone https://github.com/YOUR-USERNAME/kivara.git
   cd kivara
   ```

3. **Add upstream remote**
   ```bash
   git remote add upstream https://github.com/horcruxsys/kivara.git
   ```

4. **Create a branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

## Development Workflow

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Git

### Setup Development Environment

1. **Open in Android Studio**
   - File → Open → Select the kivara directory
   - Wait for Gradle sync to complete

2. **Verify Setup**
   ```bash
   ./gradlew build
   ./gradlew test
   ```

### Running the App
```bash
# Debug build
./gradlew installDebug

# Or use Android Studio's Run button
```

### Code Quality Checks
```bash
# Run all checks
./gradlew check

# Individual checks
./gradlew lint
./gradlew detekt
./gradlew ktlintCheck
```

## Coding Standards

### Kotlin Style Guide

Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) and [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide).

#### Key Points:
- Use 4 spaces for indentation
- Maximum line length: 120 characters
- Use meaningful variable and function names
- Prefer `val` over `var` when possible
- Use trailing commas in multi-line declarations

#### Example:
```kotlin
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val apiService: ApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun getUser(userId: String): Result<User> = withContext(ioDispatcher) {
        try {
            val user = apiService.getUser(userId)
            userDao.insertUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Architecture Guidelines

#### Clean Architecture Layers
1. **Presentation Layer** (`presentation/`)
   - ViewModels, Fragments, Activities
   - Only UI logic

2. **Domain Layer** (`domain/`)
   - Use cases, domain models
   - Pure Kotlin, no Android dependencies

3. **Data Layer** (`data/`)
   - Repositories, data sources
   - API and database implementations

#### Dependency Rule
- Presentation → Domain ← Data
- Inner layers don't know about outer layers

### Code Documentation

Use KDoc for public APIs:

```kotlin
/**
 * Fetches user data from the remote server and caches it locally.
 *
 * @param userId The unique identifier of the user
 * @return [Result] containing [User] on success or exception on failure
 * @throws NetworkException if the network is unavailable
 */
suspend fun getUser(userId: String): Result<User>
```

### Naming Conventions

#### Classes
- `PascalCase` for class names
- Suffix ViewModels with `ViewModel`
- Suffix Repositories with `Repository`
- Suffix Use Cases with `UseCase`

#### Functions
- `camelCase` for function names
- Use verb or verb phrases
- Boolean functions start with `is`, `has`, `should`

#### Variables
- `camelCase` for variables
- Constants in `UPPER_SNAKE_CASE`

#### Resources
- `snake_case` for all resource names
- Prefix with type: `fragment_home.xml`, `ic_launcher.xml`

## Testing Guidelines

### Test Pyramid
- **Unit Tests (70%)**: Fast, isolated tests
- **Integration Tests (20%)**: Test component interactions
- **UI Tests (10%)**: Test user flows

### Unit Tests

```kotlin
@ExperimentalCoroutinesApi
class UserRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: UserRepository
    private lateinit var mockApiService: ApiService
    private lateinit var mockUserDao: UserDao

    @Before
    fun setup() {
        mockApiService = mockk()
        mockUserDao = mockk()
        repository = UserRepository(mockUserDao, mockApiService, Dispatchers.Main)
    }

    @Test
    fun `getUser returns user on success`() = runTest {
        // Given
        val userId = "123"
        val expectedUser = User(userId, "John Doe")
        coEvery { mockApiService.getUser(userId) } returns expectedUser
        coEvery { mockUserDao.insertUser(any()) } just Runs

        // When
        val result = repository.getUser(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
    }
}
```

### Test Coverage
- Aim for 70%+ code coverage
- Focus on business logic in use cases
- Test edge cases and error scenarios

### Running Tests
```bash
# All tests
./gradlew test

# Specific test class
./gradlew test --tests UserRepositoryTest

# With coverage
./gradlew testDebugUnitTest jacocoTestReport
```

## Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

### Format
```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Build process or auxiliary tool changes
- `perf`: Performance improvements
- `ci`: CI/CD changes

### Examples

```
feat(auth): add biometric authentication

Implement fingerprint and face authentication for login screen.
Uses androidx.biometric library for compatibility across devices.

Closes #123
```

```
fix(network): handle timeout exceptions properly

Add timeout handling in NetworkModule to prevent app crashes
when network requests take too long.

Fixes #456
```

## Pull Request Process

### Before Submitting

1. **Update from upstream**
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Run all checks**
   ```bash
   ./gradlew check
   ./gradlew test
   ```

3. **Fix code quality issues**
   ```bash
   ./gradlew ktlintFormat
   ```

### PR Checklist

- [ ] Code follows the style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated (if needed)
- [ ] No new warnings introduced
- [ ] Tests added/updated
- [ ] All tests pass
- [ ] No merge conflicts
- [ ] PR title follows conventional commits
- [ ] Description explains what and why

### PR Template

```markdown
## Description
[Describe the changes made]

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
[Describe the tests you ran]

## Screenshots (if applicable)
[Add screenshots for UI changes]

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Tests added/updated
- [ ] Documentation updated
```

### Review Process

1. **Automated Checks**
   - CI/CD pipeline runs automatically
   - All checks must pass

2. **Code Review**
   - At least one approval required
   - Address all review comments

3. **Merge**
   - Squash and merge
   - Delete branch after merge

## Questions?

- Create an issue for bugs or feature requests
- Join our discussions for questions
- Email: dev@horcruxsys.com

## License

By contributing, you agree that your contributions will be licensed under the MIT License.
