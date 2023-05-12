package com.example.callnotificationscreen.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.callnotificationscreen.domain.IncomingCallHandler
import com.example.callnotificationscreen.domain.NotificationData


class LocalNotificationReceiver : BroadcastReceiver() {
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
        fun createNewIntent(context: Context): Intent {
            return Intent(context, LocalNotificationReceiver::class.java)
        }
    }
}
