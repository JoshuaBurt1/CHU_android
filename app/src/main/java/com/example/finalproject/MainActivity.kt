package com.example.finalproject

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.example.finalproject.databinding.ActivityMainBinding
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

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

        // Fetch data from the backend
        fetchDataFromBackend()

        // Post a new user
        //postUserToBackend(User("KOTLIN_USER", "kotlinuser@example.com", "password123"))

        // Post heart rate data (for testing)
        //postHeartRateToBackend(HeartRate("KOTLIN_USER2", 99, getCurrentTimestamp()))

    }

    // Function to get the current timestamp in ISO 8601 format
    fun getCurrentTimestamp(): String {
        val instant = Instant.now()  // Get current timestamp
        val formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC)  // Format to ISO 8601 (UTC time)
        return formatter.format(instant)
    }

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

    private fun postUserToBackend(user: User) {
        lifecycleScope.launch {
            try {
                // Make the POST request to create a user
                val response = RetrofitClient.apiService.postUser(user)

                if (response.isSuccessful) {
                    // Handle success
                    Toast.makeText(this@MainActivity, "User created successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle error from backend
                    Toast.makeText(this@MainActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle network error or any exception
                Toast.makeText(this@MainActivity, "Error posting user: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun postHeartRateToBackend(heartRate: HeartRate) {
        lifecycleScope.launch {
            try {
                // Make the POST request to create a heart rate
                val response = RetrofitClient.apiService.postHeartRate(heartRate)

                if (response.isSuccessful) {
                    // Handle success
                    Toast.makeText(this@MainActivity, "Heart rate posted successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle error from backend
                    Toast.makeText(this@MainActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle network error or any exception
                Toast.makeText(this@MainActivity, "Error posting heart rate: ${e.message}", Toast.LENGTH_LONG).show()
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
