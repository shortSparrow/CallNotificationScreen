package com.example.callnotificationscreen.domain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.callnotificationscreen.R
import com.example.callnotificationscreen.presentation.ConversationActivity
import com.example.callnotificationscreen.presentation.IncomingCallActivity
import com.example.callnotificationscreen.utils.ACCEPT_INCOMING_CALL_REQUEST_CODE
import com.example.callnotificationscreen.utils.CHANNEL_ID
import com.example.callnotificationscreen.utils.DISMISS_INCOMING_CALL_REQUEST_CODE
import com.example.callnotificationscreen.utils.GO_TO_CALL_SCREEN_REQUEST_CODE
import com.example.callnotificationscreen.utils.NOTIFICATION_ID
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.random.Random

val DEFAULT_NOTIFICATION_ID = Random.nextInt(0, 1000)

object NotificationHandler : NotificationClickListener() {
    private var notificationId: Int = DEFAULT_NOTIFICATION_ID
    private val soundUri = // TODO there add also dynamic packageID
        Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://com.example.callnotificationscreen/${R.raw.reminder_sound}")
    private var bitmapAvatar: Bitmap? = null
    private val scope = MainScope()

    fun sendNotification(context: Context) {
        scope.launch {
            val request =
                async { loadAvatar("https://upload.wikimedia.org/wikipedia/commons/1/15/Cat_August_2010-4.jpg") }
            val avatarIcon = request.await()
            bitmapAvatar = avatarIcon
            // TODO add real notification Id
            notificationId = System.currentTimeMillis().toInt()

            buildChannel(context)
            val builder = makeNotificationBuilder(context)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            with(notificationManager) {
                val notification = builder.build()
                notification.flags = Notification.FLAG_INSISTENT // repeat sound
                notify(
                    notificationId,
                    notification
                ) // use System.currentTimeMillis().toInt() because id should be uniq
            }
        }
    }

    private fun loadAvatar(url: String): Bitmap? {
        var res: Bitmap? = null
        var isLoadingComplete = false

        while (!isLoadingComplete) {
            Picasso.get()
                .load(url)
                .resize(
                    200,
                    200
                ) // must set because for large file (4 mb) not call success or failure
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        res = bitmap
                        isLoadingComplete = true
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        isLoadingComplete = true
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                })
        }

        return res
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

    private fun inflateCustomNotification(context: Context, builder: NotificationCompat.Builder) {
        val callScreenIntent = Intent(context, IncomingCallActivity::class.java)
        val callScreenIntentPendingIntent = PendingIntent.getActivity(
            context,
            GO_TO_CALL_SCREEN_REQUEST_CODE,
            callScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val dismissIntent = Intent(context, NotificationReceiver::class.java)
        dismissIntent.action = "DISMISS"
        dismissIntent.putExtra(NOTIFICATION_ID, notificationId)

        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            DISMISS_INCOMING_CALL_REQUEST_CODE,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val acceptIntent = Intent(context, ConversationActivity::class.java)
        acceptIntent.putExtra(NOTIFICATION_ID, notificationId)
        val acceptPendingIntent = PendingIntent.getActivity(
            context,
            ACCEPT_INCOMING_CALL_REQUEST_CODE,
            acceptIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val remoteView =
            RemoteViews(context.applicationContext.packageName, R.layout.custom_notification_v3)
        val remoteViewCompact =
            RemoteViews(context.applicationContext.packageName, R.layout.custom_notification_compact)

        remoteView.setOnClickPendingIntent(R.id.accept_button, acceptPendingIntent)
        remoteView.setOnClickPendingIntent(R.id.decline_button, dismissPendingIntent)
        remoteView.setImageViewBitmap(R.id.avatar, bitmapAvatar)
        remoteView.setTextViewText(R.id.person_name, "Lubov")
        remoteViewCompact.setTextViewText(R.id.person_name, "Lubov")
        remoteViewCompact.setImageViewBitmap(R.id.avatar, bitmapAvatar)

        builder.setCustomContentView(remoteViewCompact)
        builder.setCustomBigContentView(remoteView)
        builder.setCustomHeadsUpContentView(remoteView)

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