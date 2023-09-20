package com.example.mastersproject
//NOT THIS ONE HOMIE








import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MapHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_home)

        val ListButton = findViewById<Button>(R.id.ListButton)
        ListButton.setOnClickListener {
            val intent = Intent(this@MapHomeActivity, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}