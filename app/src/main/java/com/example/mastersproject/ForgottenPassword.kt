package com.example.mastersproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mastersproject.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class ForgottenPassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotten_password)

        initializeComponents()
        setupEventListeners()
    }

    private fun initializeComponents() {
        auth = FirebaseAuth.getInstance()
        emailField = findViewById(R.id.username)
    }

    private fun setupEventListeners() {
        val sendLinkButton = findViewById<Button>(R.id.SendLink)
        sendLinkButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            if (email.isNotEmpty()) {
                resetPassword(email)
            } else {
                Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show()
            }
        }

        val backButton = findViewById<Button>(R.id.back)
        backButton.setOnClickListener {
            navigateBack()
        }
    }

    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset link sent to your email.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failed to send reset link.", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun navigateBack() {
        val intent = Intent(this@ForgottenPassword, LoginActivity::class.java)
        startActivity(intent)
    }
}
