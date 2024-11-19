package com.example.finalproject

// Data class to map MongoDB data
data class User(
    val username: String,
    val email: String,
    val password: String
)

// HeartRate model class
data class HeartRate(
    val userId: String,
    val rate: Int,
    val timestamp: String
)