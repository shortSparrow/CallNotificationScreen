package com.example.callnotificationscreen.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.callnotificationscreen.domain.IncomingCallHandler
import com.example.callnotificationscreen.utils.NOTIFICATION_ID

class NotificationDismissedReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // HERE you can implement logic on delete notification or just use the same on dismiss
        intent?.let {
            val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
            IncomingCallHandler.notifyDismissWasPressed(notificationId)
        }

    }

    companion object {
        fun createNewIntent(context: Context, notificationId: Int?): Intent {
            val incomingCallIntent = Intent(context, NotificationDismissedReceiver::class.java)
            incomingCallIntent.putExtra(NOTIFICATION_ID, notificationId)
            return incomingCallIntent
        }
    }
}