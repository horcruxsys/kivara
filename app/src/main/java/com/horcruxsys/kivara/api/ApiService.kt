package com.horcruxsys.kivara.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit API service interface for defining endpoints.
 */
interface ApiService {

    /**
     * Validate user session
     */
    @GET("auth/validate")
    suspend fun validateSession(
        @Header("Authorization") token: String
    ): SessionResponse

    /**
     * Login endpoint
     */
    @POST("auth/login")
    suspend fun login(
        @Body credentials: LoginRequest
    ): LoginResponse

    /**
     * Example endpoint for fetching data.
     * Replace with actual endpoint and parameters.
     */
    @GET("exampleEndpoint")
    suspend fun fetchExampleData(
        @Query("param") param: String
    ): ExampleResponse
}

/**
 * Login request data class
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Login response data class
 */
data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val message: String?
)

/**
 * Session validation response
 */
data class SessionResponse(
    val valid: Boolean,
    val userId: String?
)

/**
 * Example response data class.
 * Replace with actual response structure.
 */
data class ExampleResponse(
    val id: String,
    val name: String
)
