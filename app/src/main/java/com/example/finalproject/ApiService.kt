package com.example.finalproject

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Retrofit API service interface
interface ApiService {
    @GET("/users")
    suspend fun getUsers(): List<User>

    @GET("/heartrates")
    suspend fun getHeartRates(): List<HeartRate>

    @POST("/users")
    suspend fun postUser(@Body user: User): Response<User>

    @POST("/heartrates")
    suspend fun postHeartRate(@Body heartRate: HeartRate): Response<HeartRate>
}
