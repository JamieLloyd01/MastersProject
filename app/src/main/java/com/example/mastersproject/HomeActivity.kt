package com.example.mastersproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        /* Set click listeners for each ImageView (button) PAGES DO NOT EXIST YET
        profileButton.setOnClickListener { navigateToProfilePage() }
        homeButton.setOnClickListener { navigateToHomePage() }
        settingsButton.setOnClickListener { navigateToSettingsPage() }
    }

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

         */
    }

}