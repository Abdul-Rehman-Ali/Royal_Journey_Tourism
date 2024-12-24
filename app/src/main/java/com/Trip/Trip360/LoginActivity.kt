package com.Trip.Trip360

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.Trip.Trip360.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Inflate the layout using ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up Sign In button click listener
        binding.btnSignin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Show loader before login starts
                binding.progressLoader.visibility = View.VISIBLE
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Fetch user data from Firestore
                    fetchUserData()
                } else {
                    // Hide loader and show error message
                    binding.progressLoader.visibility = View.GONE
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun fetchUserData() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            firestore.collection("Users").document(userId).get()
                .addOnSuccessListener { document ->
                    binding.progressLoader.visibility = View.GONE // Hide loader on success

                    if (document != null && document.exists()) {
                        // Retrieve data
                        val color = document.getString("color")
                        val email = document.getString("email")
                        val logoURL = document.getString("logoURL")
                        val password = document.getString("password")
                        val phoneNo = document.getString("phoneNo")
                        val username = document.getString("username")
                        val webName = document.getString("webName")
                        val webURL = document.getString("webURL")

                        // Store in SharedPreferences
                        storeUserDataLocally(
                            color, email, logoURL, password, phoneNo,
                            userId, username, webName, webURL
                        )

                        // Navigate to MainActivity
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Close LoginActivity
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    binding.progressLoader.visibility = View.GONE // Hide loader on failure
                    Toast.makeText(this, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            binding.progressLoader.visibility = View.GONE // Hide loader if userId is null
            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun storeUserDataLocally(
        color: String?, email: String?, logoURL: String?, password: String?,
        phoneNo: String?, userId: String, username: String?, webName: String?, webURL: String?
    ) {
        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("color", color)
        editor.putString("email", email)
        editor.putString("logoURL", logoURL)
        editor.putString("password", password)
        editor.putString("phoneNo", phoneNo)
        editor.putString("userId", userId)
        editor.putString("username", username)
        editor.putString("webName", webName)
        editor.putString("webURL", webURL)
        editor.apply()
    }
}
