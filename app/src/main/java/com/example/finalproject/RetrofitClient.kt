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

/*
package com.example.finalproject

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Base URL for Fitbit API
    private const val FITBIT_BASE_URL = "https://api.fitbit.com"  // Fitbit API base URL
    private const val HOST_URL = "https://chu-server.onrender.com/"  //This is the deployment server link
    private const val TEST_URL = "http://10.0.2.2:3000" // This is the test server link alias for localhost:3000


    private var isFitbitApi = true  // Set this flag to true if you want to use the Fitbit API

    // Replace <ACCESS_TOKEN> with your actual OAuth token or a dynamic value
    private const val AUTHORIZATION_HEADER = "Bearer <ACCESS_TOKEN>"

    // OkHttp client with logging and authorization header
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })  // Log the HTTP requests and responses (for debugging)
        .addInterceptor { chain ->
            // Add the Authorization header for every request
            val request = chain.request().newBuilder()
                .addHeader("Authorization", AUTHORIZATION_HEADER)
                .build()
            chain.proceed(request)
        }
        .build()

    // Retrofit instance
    private val retrofitFitbit: Retrofit = Retrofit.Builder()
        .baseUrl(FITBIT_BASE_URL)
        .client(okHttpClient) // Add custom OkHttp client with interceptors
        .addConverterFactory(GsonConverterFactory.create()) // Gson converter for parsing JSON
        .build()

    private val retrofitLocal: Retrofit = Retrofit.Builder()
        .baseUrl(HOST_URL)
        .addConverterFactory(GsonConverterFactory.create()) // Gson converter for parsing JSON
        .build()

    // API service to interact with Fitbit API or Local server
    val apiService: ApiService
        get() {
            // Return the appropriate API service based on the flag
            return if (isFitbitApi) {
                retrofitFitbit.create(ApiService::class.java)  // Use Fitbit API service
            } else {
                retrofitLocal.create(ApiService::class.java)  // Use Local server API service
            }
        }

    // Set the flag to switch between APIs
    fun setApiMode(isFitbit: Boolean) {
        isFitbitApi = isFitbit  // Set the mode to use Fitbit API or Local API
    }
}

 */