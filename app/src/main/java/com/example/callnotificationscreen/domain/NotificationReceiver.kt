package com.example.callnotificationscreen.domain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = System.currentTimeMillis().toInt()
        Log.d("XXXX", "sendNotification: ${notificationId}")

        IncomingCallHandler.sendNotification(
            context,
            NotificationData(
                name = "Lubov Shuliak",
                avatarUrl = "https://upload.wikimedia.org/wikipedia/commons/1/15/Cat_August_2010-4.jpg",
//                avatarUrl = "https://i.natgeofe.com/n/548467d8-c5f1-4551-9f58-6817a8d2c45e/NationalGeographic_2572187_square.jpg",
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
