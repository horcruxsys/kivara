package com.horcruxsys.kivara.api

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service interface for defining endpoints.
 */
interface ApiService {

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
 * Example response data class.
 * Replace with actual response structure.
 */
data class ExampleResponse(
    val id: String,
    val name: String
)
