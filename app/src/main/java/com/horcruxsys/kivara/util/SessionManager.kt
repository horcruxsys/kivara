package com.horcruxsys.kivara.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Session manager for handling user authentication state.
 * Uses EncryptedSharedPreferences for secure storage.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Save user session token
     */
    fun saveSessionToken(token: String) {
        sharedPreferences.edit().apply {
            putString(KEY_SESSION_TOKEN, token)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    /**
     * Get current session token
     */
    fun getSessionToken(): String? {
        return sharedPreferences.getString(KEY_SESSION_TOKEN, null)
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Clear session data (logout)
     */
    fun clearSession() {
        sharedPreferences.edit().apply {
            remove(KEY_SESSION_TOKEN)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "kivara_secure_prefs"
        private const val KEY_SESSION_TOKEN = "session_token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
}
