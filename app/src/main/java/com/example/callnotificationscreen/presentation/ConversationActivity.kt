package com.example.callnotificationscreen.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.callnotificationscreen.R
import com.example.callnotificationscreen.domain.IncomingCallHandler
import com.example.callnotificationscreen.utils.FlashlightUtils
import com.example.callnotificationscreen.utils.NOTIFICATION_ID
import com.example.callnotificationscreen.utils.showOnLockScreen


class ConversationActivity : AppCompatActivity() {
    var notificationId: Int = 0

    // TODO here you can implement your call logic
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOnLockScreen()

        notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
        IncomingCallHandler.cancelNotification(notificationId = notificationId) // Of course you may want to cancel notification but update regarding foreground service
        FlashlightUtils.flashlightStop()

        setContentView(R.layout.activity_conversation)
        findViewById<Button>(R.id.end_call_button).setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        IncomingCallHandler.incomingCallWasDismissed(notificationId)
    }


    companion object {
        fun createNewIntent(context: Context, notificationId: Int?): Intent {
            val acceptIntent = Intent(context, ConversationActivity::class.java)
            acceptIntent.putExtra(NOTIFICATION_ID, notificationId)
            return acceptIntent
        }
    }
}