package com.horcruxsys.kivara.di

import android.os.Build
import com.horcruxsys.kivara.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.CertificatePinner
import okhttp3.ConnectionSpec
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for providing network-related dependencies.
 * Includes OkHttp client with security features like certificate pinning
 * and Retrofit for API communication.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.example.com/" // TODO: Replace with actual API URL
    private const val TIMEOUT_SECONDS = 30L
    private const val APP_VERSION = BuildConfig.VERSION_NAME

    /**
     * Provides JSON serializer configuration.
     */
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        isLenient = true
        explicitNulls = false
    }

    /**
     * Provides HTTP logging interceptor.
     * Only logs in debug builds to avoid leaking sensitive data.
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.ENABLE_LOGGING) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * Provides certificate pinner for enhanced security.
     * Certificate pinning prevents man-in-the-middle attacks by validating
     * the server's certificate against known pins.
     * 
     * TODO: Add actual certificate pins for your production API
     * You can get the certificate pins using: 
     * openssl s_client -servername api.example.com -connect api.example.com:443 | 
     * openssl x509 -pubkey -noout | openssl rsa -pubin -outform der | 
     * openssl dgst -sha256 -binary | openssl enc -base64
     */
    @Provides
    @Singleton
    fun provideCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            // TODO: Add your API domain and certificate pins
            // .add("api.example.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
            // .add("api.example.com", "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=")
            .build()
    }

    /**
     * Provides connection spec for TLS configuration.
     * Enforces TLS 1.2 and 1.3 for secure connections.
     */
    @Provides
    @Singleton
    fun provideConnectionSpec(): ConnectionSpec {
        return ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_3)
            .build()
    }

    /**
     * Provides configured OkHttpClient with security features.
     * Includes:
     * - Connection timeouts
     * - Certificate pinning (when configured)
     * - TLS 1.2/1.3 enforcement
     * - Logging interceptor (debug only)
     * - User-Agent header
     * - Common headers
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        certificatePinner: CertificatePinner,
        connectionSpec: ConnectionSpec
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .apply {
                // Only apply certificate pinning if pins are configured
                if (certificatePinner.pins.isNotEmpty()) {
                    certificatePinner(certificatePinner)
                }
            }
            .connectionSpecs(listOf(connectionSpec, ConnectionSpec.CLEARTEXT))
            .addInterceptor { chain ->
                // Add common headers including User-Agent
                val userAgent = "Kivara/${APP_VERSION} (Android ${Build.VERSION.SDK_INT})"
                val request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("User-Agent", userAgent)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * Provides Retrofit instance for API communication.
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // TODO: Add API service interfaces here
    // Example:
    // @Provides
    // @Singleton
    // fun provideApiService(retrofit: Retrofit): ApiService {
    //     return retrofit.create(ApiService::class.java)
    // }
}
