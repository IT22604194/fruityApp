package com.example.mygame
import android.graphics.Rect
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import kotlin.random.Random

class GameActivity : AppCompatActivity() {
    private var lastTouchX = 0f
    private lateinit var catcherImageView: ImageView
    private lateinit var gameHandler: Handler
    private lateinit var pineapple: ImageView
    private lateinit var watermelon: ImageView
    private lateinit var mango: ImageView
    private lateinit var angrykiwi: ImageView
    private lateinit var fruitbucket: ImageView
    private val fruitSpawnInterval = 2500L
    private val fruitSpawnDelay = 1000L
    private var score = 0
    private lateinit var scoreTextView: TextView
    private lateinit var timerTextView: TextView
    private var timeLeftInMillis = 30000L
    private lateinit var timer: CountDownTimer
    private val SCORE_THRESHOLD = 800
    private var basketX = 0f
    private val PREFS_FILE_NAME = "MyGamePrefs"
    private val HIGHEST_SCORE_KEY = "highestScore"
    private var elapsedTime = 0L
    private val MAX_DURATION = 30000L
    // private val MANGO_COUNT = 2
    // private val WATERMELON_COUNT = 4
    //private val PINEAPPLE_COUNT = 3
    //private val FRUIT_BASKET_COUNT = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameHandler = Handler(Looper.getMainLooper())


        scoreTextView = findViewById(R.id.scoreTextView)
        timerTextView = findViewById(R.id.timerTextView)

        // Initialize fruit ImageViews
        pineapple = findViewById(R.id.pineapple)
        watermelon = findViewById(R.id.watermelon)
        mango = findViewById(R.id.mango)
        angrykiwi = findViewById(R.id.angrykiwi)
        fruitbucket = findViewById(R.id.fruitbucket)

        // Start spawning fruits
        startFruitSpawning()


        catcherImageView = findViewById(R.id.catcherImageView)
        catcherImageView.setOnTouchListener { _, event ->
            handleCatcherTouch(event)
        }

        // Start the game timer
        startTimer()
    }

    private fun startFruitSpawning() {
        gameHandler.postDelayed({
            if(elapsedTime < MAX_DURATION){
                spawnFruit()
                elapsedTime += fruitSpawnInterval
                startFruitSpawning()
            }

            //gameHandler.postDelayed(::startFruitSpawning, fruitSpawnInterval)
        }, fruitSpawnDelay)
    }

    private fun spawnFruit() {
        val fruitImageViews = arrayOf(pineapple, watermelon, mango, angrykiwi, fruitbucket)
        val randomIndex = Random.nextInt(fruitImageViews.size)
        val selectedFruitImageView = fruitImageViews[randomIndex]

        when (selectedFruitImageView) {
            pineapple -> score += 20
            watermelon -> score += 20
            mango -> score += 20
            angrykiwi -> score -= 5
        }

        // Check if the fruit overlaps with the basket
        val fruitRect = Rect()
        val basketRect = Rect()
        selectedFruitImageView.getHitRect(fruitRect)
        catcherImageView.getHitRect(basketRect)

        if (Rect.intersects(fruitRect, basketRect)) {
            score += 40
            selectedFruitImageView.visibility = View.INVISIBLE // Hide the fruit when caught by the basket
        } else {
            selectedFruitImageView.visibility = View.VISIBLE // Show the fruit otherwise
        }

        updateScore(true)
        moveFruitDownwards(selectedFruitImageView)
    }



    private fun handleCatcherTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> lastTouchX = event.x
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.x - lastTouchX
                moveBasket(deltaX)
                lastTouchX = event.x
            }
        }
        return true
    }

    private fun moveBasket(deltaX: Float) {
        // Move the catcher ImageView horizontally by deltaX
        catcherImageView.translationX += deltaX
        // Update the basket position
        basketX = catcherImageView.translationX
    }

    private fun moveFruitDownwards(fruitImageView: ImageView) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Move the fruit downwards
                fruitImageView.translationY += 20

                // Get the bottom position of the screen
                val bottomScreenY = resources.displayMetrics.heightPixels
                // Check if the fruit is out of the screen
                if (fruitImageView.translationY >= bottomScreenY) {
                    fruitImageView.visibility = View.INVISIBLE
                    updateScore(false)
                } else {
                    // If the fruit is still on the screen, continue moving it downwards
                    handler.postDelayed(this, 100) // Adjust the interval as needed
                }
            }
        }, 100)
    }

    private fun updateScore(isCaught: Boolean) {
        if (isCaught) {
            score += 20
        } else {
            score -= 5
        }
        scoreTextView.text = getString(R.string.score_format, score)
        if (score >= SCORE_THRESHOLD && timeLeftInMillis > 0) {
            endGame(true)
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                endGame(false)
            }
        }
        timer.start()
    }

    private fun updateTimer() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        timerTextView.text = timeLeftFormatted
    }

    private fun endGame(won: Boolean) {
        timer.cancel()
        val message = if (won) "Congratulations! You won!" else "Game over! Try again."
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        // Start the GameOverActivity
        val intent = Intent(this, GameOverActivity::class.java)
        intent.putExtra("points", score)
        intent.putExtra("highest", getHighestScore())
        startActivity(intent)
        finish()
    }

    private fun getHighestScore(): Int {
        val prefs = getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(HIGHEST_SCORE_KEY, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        gameHandler.removeCallbacksAndMessages(null)
    }
}