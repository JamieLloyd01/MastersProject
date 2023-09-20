package com.example.mastersproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.mastersproject.ui.login.LoginActivity


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)


        val mapButton = findViewById<Button>(R.id.mapButton)
        mapButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, MapsActivity::class.java)
            startActivity(intent)
        }


        val profileButton = findViewById<ImageView>(R.id.profileButton)
            profileButton.setOnClickListener {
                val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
                startActivity(intent)
            }


        val settingsButton = findViewById<ImageView>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, SettingsActivity::class.java)
            startActivity(intent)
        }


            /* Set click listeners for each ImageView (button) PAGES DO NOT EXIST YET
        profileButton.setOnClickListener { navigateToProfilePage() }
        homeButton.setOnClickListener { navigateToHomePage() }
        settingsButton.setOnClickListener { navigateToSettingsPage() }




    private fun navigateToProfilePage() {
        // Replace ProfileActivity with the actual activity you want to navigate to
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHomePage() {
        // Replace HomeActivity with the actual activity you want to navigate to
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSettingsPage() {
        // Replace SettingsActivity with the actual activity you want to navigate to
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
            adding notes blah blah blah tes test test
         */
        }

    }
