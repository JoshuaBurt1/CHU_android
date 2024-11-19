package com.example.finalproject.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentHomeBinding
import com.example.finalproject.RetrofitClient
import com.example.finalproject.User
import com.example.finalproject.HeartRate
import kotlinx.coroutines.launch
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Button click listener for submitting user data
        binding.submitUserButton.setOnClickListener {
            if (validateUserForm()) {
                // Post user data
                val username = binding.usernameInput.text.toString()
                val email = binding.emailInput.text.toString()
                val password = binding.passwordInput.text.toString()
                postUserToBackend(username, email, password)
            } else {
                // Show toast if user form is incomplete
                Toast.makeText(requireContext(), "Incomplete user form", Toast.LENGTH_SHORT).show()
            }
        }

        // Button click listener for submitting heart rate data
        binding.submitHeartRateButton.setOnClickListener {
            if (validateHeartRateForm()) {
                // Post heart rate data
                val heartRate = binding.heartRateInput.text.toString().toInt()
                postHeartRateToBackend(heartRate)
            } else {
                // Show toast if heart rate form is incomplete
                Toast.makeText(requireContext(), "Incomplete heart rate form", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    // Validate user form fields (username, email, password)
    private fun validateUserForm(): Boolean {
        val username = binding.usernameInput.text.toString()
        val email = binding.emailInput.text.toString()
        val password = binding.passwordInput.text.toString()

        return username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
    }

    // Validate heart rate form (heart rate field)
    private fun validateHeartRateForm(): Boolean {
        val heartRate = binding.heartRateInput.text.toString()
        return heartRate.isNotEmpty()
    }

    // Function to post user data to the backend
    private fun postUserToBackend(username: String, email: String, password: String) {
        lifecycleScope.launch {
            try {
                // Create the user object
                val user = User(username, email, password)

                // Make the POST request to the backend using Retrofit
                val response: Response<User> = RetrofitClient.apiService.postUser(user)

                // Handle the response
                if (response.isSuccessful) {
                    // Show success message
                    Toast.makeText(requireContext(), "User $username posted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Show error message
                    Toast.makeText(requireContext(), "Error posting user: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle exception (e.g., network failure)
                Toast.makeText(requireContext(), "Error posting user: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Function to post heart rate data to the backend
    private fun postHeartRateToBackend(heartRate: Int) {
        lifecycleScope.launch {
            try {
                // Create the heart rate object
                val heartRateData = HeartRate(userId = binding.usernameInput.text.toString(), rate = heartRate, timestamp = System.currentTimeMillis().toString())

                // Make the POST request to the backend using Retrofit
                val response: Response<HeartRate> = RetrofitClient.apiService.postHeartRate(heartRateData)

                // Handle the response
                if (response.isSuccessful) {
                    // Show success message
                    Toast.makeText(requireContext(), "Heart rate $heartRate posted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Show error message
                    Toast.makeText(requireContext(), "Error posting heart rate: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle exception (e.g., network failure)
                Toast.makeText(requireContext(), "Error posting heart rate: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
