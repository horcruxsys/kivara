# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ================================================================================================
# GENERAL ANDROID OPTIMIZATIONS
# ================================================================================================

# Preserve the line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Rename source file attribute to hide the original source file name
-renamesourcefileattribute SourceFile

# Keep annotations for runtime reflection
-keepattributes *Annotation*

# Keep generic signature for Kotlin reflection
-keepattributes Signature

# Keep exception information
-keepattributes Exceptions

# ================================================================================================
# REMOVE LOGGING IN RELEASE
# ================================================================================================

# Remove verbose, debug, and info Log calls (keep warnings and errors for production debugging)
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
}

# Remove Timber logging (but keep error and warning logs for production debugging)
-assumenosideeffects class timber.log.Timber {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
}

# Remove println statements
-assumenosideeffects class kotlin.io.ConsoleKt {
    public static *** println(...);
}

# ================================================================================================
# KOTLIN SPECIFIC
# ================================================================================================

# Preserve Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

# Kotlin serialization
-keepattributes InnerClasses
-keep,includedescriptorclasses class com.horcruxsys.kivara.**$$serializer { *; }
-keepclassmembers class com.horcruxsys.kivara.** {
    *** Companion;
}
-keepclasseswithmembers class com.horcruxsys.kivara.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ================================================================================================
# ANDROIDX & ANDROID COMPONENTS
# ================================================================================================

# Keep ViewBinding classes
-keep class com.horcruxsys.kivara.databinding.** { *; }

# ViewModel
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# Keep Fragment constructors
-keepclassmembers class * extends androidx.fragment.app.Fragment {
    public <init>(...);
}

# ================================================================================================
# HILT / DAGGER
# ================================================================================================

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Hilt Android components
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# Keep injected constructors
-keepclasseswithmembernames class * {
    @javax.inject.Inject <init>(...);
}

# Keep Hilt modules
-keep @dagger.Module class *
-keep @dagger.hilt.InstallIn class *

# ================================================================================================
# RETROFIT & OKHTTP
# ================================================================================================

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn org.conscrypt.ConscryptHostnameVerifier

# ================================================================================================
# ROOM DATABASE
# ================================================================================================

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ================================================================================================
# SECURITY & ENCRYPTION
# ================================================================================================

# Keep security crypto classes
-keep class androidx.security.crypto.** { *; }
-keep class com.google.crypto.tink.** { *; }

# ================================================================================================
# DATA MODELS
# ================================================================================================

# Keep data classes used for serialization
-keep class com.horcruxsys.kivara.data.model.** { *; }
-keep class com.horcruxsys.kivara.domain.model.** { *; }

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ================================================================================================
# NAVIGATION COMPONENT
# ================================================================================================

-keep class androidx.navigation.fragment.NavHostFragment
-keepnames class * extends androidx.navigation.Navigator

# ================================================================================================
# FIREBASE (If used)
# ================================================================================================

-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ================================================================================================
# LEAKCANARY (Automatically excluded from release, but just in case)
# ================================================================================================

-dontwarn com.squareup.leakcanary.**

# ================================================================================================
# WARNINGS TO IGNORE
# ================================================================================================

-dontwarn java.lang.invoke.StringConcatFactory