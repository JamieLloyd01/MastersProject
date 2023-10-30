package com.example.mastersproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.mastersproject.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        val homeButton = findViewById<ImageView>(R.id.homeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        val mapButton = findViewById<ImageView>(R.id.mapButton)
        mapButton.setOnClickListener {
            val intent = Intent(this@ProfileActivity, MapTest::class.java)
            startActivity(intent)
        }


        val logOut = findViewById<ImageView>(R.id.logout)
        logOut.setOnClickListener {
            auth.signOut() // This logs the user out
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}