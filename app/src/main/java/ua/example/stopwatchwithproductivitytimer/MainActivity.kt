package ua.example.stopwatchwithproductivitytimer

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val handler = Handler()
    private val colors = arrayOf(Color.RED, Color.GREEN, Color.BLUE)
    private var color = colors[0]
    private var countForChangeColor = 0
    private var isStart = true
    private var seconds = 0
    private var minutes = 0
    val CHANNEL_ID = "org.hyperskill"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        settingsButton.setOnClickListener {
            if (isStart) {
                val contentView =
                    LayoutInflater.from(this).inflate(R.layout.dialog_main, null, false)
                AlertDialog.Builder(this)
                    .setTitle("Set upper limit in seconds")
                    .setView(contentView)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        val editText = contentView?.findViewById<EditText>(R.id.upperLimitEditText)
                        countForChangeColor = editText?.text?.toString()?.toInt() ?: 0
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }

        }

        startButton.setOnClickListener {
            if (isStart) {
                handler.postDelayed(startTime, 1000)
                isStart = false
                progressBar.visibility = ProgressBar.VISIBLE
                handler.postDelayed(updateLight, 1000)
                settingsButton.isEnabled = false
            }
        }

        resetButton.setOnClickListener {
            handler.removeCallbacks(startTime)
            "00:00".also { textView.text = it }
            seconds = 0
            minutes = 0
            isStart = true
            progressBar.visibility = ProgressBar.INVISIBLE
            handler.removeCallbacks(updateLight)
            settingsButton.isEnabled = true
            textView.setTextColor(Color.BLACK)
            countForChangeColor = 0
        }
    }

    private val updateLight: Runnable = object : Runnable {
        override fun run() {
            color = colors[(colors.indexOf(color) + 1) % colors.size]
            handler.postDelayed(this, 1000)
            progressBar.indeterminateTintList = ColorStateList.valueOf(color)
        }
    }


    private val startTime: Runnable = object : Runnable {
        override fun run() {
            seconds++
            val totalTamingInSec = seconds

            if (seconds == 60) {
                seconds = 0
                minutes++
            }
            if (totalTamingInSec == countForChangeColor) {
                textView.setTextColor(Color.RED)
                createNotification()
            }


            val time = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            textView.text = time
            handler.postDelayed(this, 1000)
        }

    }


    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = "org.hyperskill"
            val descriptionText = "org.hyperskill"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Notification")
            .setContentText("Time exceeded")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(393939, notificationBuilder.build())
    }

}