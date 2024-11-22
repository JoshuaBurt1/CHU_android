package com.example.finalproject.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentHomeBinding
import com.example.finalproject.RetrofitClient
import com.example.finalproject.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    //this needs to be changed every 8 hours
    private val fitbitAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyM1BTNksiLCJzdWIiOiJDQlFLSzIiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJzY29wZXMiOiJyc29jIHJlY2cgcnNldCByaXJuIHJveHkgcnBybyBybnV0IHJzbGUgcmNmIHJhY3QgcmxvYyBycmVzIHJ3ZWkgcmhyIHJ0ZW0iLCJleHAiOjE3MzIyNzQ4NDUsImlhdCI6MTczMjI0NjA0NX0.1HroEZyFoGh0X3O1Tr12pAoXZwTLjQrrtmm3ged2TkM"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.submitChangesButton.visibility = View.GONE
        binding.submitUserButton.setOnClickListener {
            if (validateUserForm()) {
                // Post user data
                val username = binding.usernameInput.text.toString()
                val password = binding.passwordInput.text.toString()
                val clientId = " "
                val fitbitAccessToken = " "
                val age = 0.1
                val gender = " "
                val height = 0.1
                val weight = 0.1
                val memberSince = " "
                val averageDailySteps = 0.1
                //val steps: List<Pair<String, Int>> = listOf(
                //   Pair(generateCurrentTimestamp(), 1)
                //)
                postUserToBackend(
                    username,
                    password,
                    clientId,
                    fitbitAccessToken,
                    age,
                    gender,
                    height,
                    weight,
                    memberSince,
                    averageDailySteps,

                )
            } else {
                // Show toast if user form is incomplete
                Toast.makeText(requireContext(), "Incomplete user form", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up the button click listener
        binding.importUserProfileButton.setOnClickListener {
            fetchFitbitData()
        }

        return root
    }

    // Validate user form fields (username, password)
    private fun validateUserForm(): Boolean {
        val username = binding.usernameInput.text.toString()
        val password = binding.passwordInput.text.toString()

        return username.isNotEmpty() && password.isNotEmpty()
    }

    fun generateCurrentTimestamp(): String {
        val currentDateTime = LocalDateTime.now()
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        return currentDateTime.format(dateTimeFormatter)
    }

    // Function to fetch user data from Fitbit API and parse it
    private fun fetchFitbitData() {
        lifecycleScope.launch {
            try {
                // Fetch data from Fitbit API
                val profileJson = fetchFitbitApiData("https://api.fitbit.com/1/user/-/profile.json")
                val dailyStepsJson = fetchFitbitApiData("https://api.fitbit.com/1/user/-/activities/tracker/steps/date/2024-11-07/today.json")
                val cardioScoreJson = fetchFitbitApiData("https://api.fitbit.com/1/user/-/cardioscore/date/2024-11-07/2024-11-19.json")
                val caloriesJson =  fetchFitbitApiData("https://api.fitbit.com/1/user/-/activities/tracker/calories/date/2024-11-07/today.json")
                val activeZoneJson = fetchFitbitApiData("https://api.fitbit.com/1/user/-/activities/active-zone-minutes/date/2024-11-07/today.json")
                val heartRateJson = fetchFitbitApiData("https://api.fitbit.com/1/user/-/activities/heart/date/2024-11-12/1d/5min.json")

                // Log the raw profile JSON response for debugging
                Log.d("FitbitAPI", "Profile: $profileJson")
                Log.d("FitbitAPI", "Profile: $dailyStepsJson")
                Log.d("FitbitAPI", "Profile: $cardioScoreJson")
                Log.d("FitbitAPI", "Profile: $caloriesJson")
                Log.d("FitbitAPI", "Profile: $activeZoneJson")
                Log.d("FitbitAPI", "Profile: $heartRateJson")

                // Parse the JSON response using Gson
                val gson = Gson()

                // We can parse the JSON as a Map to access the properties dynamically
                val fitbitResponse: Map<String, Any> = gson.fromJson(profileJson, object : TypeToken<Map<String, Any>>() {}.type)

                // Extract the 'user' object from the response
                val user = fitbitResponse["user"] as Map<String, Any>

                // Create an array (List in Kotlin) to store parsed data
                val parsedData = arrayOf(
                    user["age"],                  // Index 0 - Age
                    user["gender"],               // Index 1 - Gender
                    user["height"],               // Index 2 - Height
                    user["weight"],               // Index 3 - Weight
                    user["memberSince"],          // Index 4 - Member Since
                    user["averageDailySteps"],    // Index 5 - Average Daily Steps
                    user["displayName"],          // Index 6 - username
                    user["encodedId"]             // Index 7 - password
                )

                // Log the parsed values for debugging
                Log.d("FitbitAPI", "Age: ${parsedData[0]}")
                Log.d("FitbitAPI", "Gender: ${parsedData[1]}")
                Log.d("FitbitAPI", "Height: ${parsedData[2]}")
                Log.d("FitbitAPI", "Weight: ${parsedData[3]}")
                Log.d("FitbitAPI", "Member Since: ${parsedData[4]}")
                Log.d("FitbitAPI", "Average Daily Steps: ${parsedData[5]}")
                Log.d("FitbitAPI", "Username: ${parsedData[6]}")
                Log.d("FitbitAPI", "Password: ${parsedData[7]}")

                // Now, post the user data to the backend using the parsed data
                postUserToBackend(
                    username = parsedData[6] as String,
                    password = parsedData[7] as String,
                    clientId = "23PS6K",  // Replace with actual client ID
                    fitbitAccessToken = fitbitAccessToken,
                    age = parsedData[0] as Double,
                    gender = parsedData[1] as String,
                    height = parsedData[2] as Double,
                    weight = parsedData[3] as Double,
                    memberSince = parsedData[4] as String,
                    averageDailySteps = parsedData[5] as Double
                )

            } catch (e: Exception) {
                Log.e("FitbitAPI", "Error fetching data: ${e.message}")
                Toast.makeText(requireContext(), "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to post user data to the backend
    // In HomeFragment
    private fun postUserToBackend(
        username: String,
        password: String,
        clientId: String,
        fitbitAccessToken: String,
        age: Double,
        gender: String,
        height: Double,
        weight: Double,
        memberSince: String,
        averageDailySteps: Double
    ) {
        lifecycleScope.launch {
            try {
                // Manually format the JSON request body to match the curl format
                val json = """
                {
                    "username": "$username",
                    "password": "$password",
                    "clientId": "$clientId",
                    "fitbitAccessToken": "$fitbitAccessToken",
                    "age": $age,
                    "gender": "$gender",
                    "height": $height,
                    "weight": $weight,
                    "memberSince": "$memberSince",
                    "averageDailySteps": $averageDailySteps
                }
            """.trimIndent()

                // Convert the JSON string to RequestBody
                val mediaType = "application/json".toMediaTypeOrNull()
                val requestBody = json.toRequestBody(mediaType)
                val user = User(username, password, clientId, fitbitAccessToken, age, gender, height, weight, memberSince, averageDailySteps)

                // Make the POST request to the backend using Retrofit
                val response: Response<User> = RetrofitClient.apiService.postUser(user)

                // Handle the response
                if (response.isSuccessful) {
                    // Show success message
                    Toast.makeText(requireContext(), "User $username posted successfully", Toast.LENGTH_SHORT).show()
                    binding.sectionChange.text = "User Profile"
                    // Hide login fields after successful login
                    binding.usernameInput.visibility = View.GONE
                    binding.passwordInput.visibility = View.GONE
                    binding.submitUserButton.visibility = View.GONE
                    binding.importUserProfileButton.visibility = View.GONE
                    // Update UI with the user's details
                    binding.ageValueTextView.text = "${age.toInt()}"
                    binding.heightValueTextView.text = "${height} cm"
                    binding.weightValueTextView.text = "${weight} kg"
                    binding.averageDailyStepsValueTextView.text = "${averageDailySteps.toInt()} steps"

                    // Make the TextViews visible after successful login
                    binding.userDataTable.visibility = View.VISIBLE
                    binding.submitChangesButton.visibility = View.VISIBLE


                    binding.submitChangesButton.setOnClickListener {
                        // Get updated values from EditTexts
                        val updatedAge = binding.ageEditText.text.toString().toDoubleOrNull() ?: age
                        val updatedHeight = binding.heightEditText.text.toString().toDoubleOrNull() ?: height
                        val updatedWeight = binding.weightEditText.text.toString().toDoubleOrNull() ?: weight
                        val updatedAverageDailySteps = binding.averageDailyStepsEditText.text.toString().toDoubleOrNull() ?: averageDailySteps

                        // Validate if all fields are filled
                        // Post the updated values to the backend, using the stored login information
                        postUserToBackend(
                            username = username,
                            password = password,
                            clientId = clientId,
                            fitbitAccessToken = fitbitAccessToken,
                            age = updatedAge,
                            gender = gender,
                            height = updatedHeight,
                            weight = updatedWeight,
                            memberSince = memberSince,
                            averageDailySteps = updatedAverageDailySteps
                        )
                    }

                } else {
                    // Show error message with the response body
                    Toast.makeText(requireContext(), "Error posting user: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle exception (e.g., network failure)
                Toast.makeText(requireContext(), "Error posting user: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Function to fetch Fitbit API data
    private suspend fun fetchFitbitApiData(url: String): String {
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $fitbitAccessToken")
            .header("Content-Type", "application/json") // Ensure content type is JSON
            .build()

        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("FitbitAPI", "Response: $responseBody")
                    return@withContext responseBody ?: "No data"
                } else {
                    throw Exception("Error fetching data: ${response.code}")
                }
            } catch (e: Exception) {
                throw Exception("Error: ${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

