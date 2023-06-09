package com.example.callnotificationscreen.services

import com.example.callnotificationscreen.domain.IncomingCallHandler
import com.example.callnotificationscreen.domain.NotificationData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//       Here we listen Data Messages: Theses messages trigger the onMessageReceived() callback
//       even if your app is in foreground/background/killed
        val data = remoteMessage.data
        val title: String? = data["title"]
        val avatarUrl: String? = data["avatarUrl"]
        if (title != null) {
            IncomingCallHandler.sendNotification(
                this,
                NotificationData(
                    name = title,
                    avatarUrl = avatarUrl,
                    notificationId = remoteMessage.messageId.hashCode()
                )
            )
        }
    }
}