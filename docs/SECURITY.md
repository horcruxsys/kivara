# Security Best Practices

This document outlines the security measures implemented in the Kivara Android application.

## 🔐 Implemented Security Features

### 1. Encrypted Storage
- **EncryptedSharedPreferences** for secure local data storage
- Uses AES256-GCM for encryption
- Master keys managed by Android Keystore
- Implementation: `di/AppModule.kt`

```kotlin
// Example usage
@Inject lateinit var securePrefs: SharedPreferences

// Writing encrypted data
securePrefs.edit().putString("api_key", "secret_value").apply()

// Reading encrypted data
val apiKey = securePrefs.getString("api_key", null)
```

### 2. Network Security

#### Certificate Pinning
- Configured in `di/NetworkModule.kt`
- Prevents Man-in-the-Middle (MITM) attacks
- TODO: Add actual certificate pins for production API

```kotlin
// To get certificate pins, run:
// openssl s_client -servername api.example.com -connect api.example.com:443 \
//   | openssl x509 -pubkey -noout | openssl rsa -pubin -outform der \
//   | openssl dgst -sha256 -binary | openssl enc -base64
```

#### TLS Configuration
- Enforces TLS 1.2/1.3
- Configured via OkHttpClient
- Clear text traffic disabled in manifest

#### Network Security Config
Create `res/xml/network_security_config.xml` for additional security:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
    <domain-config>
        <domain includeSubdomains="true">api.example.com</domain>
        <pin-set>
            <pin digest="SHA-256">AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=</pin>
            <pin digest="SHA-256">BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=</pin>
        </pin-set>
    </domain-config>
</network-security-config>
```

### 3. Code Obfuscation

#### R8/ProGuard
- **Enabled in release builds**: `isMinifyEnabled = true`
- **Resource shrinking**: `isShrinkResources = true`
- Comprehensive rules in `app/proguard-rules.pro`

#### Protected Elements
- All logging removed in release builds
- API models and data classes preserved
- Kotlin metadata maintained for reflection
- Serialization classes kept

### 4. Logging Security

#### Timber Configuration
- Debug builds: Full logging enabled
- Release builds: Only WARN and ERROR logs
- All logs stripped by R8 in production
- Custom ReleaseTree for crash reporting integration

#### Log Removal
ProGuard rules remove all log statements:
```proguard
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
}
```

### 5. Build Configuration

#### Debug vs Release
- **Debug**: 
  - Additional logging
  - LeakCanary enabled
  - Different application ID suffix
  - No obfuscation

- **Release**:
  - Minimal logging
  - No LeakCanary
  - Full obfuscation
  - Code and resource shrinking

### 6. Manifest Security

```xml
<!-- Disabled cleartext traffic -->
android:usesCleartextTraffic="false"

<!-- Backup security -->
android:allowBackup="true"
android:dataExtractionRules="@xml/data_extraction_rules"
android:fullBackupContent="@xml/backup_rules"
```

## 🔒 Security Checklist

### Pre-Release Security Review

- [ ] Certificate pinning configured with production pins
- [ ] All API keys stored securely (not hardcoded)
- [ ] ProGuard/R8 rules tested with release build
- [ ] Network security config validated
- [ ] Sensitive data encrypted at rest
- [ ] HTTPS enforced for all network calls
- [ ] WebView security configured (if applicable)
- [ ] Deep links validated and secured
- [ ] File provider configured correctly
- [ ] Permissions requested are minimal and necessary
- [ ] Content providers are not exported unnecessarily
- [ ] Broadcast receivers are protected
- [ ] SQL injection prevented (use Room with parameterized queries)
- [ ] No sensitive data in logs
- [ ] Crash reports don't contain sensitive data

## 🚨 Security Vulnerabilities to Avoid

### 1. Hardcoded Secrets
❌ **DON'T**:
```kotlin
const val API_KEY = "sk_live_1234567890abcdef"
```

✅ **DO**:
```kotlin
// Store in local.properties or use remote config
val apiKey = BuildConfig.API_KEY
// Or use encrypted shared preferences
val apiKey = securePrefs.getString("api_key", null)
```

### 2. SQL Injection
❌ **DON'T**:
```kotlin
val query = "SELECT * FROM users WHERE name = '$userName'"
```

✅ **DO**:
```kotlin
@Query("SELECT * FROM users WHERE name = :userName")
fun getUserByName(userName: String): User?
```

### 3. Insecure Data Storage
❌ **DON'T**:
```kotlin
val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
prefs.edit().putString("password", password).apply()
```

✅ **DO**:
```kotlin
// Use EncryptedSharedPreferences
securePrefs.edit().putString("password", password).apply()
```

### 4. Weak Cryptography
❌ **DON'T**:
```kotlin
// DES, MD5, SHA1 are deprecated
```

✅ **DO**:
```kotlin
// Use Android Keystore with AES256-GCM
// Use EncryptedSharedPreferences
```

## 🔐 Additional Security Measures (TODO)

### High Priority
1. **Biometric Authentication**
   - Implement fingerprint/face authentication for sensitive operations
   - Use `androidx.biometric:biometric` library

2. **OAuth 2.0 with PKCE**
   - Implement secure authentication flow
   - Use AppAuth library for Android

3. **Firebase Crashlytics**
   - Set up crash reporting
   - Configure to exclude sensitive data from reports
   - Disable crash reporting for debug builds

### Medium Priority
4. **Root Detection**
   - Detect rooted devices
   - Warn users about security risks
   - Consider SafetyNet Attestation API

5. **Tampering Detection**
   - Verify app signature
   - Detect if app has been modified
   - Check for debugging

6. **Runtime Application Self-Protection (RASP)**
   - Monitor for security threats at runtime
   - Detect and respond to attacks

### Low Priority
7. **ProGuard Mapping Upload**
   - Automate mapping file upload to Play Console
   - Enable proper crash deobfuscation

8. **Security Testing**
   - Integrate OWASP dependency check
   - Regular penetration testing
   - Security code reviews

## 📚 References

- [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)
- [OWASP Mobile Security Testing Guide](https://owasp.org/www-project-mobile-security-testing-guide/)
- [Android Security Tips](https://developer.android.com/training/articles/security-tips)
- [EncryptedSharedPreferences](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences)
- [Network Security Configuration](https://developer.android.com/training/articles/security-config)

## 🆘 Security Issues

If you discover a security vulnerability, please email security@horcruxsys.com instead of creating a public issue.
