package com.horcruxsys.kivara

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for Kivara app.
 * This is the entry point for the application and is used to initialize
 * application-wide components like Timber, Hilt, etc.
 */
@HiltAndroidApp
class KivaraApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        initializeTimber()
    }

    /**
     * Initialize Timber logging framework.
     * In debug builds, we plant a DebugTree for detailed logging.
     * In release builds, we can plant a custom tree for crash reporting (e.g., Crashlytics).
     */
    private fun initializeTimber() {
        if (BuildConfig.ENABLE_LOGGING) {
            // Debug builds: Use Timber's DebugTree for detailed logs
            Timber.plant(Timber.DebugTree())
            Timber.d("Timber initialized for debug build")
        } else {
            // Release builds: Plant a custom tree for production logging
            // This tree can send error logs to crash reporting services
            Timber.plant(ReleaseTree())
            Timber.i("Timber initialized for release build")
        }
    }

    /**
     * Custom Timber tree for release builds.
     * Only logs warnings and errors to avoid performance issues.
     * Can be extended to send logs to crash reporting services like Firebase Crashlytics.
     */
    private class ReleaseTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            // Only log warnings and errors in release builds
            if (priority == android.util.Log.WARN || priority == android.util.Log.ERROR) {
                // TODO: Send to crash reporting service (e.g., Firebase Crashlytics)
                // FirebaseCrashlytics.getInstance().log(message)
                // if (t != null) {
                //     FirebaseCrashlytics.getInstance().recordException(t)
                // }
            }
        }
    }
}
