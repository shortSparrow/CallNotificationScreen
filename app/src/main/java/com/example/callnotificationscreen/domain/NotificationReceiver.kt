package com.example.callnotificationscreen.domain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "DISMISS") {
            NotificationHandler.dismissedButtonClicked(context, intent)
        } else {
            Log.d("XXXX", "sendNotification")
            NotificationHandler.sendNotification(context)
        }
    }


    companion object {
        fun build(context: Context): Intent {
            return Intent(context, NotificationReceiver::class.java)
        }
    }
}
