package com.example.mygame

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GameLevelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_level)

        // Find the coconut ImageView
        val coconutImageView = findViewById<ImageView>(R.id.imageView8)

        // Set a click listener for the coconut ImageView
        coconutImageView.setOnClickListener {
            // Navigate to GameActivity when coconut is clicked
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }
}
