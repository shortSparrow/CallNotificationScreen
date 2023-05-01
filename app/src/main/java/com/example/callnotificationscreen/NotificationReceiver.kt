package com.example.callnotificationscreen

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    private val soundUri =
        Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://com.example.callnotificationscreen/${R.raw.reminder_sound}");

    override fun onReceive(context: Context, intent: Intent) {
        makeNotification(context)
    }

    private fun makeNotification(context: Context) {
        val fullScreenIntent = Intent(context, CallActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.arrow_up_float)
            .setContentTitle("My notification")
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_MAX) // must be at least HIGH
            .setSound(soundUri)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // not sure that is needed
            .setFullScreenIntent(fullScreenPendingIntent, true)


        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        with(notificationManager) {
            buildChannel()
            val notification = builder.build()
            notification.flags = Notification.FLAG_INSISTENT; // repeat sound
            notify(System.currentTimeMillis().toInt(), notification) // use System.currentTimeMillis().toInt() because id should be uniq
        }
    }

    private fun buildChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Example Notification Channel"
            val descriptionText = "This is used to demonstrate the Full Screen Intent"
            val importance = NotificationManager.IMPORTANCE_HIGH //  must be at least HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(soundUri, attributes);
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC // not sure that is needed
        }
    }

    companion object {
        const val CHANNEL_ID = "1"
        fun build(context: Context): Intent {
            return Intent(context, NotificationReceiver::class.java)
//                .also {
//                    it.putExtra(LOCK_SCREEN_KEY, isLockScreen)
//                }
        }
    }
}