# Quick Start Guide

Get up and running with Kivara in 5 minutes!

## Prerequisites

- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 17 or later
- **Git**: Latest version

## Setup Steps

### 1. Clone the Repository

```bash
git clone https://github.com/horcruxsys/kivara.git
cd kivara
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Select **File → Open**
3. Navigate to the cloned `kivara` directory
4. Click **OK**

### 3. Sync Gradle

Android Studio will automatically start syncing Gradle. Wait for it to complete.

If sync fails:
- **File → Invalidate Caches / Restart**
- Try again

### 4. Run the App

1. Connect an Android device or start an emulator
2. Click the **Run** button (▶️) or press `Shift + F10`
3. Select your device
4. Wait for the app to build and install

## Project Structure Overview

```
kivara/
├── app/                        # Main application module
│   ├── src/main/              # Production code
│   │   ├── java/              # Kotlin source files
│   │   └── res/               # Resources
│   ├── src/test/              # Unit tests
│   └── src/androidTest/       # Instrumentation tests
├── docs/                       # Documentation
├── config/                     # Configuration files
└── .github/                    # GitHub workflows
```

## Key Files to Know

### Build Configuration
- `gradle/libs.versions.toml` - Dependency versions
- `app/build.gradle.kts` - App module configuration
- `build.gradle.kts` - Root project configuration

### Code
- `app/src/main/java/com/horcruxsys/kivara/KivaraApplication.kt` - Application class
- `app/src/main/java/com/horcruxsys/kivara/di/` - Dependency injection modules
- `app/src/main/java/com/horcruxsys/kivara/MainActivity.kt` - Main activity

### Documentation
- `README.md` - Project overview
- `CONTRIBUTING.md` - Contributing guidelines
- `docs/ARCHITECTURE.md` - Architecture documentation
- `docs/SECURITY.md` - Security guidelines
- `docs/TESTING.md` - Testing guide

## Common Tasks

### Build the App

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

### Run Tests

```bash
# Unit tests
./gradlew test

# Instrumentation tests
./gradlew connectedAndroidTest

# All tests
./gradlew check
```

### Code Quality

```bash
# Format code
./gradlew ktlintFormat

# Run lint
./gradlew lint

# Run detekt
./gradlew detekt

# Run all checks
./gradlew check
```

### Clean Build

```bash
./gradlew clean
```

## Making Your First Change

### 1. Create a Branch

```bash
git checkout -b feature/my-first-change
```

### 2. Make Changes

Edit any file, for example:
- `app/src/main/res/values/strings.xml` - Change app name
- `app/src/main/java/com/horcruxsys/kivara/ui/home/HomeFragment.kt` - Modify home screen

### 3. Format Code

```bash
./gradlew ktlintFormat
```

### 4. Run Tests

```bash
./gradlew test
```

### 5. Commit Changes

```bash
git add .
git commit -m "feat: my first change"
```

### 6. Push to GitHub

```bash
git push origin feature/my-first-change
```

### 7. Create Pull Request

1. Go to GitHub
2. Click **Compare & pull request**
3. Fill in the PR template
4. Submit for review

## Understanding the Architecture

Kivara uses **Clean Architecture** with three layers:

```
┌─────────────────┐
│  Presentation   │  ← UI (Fragments, ViewModels)
├─────────────────┤
│     Domain      │  ← Business Logic (Use Cases)
├─────────────────┤
│      Data       │  ← Data Access (Repositories, APIs)
└─────────────────┘
```

### Adding a New Feature

1. **Domain Layer**: Create use case
   ```kotlin
   // domain/usecase/GetUserUseCase.kt
   class GetUserUseCase @Inject constructor(
       private val repository: UserRepository
   ) {
       suspend operator fun invoke(userId: String) = 
           repository.getUser(userId)
   }
   ```

2. **Data Layer**: Implement repository
   ```kotlin
   // data/repository/UserRepositoryImpl.kt
   class UserRepositoryImpl @Inject constructor(
       private val apiService: ApiService
   ) : UserRepository {
       override suspend fun getUser(userId: String) = 
           apiService.getUser(userId)
   }
   ```

3. **Presentation Layer**: Use in ViewModel
   ```kotlin
   // presentation/ui/profile/ProfileViewModel.kt
   @HiltViewModel
   class ProfileViewModel @Inject constructor(
       private val getUserUseCase: GetUserUseCase
   ) : ViewModel() {
       fun loadUser(userId: String) {
           viewModelScope.launch {
               val user = getUserUseCase(userId)
               // Update UI state
           }
       }
   }
   ```

## Debugging Tips

### Logcat Filters

In Android Studio Logcat:
- **Filter by tag**: `tag:Kivara`
- **Filter by level**: Select `Debug`, `Info`, `Warn`, or `Error`
- **Filter by package**: `package:com.horcruxsys.kivara`

### Using Timber

```kotlin
Timber.d("Debug message")
Timber.i("Info message")
Timber.w("Warning message")
Timber.e(exception, "Error message")
```

### Breakpoint Debugging

1. Click on line number to add breakpoint (red dot)
2. Run app in debug mode (🐛 icon)
3. Use debug controls to step through code

## Common Issues

### Build Failed: "Could not resolve..."

**Solution**: Check internet connection and sync Gradle
```bash
./gradlew clean build --refresh-dependencies
```

### App Crashes on Start

**Solution**: Check Logcat for error messages
- Look for red error messages
- Check stack trace for source of crash

### Tests Fail

**Solution**: Clean and rebuild
```bash
./gradlew clean test
```

### IDE Slow/Unresponsive

**Solution**: Increase memory
Edit `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m
```

## Getting Help

- 📖 **Documentation**: `docs/` directory
- 🐛 **Bug Reports**: Create an issue on GitHub
- 💡 **Feature Requests**: Create an issue on GitHub
- 💬 **Questions**: GitHub Discussions
- 📧 **Email**: dev@horcruxsys.com

## Next Steps

1. ✅ Read [ARCHITECTURE.md](docs/ARCHITECTURE.md) to understand the app structure
2. ✅ Review [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guidelines
3. ✅ Check [TESTING.md](docs/TESTING.md) for testing best practices
4. ✅ Explore the codebase and existing features
5. ✅ Make your first contribution!

## Resources

- [Android Developer Guide](https://developer.android.com/guide)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Dagger Hilt](https://dagger.dev/hilt/)
- [Jetpack Components](https://developer.android.com/jetpack)

---

**Happy Coding! 🚀**
