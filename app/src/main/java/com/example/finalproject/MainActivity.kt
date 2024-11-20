package com.example.finalproject

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.example.finalproject.databinding.ActivityMainBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    // Replace with your own access token obtained from Fitbit OAuth process
    private val fitbitAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyM1BTNksiLCJzdWIiOiJDQlFLSzIiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJzY29wZXMiOiJyc29jIHJlY2cgcnNldCByaXJuIHJveHkgcm51dCBycHJvIHJzbGUgcmNmIHJhY3QgcnJlcyBybG9jIHJ3ZWkgcmhyIHJ0ZW0iLCJleHAiOjE3MzIxMDI5NTcsImlhdCI6MTczMjA3NDE1N30.nhnNRfjRsaWoMCnoGE5hSk2-lNochqJ8kUOEJyI3bYA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        // Initialize navigation drawer
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        fetchDataFromBackend()
        fetchHeartRateFromFitbitAndPostToMongoDB()
    }

    // Function to fetch heart rate data from Fitbit and post it to MongoDB
    private fun fetchHeartRateFromFitbitAndPostToMongoDB() {
        lifecycleScope.launch {
            try {
                // Fetch heart rate data from Fitbit API
                val heartRate = fetchHeartRateFromFitbit()
                if (heartRate != null) {
                    // Show the last heart rate value in a Toast
                    Toast.makeText(this@MainActivity, "Last heart rate: $heartRate", Toast.LENGTH_SHORT).show()

                    // Now post the heart rate to MongoDB
                    postHeartRateToMongoDB(heartRate)
                } else {
                    Toast.makeText(this@MainActivity, "Failed to fetch heart rate data", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle error (e.g., network failure, invalid response)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Fetch heart rate data from Fitbit using access token
    private suspend fun fetchHeartRateFromFitbit(): String? {
        val url = "https://api.fitbit.com/1/user/-/profile.json" // Example endpoint to get heart rate data
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $fitbitAccessToken")
            .build()

        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    // Log the raw JSON response for debugging
                    val responseBody = response.body?.string()
                    println("Fitbit Response: $responseBody")  // Log the raw response for inspection

                    // Parse the heart rate from JSON response
                    val jsonObject = JSONObject(responseBody)
                    val activities = jsonObject.getJSONArray("user")
                    val heartRate = activities.getJSONObject(0)
                        .getJSONArray("dataset")
                        .getJSONObject(0)
                        .getInt("value") // Get the last heart rate value

                    heartRate.toString()
                } else {
                    println("Fitbit API request failed with code: ${response.code}")  // Log the error response code
                    null
                }
            } catch (e: IOException) {
                println("Error fetching heart rate: ${e.message}")  // Log the exception
                null
            }
        }
    }

    // Function to post heart rate data to MongoDB
    private suspend fun postHeartRateToMongoDB(heartRate: String) {
        val url = "https://your-mongo-api-endpoint.com/heartRate" // Replace with your MongoDB API endpoint
        val json = JSONObject()
        json.put("heartRate", heartRate)
        json.put("timestamp", System.currentTimeMillis())

        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    // Successfully posted heart rate to MongoDB
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Heart rate posted to MongoDB", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Failed to post heart rate", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error posting heart rate: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

// Lifecycles and Coroutines: The fetchDataFromBackend() and postUserToBackend() methods use Kotlin coroutines,
    // which are a key feature that facilitates asynchronous programming in a functional style.
    // You define what happens asynchronously using functions like launch, withContext, and async.
    // These functions allow you to write asynchronous code in a declarative style, which is a hallmark of
    // functional programming.
    private fun fetchDataFromBackend() {
        lifecycleScope.launch {
            try {
                // Fetch users data from the backend
                val usersData: List<User> = RetrofitClient.apiService.getUsers()
                if (usersData.isNotEmpty()) {
                    // Handle users data (e.g., show the first user)
                    val firstUser = usersData[0]
                    Toast.makeText(this@MainActivity, "First user: ${firstUser.username}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "No users found", Toast.LENGTH_SHORT).show()
                }

                // Fetch heart rates data from the backend
                val heartratesData: List<HeartRate> = RetrofitClient.apiService.getHeartRates()
                if (heartratesData.isNotEmpty()) {
                    // Handle heart rates data (e.g., show the first heart rate)
                    val firstHeartRate = heartratesData[0]
                    Toast.makeText(this@MainActivity, "First heart rate: ${firstHeartRate.rate}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "No heart rates found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle error (e.g., network failure, no internet)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}