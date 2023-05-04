package com.example.callnotificationscreen.domain

import android.app.NotificationManager
import android.content.Context

// handle behavior after pressing dismiss button in notification/activity (not handle click itself!)
open class IncomingCallDismissPressListener {
    private val dismissListener = mutableListOf<() -> Unit>()

    fun notifyDismissWasPressed(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = IncomingCallHandler.getNotificationParsedData()?.notificationId
        notificationId?.let {
            notificationManager.cancel(notificationId)
        }

        dismissListener.forEach {
            it()
        }
    }

    fun addDismissPressListener(listener: () -> Unit) {
        dismissListener.add(listener)
    }

    fun removeDismissPressListener(listener: () -> Unit) {
        dismissListener.removeAll { it == listener }
    }
}