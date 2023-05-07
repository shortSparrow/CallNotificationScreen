package com.example.callnotificationscreen.presentation

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.callnotificationscreen.CallNotificationApp.Companion.TAG
import com.example.callnotificationscreen.R
import com.example.callnotificationscreen.domain.IncomingCallHandler
import com.example.callnotificationscreen.utils.FlashlightUtils
import com.example.callnotificationscreen.utils.NOTIFICATION_ID
import com.example.callnotificationscreen.utils.turnScreenOnAndKeyguardOff


class IncomingCallActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        turnScreenOnAndKeyguardOff()
        IncomingCallHandler.addCallDismissListener(::dismissActionListener)
        setContentView(R.layout.activity_incoming_call)

        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
        val notificationParsedData = IncomingCallHandler.getNotificationParsedData(notificationId)
        findViewById<TextView>(R.id.title).text = notificationParsedData?.name
        findViewById<ImageView>(R.id.avatar_large).setImageBitmap(notificationParsedData?.bitmapAvatar)

        findViewById<Button>(R.id.decline_button).setOnClickListener {
            IncomingCallHandler.incomingCallWasDismissed(notificationId = notificationId)
        }
        findViewById<Button>(R.id.accept_button).setOnClickListener {
            val acceptIntent = ConversationActivity.createNewIntent(this, notificationId)
            startActivity(acceptIntent)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        IncomingCallHandler.removeCallDismissListener(::dismissActionListener)
        FlashlightUtils.flashlightStop()
        Log.d(TAG, "onDestroy IncomingCallActivity")
    }

    private fun dismissActionListener() {
        finish()
    }

    // TODO maybe delete
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


    companion object {
        fun createNewIntent(context: Context, notificationId: Int?): Intent {
            val incomingCallIntent = Intent(context, IncomingCallActivity::class.java)
            incomingCallIntent.putExtra(NOTIFICATION_ID, notificationId)
            return incomingCallIntent
        }
    }
}