package com.example.finalproject

import android.os.Bundle
import android.view.Menu
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.example.finalproject.databinding.ActivityMainBinding

//TWO major issues using this on your phone:
// 1. chu-server needs to be running and be fast (use firestore?)
// 2. the access token needs to be current (this token needs to be refreshed in the app itself on start)
// A lot of MainActivity is pre-built code from choosing a navigation drawer at the start of the project
// The only function of note here is fetchDataFromDatabase() which checks if there is a connection to the database in Logcat
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        // Fetch data from MongoDB
        fetchDataFromDatabase()
    }

    // Client:Server:Database connection check, logs data to logcat backend
    private fun fetchDataFromDatabase() {
        lifecycleScope.launch {
            try {
                val usersData: List<User> = RetrofitClient.apiService.getUsers()
                Log.d("MainActivity - USER LOG", "Users data: $usersData")
                if (usersData.isNotEmpty()) {
                    val lastUser = usersData.last() // Get the last user
                    Log.d("MainActivity - USER LOG", "Last user: ${lastUser.username}")
                } else {
                    Log.d("MainActivity", "No users found in response")
                }
            } catch (e: Exception) {
                    Log.e("MainActivity", "Error occurred: ${e.message}", e)
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
