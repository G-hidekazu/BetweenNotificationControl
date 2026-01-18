package com.between.notificationcontrol

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val prefsName = "between_prefs"
    private val settingKey = "lockscreen_enabled"
    private val channelId = "between_lock_screen"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toggleButton = findViewById<ImageButton>(R.id.toggleButton)
        val statusText = findViewById<TextView>(R.id.statusText)
        val helperText = findViewById<TextView>(R.id.helperText)

        val isEnabled = readSetting()
        updateUi(isEnabled, toggleButton, statusText, helperText)
        ensureNotificationChannel(isEnabled)

        toggleButton.setOnClickListener {
            val newValue = !readSetting()
            saveSetting(newValue)
            ensureNotificationChannel(newValue)
            updateUi(newValue, toggleButton, statusText, helperText)
        }
    }

    private fun readSetting(): Boolean {
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        return prefs.getBoolean(settingKey, true)
    }

    private fun saveSetting(isEnabled: Boolean) {
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(settingKey, isEnabled).apply()
    }

    private fun updateUi(
        isEnabled: Boolean,
        toggleButton: ImageButton,
        statusText: TextView,
        helperText: TextView
    ) {
        val colorRes = if (isEnabled) R.color.between_green else R.color.between_red
        val label = if (isEnabled) "ON" else "OFF"
        val helper = if (isEnabled) {
            "ON: ロック画面にBetweenの通知を表示"
        } else {
            "OFF: ロック画面にBetweenの通知を表示しない"
        }

        toggleButton.imageTintList = ContextCompat.getColorStateList(this, colorRes)
        statusText.text = "ロック画面通知: $label"
        helperText.text = helper
    }

    private fun ensureNotificationChannel(isEnabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val lockscreenVisibility = if (isEnabled) {
                android.app.Notification.VISIBILITY_PUBLIC
            } else {
                android.app.Notification.VISIBILITY_SECRET
            }
            val channel = NotificationChannel(
                channelId,
                "Between ロック画面通知",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Betweenのロック画面通知表示を管理します"
                lockscreenVisibility = lockscreenVisibility
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
