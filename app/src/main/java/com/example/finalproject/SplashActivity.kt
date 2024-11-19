package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)  // Set the splash layout

        // Show the splash screen for 3 seconds, then navigate to MainActivity
        Handler().postDelayed({
            // Start MainActivity after the splash screen
            startActivity(Intent(this, MainActivity::class.java))
            finish()  // Close SplashActivity to remove it from the back stack
        }, 3000)  // Delay of 3 seconds
    }
}