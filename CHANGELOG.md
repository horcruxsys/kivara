# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Complete Android best practices implementation
- Clean Architecture with MVVM pattern
- Dagger Hilt for dependency injection
- Timber logging with production configuration
- R8 code shrinking and obfuscation
- Comprehensive ProGuard/R8 rules
- EncryptedSharedPreferences for secure storage
- Certificate pinning configuration
- GitHub Actions CI/CD workflows
- Detekt and ktlint configurations
- Unit test infrastructure with coroutine support
- LeakCanary for memory leak detection
- Comprehensive documentation (README, ARCHITECTURE, SECURITY, TESTING, PERFORMANCE, CONTRIBUTING)
- Architecture Decision Records (ADRs)
- API Level 35 targeting for Google Play compliance

### Changed
- Updated Gradle Version Catalog with all modern dependencies
- Migrated to KSP from KAPT for faster builds
- Updated to Java 17 for better performance
- Improved build configuration with optimizations

### Security
- Added EncryptedSharedPreferences for secure data storage
- Implemented certificate pinning for network security
- Disabled cleartext traffic
- Added comprehensive security documentation
- Implemented proper log removal in release builds

## [1.0.0] - YYYY-MM-DD

### Added
- Initial release
- Basic app structure with bottom navigation
- Home, Dashboard, and Notifications screens

---

## Types of Changes

- **Added**: New features
- **Changed**: Changes in existing functionality
- **Deprecated**: Soon-to-be removed features
- **Removed**: Removed features
- **Fixed**: Bug fixes
- **Security**: Security improvements

## Version Format

Version numbers follow Semantic Versioning (MAJOR.MINOR.PATCH):
- **MAJOR**: Incompatible API changes
- **MINOR**: Backward-compatible functionality additions
- **PATCH**: Backward-compatible bug fixes

## Links

- [Unreleased]: https://github.com/horcruxsys/kivara/compare/v1.0.0...HEAD
- [1.0.0]: https://github.com/horcruxsys/kivara/releases/tag/v1.0.0
