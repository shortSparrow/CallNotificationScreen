package com.example.callnotificationscreen.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
            finish() // needed because on wakelock onDestroy may not work, so do it manually
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        IncomingCallHandler.removeCallDismissListener(::dismissActionListener)
        FlashlightUtils.flashlightStop()
    }

    private fun dismissActionListener() {
        finish()
    }

    companion object {
        fun createNewIntent(context: Context, notificationId: Int?): Intent {
            val incomingCallIntent = Intent(context, IncomingCallActivity::class.java)
            incomingCallIntent.putExtra(NOTIFICATION_ID, notificationId)
            return incomingCallIntent
        }
    }
}