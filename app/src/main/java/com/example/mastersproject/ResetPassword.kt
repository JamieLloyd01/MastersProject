package com.example.mastersproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.mastersproject.ui.login.LoginActivity

class ResetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val returnToSignInButton = findViewById<Button>(R.id.sign_in)
        returnToSignInButton.setOnClickListener {
            val intent = Intent(this@ResetPassword, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}