package com.example.finalproject

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

//HTTP Methods:
//GET user information and display in app
//POST new users and new information to the Server -> Database
interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<User>

    @POST("/users")
    suspend fun postUser(@Body user: User): Response<User>
}
