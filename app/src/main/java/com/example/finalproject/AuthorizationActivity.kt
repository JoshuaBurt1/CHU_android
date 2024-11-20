package com.example.finalproject

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import org.json.JSONObject

class AuthorizationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle the redirect URI and extract the authorization code
        val uri: Uri? = intent?.data
        uri?.let {
            val code = uri.getQueryParameter("code")  // Extract authorization code
            if (!code.isNullOrEmpty()) {
                // Successfully received authorization code
                exchangeAuthorizationCodeForToken(code)
            } else {
                // Handle error (no code in the response)
                Toast.makeText(this, "Authorization failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun exchangeAuthorizationCodeForToken(code: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Authenticating with Fitbit...")
        progressDialog.show()

        // The client_id and redirect_uri are required
        val clientId = "23PS6K"  // Public client ID
        val redirectUri = "https://chu-server.onrender.com/redirect"

        val requestBody = FormBody.Builder()
            .add("client_id", clientId)
            .add("code", code)
            .add("grant_type", "authorization_code")
            .add("redirect_uri", redirectUri)
            .build()

        val request = Request.Builder()
            .url("https://api.fitbit.com/oauth2/token")  // Fitbit's OAuth token endpoint
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@AuthorizationActivity,
                        "Network failure: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    progressDialog.dismiss()
                }

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody)
                    val accessToken = json.getString("access_token")
                    val refreshToken = json.getString("refresh_token")

                    // Save the access token and refresh token securely
                    saveAccessToken(accessToken)
                    saveRefreshToken(refreshToken)

                    runOnUiThread {
                        Toast.makeText(
                            this@AuthorizationActivity,
                            "Access Token Obtained!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()  // Go back to MainActivity
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@AuthorizationActivity,
                            "Failed to get token",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun saveAccessToken(accessToken: String) {
        val sharedPreferences = getSharedPreferences("fitbit_oauth", MODE_PRIVATE)
        sharedPreferences.edit().putString("access_token", accessToken).apply()
    }

    private fun saveRefreshToken(refreshToken: String) {
        val sharedPreferences = getSharedPreferences("fitbit_oauth", MODE_PRIVATE)
        sharedPreferences.edit().putString("refresh_token", refreshToken).apply()
    }

    private fun refreshAccessToken(refreshToken: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Refreshing Token...")
        progressDialog.show()

        val clientId = "23PS6K"  // Public Client ID, no client_secret required in this flow
        val redirectUri = "https://chu-server.onrender.com/redirect"

        // Send request without the client_secret for client-side OAuth
        val requestBody = FormBody.Builder()
            .add("client_id", clientId)
            .add("refresh_token", refreshToken)
            .add("grant_type", "refresh_token")
            .add("redirect_uri", redirectUri)
            .build()

        val request = Request.Builder()
            .url("https://api.fitbit.com/oauth2/token")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@AuthorizationActivity,
                        "Token refresh failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    progressDialog.dismiss()
                }

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody)
                    val newAccessToken = json.getString("access_token")

                    // Save the new access token securely
                    saveAccessToken(newAccessToken)

                    runOnUiThread {
                        Toast.makeText(
                            this@AuthorizationActivity,
                            "Access Token Refreshed!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@AuthorizationActivity,
                            "Failed to refresh token",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}