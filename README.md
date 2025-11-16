# Kivara - Android Best Practices Implementation

[![Android CI](https://github.com/horcruxsys/kivara/actions/workflows/android-ci.yml/badge.svg)](https://github.com/horcruxsys/kivara/actions/workflows/android-ci.yml)
[![Android Release](https://github.com/horcruxsys/kivara/actions/workflows/android-release.yml/badge.svg)](https://github.com/horcruxsys/kivara/actions/workflows/android-release.yml)

A production-ready Android application implementing comprehensive best practices for scalability, security, performance, and maintainability.

## 🚀 Key Features

### Architecture & Design
- **Clean Architecture** with clear separation of concerns
- **MVVM Pattern** with ViewModels and LiveData
- **Multi-Module Architecture** ready structure (feature modules, core modules)
- **Dependency Injection** with Dagger Hilt
- **SOLID Principles** throughout the codebase

### Development Tools
- **Gradle Version Catalog** for centralized dependency management
- **Type-safe dependency accessors**
- **KSP** for faster annotation processing
- **Kotlin Coroutines** for asynchronous operations

### Logging
- **Timber** integration for production-grade logging
- Proper log level usage (VERBOSE, DEBUG, INFO, WARN, ERROR)
- **Automatic log removal** in release builds via ProGuard/R8
- Debug and Release logging strategies

### Performance
- **R8 code shrinking and obfuscation** enabled
- Resource shrinking for smaller APK/AAB size
- **LeakCanary** integration for memory leak detection (debug only)
- Optimized build configuration with Java 17
- Core library desugaring for Java 8+ API support

### Security
- **EncryptedSharedPreferences** for secure data storage
- Android Keystore integration ready
- **Certificate pinning** configuration for network security
- **TLS 1.2/1.3** support
- Code obfuscation with comprehensive ProGuard rules
- `usesCleartextTraffic` disabled

### Testing
- **Unit tests** with JUnit, MockK, and Truth
- **Coroutine testing** with TestDispatcher
- **UI tests** infrastructure ready with Espresso
- Test coverage reporting setup
- **InstantTaskExecutorRule** for LiveData testing

### CI/CD
- **GitHub Actions** workflows for:
  - Automated builds and tests
  - Matrix builds for multiple API levels (24, 29, 33, 35)
  - Lint checks with Android Lint
  - Static analysis with Detekt
  - Code formatting with ktlint
  - Automated release builds (APK & AAB)
  - Artifact management

### Code Quality
- **Detekt** for Kotlin static analysis
- **ktlint** for code formatting
- **Android Lint** configuration
- Comprehensive ProGuard/R8 rules

### Google Play Compliance
- ✅ **Target API Level 35** (Android 15) - Required by August 31, 2025
- ✅ **Android App Bundle (.aab)** format ready
- ✅ Security and privacy best practices implemented
- Ready for Android Vitals monitoring

## 📋 Requirements

- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 17 or later
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35 (Android 15)
- **Compile SDK**: API 35

## 🏗️ Project Structure

```
kivara/
├── app/                              # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/horcruxsys/kivara/
│   │   │   │   ├── di/              # Dependency Injection modules
│   │   │   │   ├── data/            # Data layer (repositories, data sources)
│   │   │   │   │   ├── local/       # Local data sources (Room, SharedPrefs)
│   │   │   │   │   ├── remote/      # Remote data sources (Retrofit, APIs)
│   │   │   │   │   └── repository/  # Repository implementations
│   │   │   │   ├── domain/          # Domain layer (use cases, models)
│   │   │   │   │   ├── model/       # Domain models
│   │   │   │   │   └── usecase/     # Business logic use cases
│   │   │   │   ├── presentation/    # Presentation layer (UI)
│   │   │   │   │   └── ui/          # Fragments, Activities, ViewModels
│   │   │   │   └── util/            # Utility classes
│   │   │   └── res/                 # Resources
│   │   ├── test/                    # Unit tests
│   │   └── androidTest/             # Instrumentation tests
│   ├── build.gradle.kts             # App module build configuration
│   └── proguard-rules.pro           # ProGuard/R8 rules
├── config/
│   └── detekt/                      # Detekt configuration
│       ├── detekt.yml
│       └── baseline.xml
├── .github/
│   └── workflows/                   # GitHub Actions workflows
│       ├── android-ci.yml           # CI workflow
│       └── android-release.yml      # Release workflow
├── gradle/
│   └── libs.versions.toml           # Version catalog
├── build.gradle.kts                 # Root build configuration
└── settings.gradle.kts              # Gradle settings
```

## 🔧 Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/horcruxsys/kivara.git
   cd kivara
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Click "Open an existing Android Studio project"
   - Select the cloned directory

3. **Sync Gradle**
   - Wait for Gradle to sync dependencies
   - If you encounter issues, try: `File > Invalidate Caches / Restart`

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the Run button or press `Shift + F10`

## 🧪 Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

### Generate Coverage Report
```bash
./gradlew jacocoTestReport
```
Coverage reports will be available in `app/build/reports/jacoco/`

## 🔍 Code Quality Checks

### Run All Checks
```bash
./gradlew check
```

### Android Lint
```bash
./gradlew lint
```
Reports: `app/build/reports/lint-results.html`

### Detekt
```bash
./gradlew detekt
```
Reports: `app/build/reports/detekt/`

### ktlint
```bash
./gradlew ktlintCheck
```

### Auto-format with ktlint
```bash
./gradlew ktlintFormat
```

## 📦 Building

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build (APK)
```bash
./gradlew assembleRelease
```

### Release Build (AAB for Play Store)
```bash
./gradlew bundleRelease
```

## 🔐 Release Signing

To sign release builds, add the following to your `local.properties`:

```properties
KEYSTORE_FILE=/path/to/keystore.jks
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

For GitHub Actions, add these as repository secrets:
- `KEYSTORE_BASE64`: Base64-encoded keystore file
- `KEYSTORE_PASSWORD`: Keystore password
- `KEY_ALIAS`: Key alias
- `KEY_PASSWORD`: Key password

## 📚 Documentation

### Architecture Decision Records (ADRs)
Architecture decisions are documented in the `docs/adr/` directory (coming soon).

### Code Documentation
- Code is documented using KDoc format
- View generated documentation: `./gradlew dokkaHtml`

## 🔒 Security Features

### Implemented
- ✅ Encrypted SharedPreferences for secure local storage
- ✅ Certificate pinning configuration ready
- ✅ Code obfuscation with R8
- ✅ Logging removed in release builds
- ✅ Clear text traffic disabled
- ✅ Secure network communication (TLS 1.2/1.3)

### TODO
- [ ] Implement biometric authentication
- [ ] Add OAuth 2.0 with PKCE
- [ ] Integrate Firebase Crashlytics
- [ ] Add ProGuard mapping upload to Play Console

## 🚀 CI/CD Pipeline

GitHub Actions workflows are configured for:

### Continuous Integration
- Build verification on push/PR
- Unit and instrumentation tests
- Code quality checks (Lint, Detekt, ktlint)
- Test coverage reporting
- Multi-API level testing (24, 29, 33, 35)

### Continuous Deployment
- Automated release builds on version tags
- APK and AAB artifact generation
- Mapping file preservation for debugging
- GitHub Release creation

## 📈 Performance Optimization

- **R8 Shrinking**: Reduces APK size by 30-40%
- **Resource Shrinking**: Removes unused resources
- **Code Obfuscation**: Protects intellectual property
- **ProGuard Rules**: Optimized for Kotlin and AndroidX
- **LeakCanary**: Memory leak detection in debug builds

## 🔄 Dependencies

All dependencies are managed via Gradle Version Catalog (`gradle/libs.versions.toml`).

### Major Dependencies
- **AndroidX**: Latest stable versions
- **Hilt**: 2.51.1 (Dependency Injection)
- **Retrofit**: 2.11.0 (Network)
- **OkHttp**: 4.12.0 (HTTP Client)
- **Room**: 2.6.1 (Database)
- **Coroutines**: 1.8.1 (Async)
- **Timber**: 5.0.1 (Logging)
- **LeakCanary**: 2.14 (Memory Leaks)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Android Jetpack components
- Open source community
- Contributors to this project

## 📞 Contact

- **Project Link**: [https://github.com/horcruxsys/kivara](https://github.com/horcruxsys/kivara)
- **Issues**: [https://github.com/horcruxsys/kivara/issues](https://github.com/horcruxsys/kivara/issues)

---

**Made with ❤️ by the Kivara team**
