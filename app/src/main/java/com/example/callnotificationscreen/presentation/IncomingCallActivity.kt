package com.example.callnotificationscreen.presentation

import android.app.KeyguardManager
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.callnotificationscreen.R
import com.example.callnotificationscreen.domain.NotificationHandler


class IncomingCallActivity : AppCompatActivity() {
    private fun dismissActionListener() {
        Log.d("XXX", "Canceled IncomingCallActivity")
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unlockScreen()

        setContentView(R.layout.activity_incoming_call)
        Log.d("XXX", "IncomingCallActivity onCreate")
        NotificationHandler.setDismissPressListener(::dismissActionListener)
    }


    override fun onDestroy() {
        super.onDestroy()
        NotificationHandler.removeDismissPressListener(::dismissActionListener)
    }

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
            "myapp:mywakelocktal2"
        )
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)

        // to release the screen lock
        val keyguardManager =
            applicationContext.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        val keyguardLock = keyguardManager.newKeyguardLock("TAG")
        keyguardLock.disableKeyguard()
    }
}