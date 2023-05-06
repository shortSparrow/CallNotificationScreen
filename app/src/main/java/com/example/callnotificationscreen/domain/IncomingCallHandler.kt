package com.example.callnotificationscreen.domain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.callnotificationscreen.CallNotificationApp
import com.example.callnotificationscreen.R
import com.example.callnotificationscreen.presentation.ConversationActivity
import com.example.callnotificationscreen.presentation.DismissDummyActivity
import com.example.callnotificationscreen.presentation.IncomingCallActivity
import com.example.callnotificationscreen.services.NotificationDismissedReceiver
import com.example.callnotificationscreen.utils.CHANNEL_ID
import com.example.callnotificationscreen.utils.getBitmapFromVectorDrawable
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


data class NotificationData(
    val avatarUrl: String? = null,
    val bitmapAvatar: Bitmap? = null,
    val name: String,
    val notificationId: Int
)


object IncomingCallHandler : IncomingCallDismissPressListener() {
    private val soundUri =
        Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${CallNotificationApp.getContext().packageName}/${R.raw.reminder_sound}")
    private val scope = CoroutineScope(Dispatchers.IO)
    private var notificationQue = mutableListOf<NotificationData>()

    fun getNotificationParsedData(id: Int): NotificationData? {
        return notificationQue.find { it.notificationId == id }
    }

    private fun removeNotificationFromQue(id: Int) {
        notificationQue.removeIf { it.notificationId == id }
    }

    fun cancelNotification(notificationId: Int?) {
        val notificationManager =
            CallNotificationApp.getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationId?.let {
            notificationManager.cancel(notificationId)
            removeNotificationFromQue(notificationId)
        }

    }

    fun sendNotification(context: Context, notificationData: NotificationData) {
        scope.launch {
            val bitmapAvatar = try {
                Picasso.get()
                    .load(notificationData.avatarUrl)
                    .get()
            } catch (e: Exception) {
                // when no internet or something went wrong
                // Only fot xml, for png ot jpg use  BitmapFactory.decodeResource(context.resources, R.drawable.person_avatar_placeholder)
                getBitmapFromVectorDrawable(context, R.drawable.avatar)
            }
            val notificationParsedData = notificationData.copy(bitmapAvatar = bitmapAvatar)
            notificationQue.add(notificationParsedData)

            buildChannel(context)
            val builder = makeNotificationBuilder(context, notificationParsedData.notificationId)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            with(notificationManager) {
                val notification = builder.build()
                notification.flags = Notification.FLAG_INSISTENT // repeat sound
                notify(notificationParsedData.notificationId, notification)
            }

        }
    }

    private fun makeNotificationBuilder(
        context: Context,
        notificationId: Int
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.arrow_up_float)
            .setPriority(NotificationCompat.PRIORITY_MAX) // must be at least HIGH
            .setSound(soundUri)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // not sure that is needed
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .apply {
                inflateCustomNotification(context, this, notificationId)
            }
    }

    private fun inflateCustomNotification(
        context: Context,
        builder: NotificationCompat.Builder,
        notificationId: Int
    ) {
        val callScreenIntent = IncomingCallActivity.createNewIntent(context, notificationId)
        val callScreenIntentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,  // Here I user id as requestCode because with requestCode as constant I had unexpected behavior with multiple notifications
            callScreenIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = DismissDummyActivity.createNewIntent(context, notificationId)
        // add these flags to open activity in new task, the same you can do just add taskAffinity in AndroidManifest
        // this allow us to close new task and don't affect mainActivity
        dismissIntent.addFlags(FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        val dismissPendingIntent = PendingIntent.getActivity(
            context,
            notificationId, // Here I user id as requestCode because with requestCode as constant I had unexpected behavior with multiple notifications
            dismissIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


        val deleteIntent = NotificationDismissedReceiver.createNewIntent(context, notificationId)
        val deletePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId, // Here I user id as requestCode because with requestCode as constant I had unexpected behavior with multiple notifications
            deleteIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val acceptIntent = ConversationActivity.createNewIntent(context, notificationId)
        val acceptPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,  // Here I user id as requestCode because with requestCode as constant I had unexpected behavior with multiple notifications
            acceptIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val remoteViewBigContent =
            RemoteViews(context.applicationContext.packageName, R.layout.custom_notification)
        val remoteViewCompact =
            RemoteViews(
                context.applicationContext.packageName,
                R.layout.custom_notification_compact
            )

        val currentNotificationParsedData = getNotificationParsedData(notificationId)
        remoteViewBigContent.setOnClickPendingIntent(R.id.accept_button, acceptPendingIntent)
        remoteViewBigContent.setOnClickPendingIntent(R.id.decline_button, dismissPendingIntent)
        remoteViewBigContent.setImageViewBitmap(
            R.id.avatar,
            currentNotificationParsedData?.bitmapAvatar
        )
        remoteViewBigContent.setTextViewText(R.id.person_name, currentNotificationParsedData?.name)
        remoteViewCompact.setTextViewText(R.id.person_name, currentNotificationParsedData?.name)
        remoteViewCompact.setImageViewBitmap(
            R.id.avatar,
            currentNotificationParsedData?.bitmapAvatar
        )

        builder.setCustomContentView(remoteViewCompact)
        builder.setCustomBigContentView(remoteViewBigContent)
        builder.setCustomHeadsUpContentView(remoteViewBigContent)
        builder.setDeleteIntent(deletePendingIntent) // on hide from notification panel

        builder.setContentIntent(callScreenIntentPendingIntent)
        builder.setFullScreenIntent(callScreenIntentPendingIntent, true)
    }


    // TODO add light flashing
    private fun buildChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Example Notification Channel"
            val descriptionText = "This is used to demonstrate the Full Screen Intent"
            val importance = NotificationManager.IMPORTANCE_HIGH //  must be at least HIGH
            val channel =
                NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }

            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            channel.enableLights(true)
            channel.enableVibration(true)
            channel.setSound(soundUri, attributes)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC // not sure that is needed
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}