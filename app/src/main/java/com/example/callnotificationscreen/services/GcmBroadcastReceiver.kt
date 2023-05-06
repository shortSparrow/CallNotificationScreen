package com.example.callnotificationscreen.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.callnotificationscreen.CallNotificationApp.Companion.TAG
import com.example.callnotificationscreen.domain.IncomingCallHandler
import com.example.callnotificationscreen.domain.NotificationData

// For Xiaomi alternative way of autostart (But autostart is better, because this way is detracted, and change by Google to FirebaseMessagingService
class GcmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val bundle = intent!!.extras
            if (bundle != null) {
                IncomingCallHandler.sendNotification(
                    context,
                    NotificationData(
                        name = bundle.getString("title", "no title"),
                        avatarUrl = bundle.getString("avatarUrl", ""),
                        notificationId = bundle.getString("google.message_id", "qsdfegeg")
                            .hashCode()
                    )
                )
                Log.d(TAG, bundle.getString("body").toString())
            }
            context.startService(Intent(context, MyFirebaseMessagingService::class.java))
        }
    }
}