package com.example.mastersproject

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.mastersproject.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        val auth: FirebaseAuth = FirebaseAuth.getInstance()




        val inputEmail = findViewById<TextView>(R.id.email)
        val inputUsername = findViewById<TextView>(R.id.username)
        val inputPassword = findViewById<TextView>(R.id.password)
        val createAccount = findViewById<Button>(R.id.signup)
        val progressBar = findViewById<ProgressBar>(R.id.loading)


        createAccount.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val email = inputEmail.text.toString()
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()



            if (email.isEmpty()) {
                Toast.makeText(this@SignUp, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (username.isEmpty()) {
                Toast.makeText(this@SignUp, "Enter username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this@SignUp, "Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                baseContext,
                                "Account created",
                                Toast.LENGTH_SHORT,
                            ).show()

                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            progressBar.visibility = View.GONE

                        }
                    }


        }






        val accountAlready = findViewById<Button>(R.id.already_signed_up)
        accountAlready.setOnClickListener {
            val intent = Intent(this@SignUp, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}