package com.example.mygame
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)

        // Get the points from the intent
        val points = intent.getIntExtra("points", 0)
        val highest = intent.getIntExtra("highest", 0)

        // Find views by their IDs
        val tvPoints: TextView = findViewById(R.id.tvPoints)
        val tvHighest: TextView = findViewById(R.id.tvHighest)
        val ivNewHighest: ImageView = findViewById(R.id.ivNewHighest)
        val restartButton: ImageButton = findViewById(R.id.restart)
        val exitButton: ImageButton = findViewById(R.id.Exit)

        // Set the points and highest score
        tvPoints.text = points.toString()
        tvHighest.text = highest.toString()

        // Check if the current score is higher than the highest score
        if (points > highest) {
            ivNewHighest.visibility = View.VISIBLE
        }

        // Set click listeners for the restart and exit buttons
        restartButton.setOnClickListener {
            // Restart the game
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        exitButton.setOnClickListener {
            // Exit the game
            finishAffinity()
        }
    }
}
