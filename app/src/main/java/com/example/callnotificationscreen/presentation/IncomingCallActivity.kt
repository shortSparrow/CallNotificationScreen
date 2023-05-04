package com.example.callnotificationscreen.presentation

import android.app.KeyguardManager
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.callnotificationscreen.R
import com.example.callnotificationscreen.domain.IncomingCallHandler
import com.example.callnotificationscreen.utils.turnScreenOnAndKeyguardOff


class IncomingCallActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        turnScreenOnAndKeyguardOff()
        IncomingCallHandler.addDismissPressListener(::dismissActionListener)
        setContentView(R.layout.activity_incoming_call)

        val notificationParsedData = IncomingCallHandler.getNotificationParsedData()
        findViewById<TextView>(R.id.title).text = notificationParsedData?.name
        findViewById<ImageView>(R.id.avatar_large).setImageBitmap(notificationParsedData?.bitmapAvatar)

        findViewById<Button>(R.id.decline_button).setOnClickListener {
            IncomingCallHandler.notifyDismissWasPressed(context = this)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        IncomingCallHandler.removeDismissPressListener(::dismissActionListener)
    }

    private fun dismissActionListener() {
        finish()
    }

    // was change by turnScreenOnAndKeyguardOff
    private fun unlockScreen() {
        val window = window
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        // to wake up the screen
        val pm = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(
            (PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP),
            "myapp:useless_tag"
        )
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)

        // to release the screen lock
        val keyguardManager =
            applicationContext.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        val keyguardLock = keyguardManager.newKeyguardLock("TAG")
        keyguardLock.disableKeyguard()
    }
}