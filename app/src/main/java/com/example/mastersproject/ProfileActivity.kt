package com.example.mastersproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class ProfileActivity : AppCompatActivity() {
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
    }
}