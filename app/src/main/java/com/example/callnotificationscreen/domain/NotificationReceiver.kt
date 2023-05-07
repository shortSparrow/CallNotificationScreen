package com.example.callnotificationscreen.domain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = System.currentTimeMillis().toInt()

        IncomingCallHandler.sendNotification(
            context,
            NotificationData(
                name = "Your Friend",
                avatarUrl = "https://upload.wikimedia.org/wikipedia/commons/1/15/Cat_August_2010-4.jpg",
                notificationId = notificationId
            )
        )
    }

    companion object {
        fun build(context: Context): Intent {
            return Intent(context, NotificationReceiver::class.java)
        }
    }
}
