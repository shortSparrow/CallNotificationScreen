package com.example.callnotificationscreen.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.callnotificationscreen.R
import com.example.callnotificationscreen.domain.IncomingCallHandler
import com.example.callnotificationscreen.utils.FlashlightUtils
import com.example.callnotificationscreen.utils.NOTIFICATION_ID

class ConversationActivity : AppCompatActivity() {
    // TODO here you can implement your call logic
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
        IncomingCallHandler.cancelNotification(notificationId = notificationId)
        FlashlightUtils.flashlightStop()

        setContentView(R.layout.activity_conversation)
    }

    companion object {
        fun createNewIntent(context: Context, notificationId: Int?): Intent {
            val acceptIntent = Intent(context, ConversationActivity::class.java)
            acceptIntent.putExtra(NOTIFICATION_ID, notificationId)
            return acceptIntent
        }
    }
}