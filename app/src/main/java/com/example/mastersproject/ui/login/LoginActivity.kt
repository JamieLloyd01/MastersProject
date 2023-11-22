package com.example.mastersproject.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mastersproject.ForgottenPassword
import com.example.mastersproject.HomeActivity
import com.example.mastersproject.R
import com.example.mastersproject.SignUp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToHome()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupUI()
    }

    private fun setupUI() {
        val enteredEmail = findViewById<TextView>(R.id.email)
        val enteredPassword = findViewById<TextView>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login)
        val progressBar = findViewById<ProgressBar>(R.id.loading)

        loginButton.setOnClickListener {
            doLogin(enteredEmail.text.toString(), enteredPassword.text.toString(), progressBar)
        }

        findViewById<Button>(R.id.navigateToSignUp).setOnClickListener {
            navigateTo(SignUp::class.java)
        }

        findViewById<Button>(R.id.forgot_password).setOnClickListener {
            navigateTo(ForgottenPassword::class.java)
        }
    }

    private fun doLogin(email: String, password: String, progressBar: ProgressBar) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    navigateToHome()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Email or password incorrect", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToHome() {
        navigateTo(HomeActivity::class.java)
        finish()
    }

    private fun navigateTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }
}

