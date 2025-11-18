# Build Requirements

## Prerequisites

### Required Software
- **JDK 17**: Required for Kotlin 2.0.21 and Android Gradle Plugin 8.5.2
- **Android SDK**: API Level 35 (Android 15) for compilation
- **Gradle**: Version 8.13 (automatically downloaded by wrapper)

### Network Access Required
The build process requires internet access to download:
- Android Gradle Plugin (AGP) 8.5.2
- Kotlin compiler 2.0.21
- Android SDK components (Build Tools, Platform Tools)
- All project dependencies from Maven Central and Google repositories

## Build Commands

### Clean Build
```bash
./gradlew clean build
```

### Run Tests
```bash
# Unit tests
./gradlew test

# Generate coverage report
./gradlew jacocoTestReport

# Instrumentation tests (requires emulator or device)
./gradlew connectedAndroidTest
```

### Code Quality Checks
```bash
# Android Lint
./gradlew lint

# Detekt (static analysis)
./gradlew detekt

# ktlint (code formatting)
./gradlew ktlintCheck
./gradlew ktlintFormat  # Auto-fix formatting issues
```

### Build Release APK/AAB
```bash
# Debug APK
./gradlew assembleDebug

# Release APK (requires signing configuration)
./gradlew assembleRelease

# Release AAB for Play Store
./gradlew bundleRelease
```

## Known Build Issues

### Network Restrictions
If you encounter errors like:
```
Plugin [id: 'com.android.application', version: '8.5.2'] was not found
```

This indicates that Gradle cannot access the required repositories. Ensure:
1. Internet connection is available
2. No firewall blocking `google()`, `mavenCentral()`, or `gradlePluginPortal()`
3. Proxy settings are configured if required

### Environment-Specific Issues

#### CI/CD Environment
- Ensure Android SDK is installed or use appropriate Docker images
- Example: `cimg/android:2024.01.1` or `ubuntu-latest` with `android-sdk` action

#### Local Development
- Install Android Studio with SDK Manager
- Accept all SDK licenses: `sdkmanager --licenses`
- Set `ANDROID_HOME` environment variable

## Troubleshooting

### Build Fails with "SDK not found"
```bash
export ANDROID_HOME=/path/to/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

### Dependency Resolution Issues
```bash
# Clear Gradle cache
./gradlew clean --refresh-dependencies

# Delete .gradle folder if needed
rm -rf ~/.gradle/caches
```

### Certificate Pinning Configuration Error
The NetworkModule includes certificate pinning setup that is commented out by default.
To enable:
1. Generate certificate pins for your API domain
2. Uncomment the `.certificatePinner(certificatePinner)` line in `NetworkModule.kt`
3. Add actual pins in `provideCertificatePinner()` method

## Build Performance Tips

- Use Gradle daemon (enabled by default)
- Increase Gradle heap size in `gradle.properties`:
  ```properties
  org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g
  ```
- Enable configuration cache:
  ```properties
  org.gradle.configuration-cache=true
  ```

## Support

For build issues:
1. Check GitHub Actions logs for detailed error messages
2. Verify all prerequisites are met
3. Try cleaning and rebuilding: `./gradlew clean build`
4. Check the [Gradle troubleshooting guide](https://docs.gradle.org/current/userguide/troubleshooting.html)
