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
import com.example.callnotificationscreen.utils.ACCEPT_INCOMING_CALL_REQUEST_CODE
import com.example.callnotificationscreen.utils.CHANNEL_ID
import com.example.callnotificationscreen.utils.DISMISS_INCOMING_CALL_REQUEST_CODE
import com.example.callnotificationscreen.utils.GO_TO_CALL_SCREEN_REQUEST_CODE
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

// TODO add ability handle multiple incoming calls
object IncomingCallHandler : IncomingCallDismissPressListener() {
    private val soundUri =
        Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${CallNotificationApp.getContext().packageName}/${R.raw.reminder_sound}")
    private val scope = CoroutineScope(Dispatchers.IO)
    private var notificationParsedData: NotificationData? = null


    fun getNotificationParsedData() = notificationParsedData

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
            notificationParsedData = notificationData.copy(bitmapAvatar = bitmapAvatar)


            buildChannel(context)
            val builder = makeNotificationBuilder(context)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationParsedData?.notificationId?.let { notificationId ->
                with(notificationManager) {
                    val notification = builder.build()
                    notification.flags = Notification.FLAG_INSISTENT // repeat sound
                    notify(notificationId, notification)
                }
            }

        }
    }

    private fun makeNotificationBuilder(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.arrow_up_float)
            .setPriority(NotificationCompat.PRIORITY_MAX) // must be at least HIGH
            .setSound(soundUri)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // not sure that is needed
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .apply {
                inflateCustomNotification(context, this)
            }
    }

    // TODO probably remove FLAG_UPDATE_CURRENT and make all FLAG_IMMUTABLE
    private fun inflateCustomNotification(context: Context, builder: NotificationCompat.Builder) {
        val callScreenIntent = Intent(context, IncomingCallActivity::class.java)
        val callScreenIntentPendingIntent = PendingIntent.getActivity(
            context,
            GO_TO_CALL_SCREEN_REQUEST_CODE,
            callScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(context, DismissDummyActivity::class.java)
        // add these flags to open activity in new task
        // the same you can do just add taskAffinity in AndroidManifest
        // this allow us to close new task and don't affect mainActivity
        dismissIntent.addFlags(FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

        val dismissPendingIntent = PendingIntent.getActivity(
            context,
            DISMISS_INCOMING_CALL_REQUEST_CODE,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val acceptIntent = Intent(context, ConversationActivity::class.java)
        val acceptPendingIntent = PendingIntent.getActivity(
            context,
            ACCEPT_INCOMING_CALL_REQUEST_CODE,
            acceptIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val remoteViewBigContent =
            RemoteViews(context.applicationContext.packageName, R.layout.custom_notification_v3)
        val remoteViewCompact =
            RemoteViews(
                context.applicationContext.packageName,
                R.layout.custom_notification_compact
            )

        remoteViewBigContent.setOnClickPendingIntent(R.id.accept_button, acceptPendingIntent)
        remoteViewBigContent.setOnClickPendingIntent(R.id.decline_button, dismissPendingIntent)
        remoteViewBigContent.setImageViewBitmap(R.id.avatar, notificationParsedData?.bitmapAvatar)
        remoteViewBigContent.setTextViewText(R.id.person_name, notificationParsedData?.name)
        remoteViewCompact.setTextViewText(R.id.person_name, notificationParsedData?.name)
        remoteViewCompact.setImageViewBitmap(R.id.avatar, notificationParsedData?.bitmapAvatar)

        builder.setCustomContentView(remoteViewCompact)
        builder.setCustomBigContentView(remoteViewBigContent)
        builder.setCustomHeadsUpContentView(remoteViewBigContent)

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
            channel.importance = NotificationManager.IMPORTANCE_HIGH
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}