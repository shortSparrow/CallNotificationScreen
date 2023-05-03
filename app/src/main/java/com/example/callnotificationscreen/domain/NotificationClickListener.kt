package com.example.callnotificationscreen.domain

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.callnotificationscreen.utils.NOTIFICATION_ID

open class NotificationClickListener {
    private val dismissListener = mutableListOf<() -> Unit>()

    // TODO maybe also add Accept button clicked
    fun dismissedButtonClicked(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
        notificationManager.cancel(notificationId)

//        val callScreenIntent = Intent(context, IncomingCallActivity::class.java)
//        PendingIntent.getBroadcast(
//            context, ACCEPT_REQUEST_CODE, callScreenIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
//        ).cancel();
//
//        val dismissIntent = Intent(context, NotificationReceiver::class.java)
//        PendingIntent.getBroadcast(
//            context, DISMISS_REQUEST_CODE, dismissIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
//        ).cancel();

        dismissListener.forEach {
            it()
        }
    }

    fun setDismissPressListener(listener: () -> Unit) {
        Log.d("XXX", "setDismissPressListener: ${listener.hashCode()}")
        dismissListener.add(listener)
    }

    fun removeDismissPressListener(listener: () -> Unit) {
        Log.d("XXX", "removeDismissPressListener: ${listener.hashCode()}")
        dismissListener.removeAll { it == listener }
    }
}