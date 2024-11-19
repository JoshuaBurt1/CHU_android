package com.example.finalproject

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    //private const val BASE_URL = "https://chu-server.onrender.com/"  //This is the deployment server link
    private const val BASE_URL = "http://10.0.2.2:3000" // This is the test server link alias for localhost:3000

    // Retrofit instance
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // API service to interact with the backend
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}