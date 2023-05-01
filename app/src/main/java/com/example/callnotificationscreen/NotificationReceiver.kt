package com.example.callnotificationscreen

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target


class NotificationReceiver : BroadcastReceiver() {
    private val soundUri =
        Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://com.example.callnotificationscreen/${R.raw.reminder_sound}");
    private var bitmapAvatar: Bitmap? = null
    private val isLoadingAvatarComplete: MutableLiveData<Boolean> = MutableLiveData(false)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "DISMISS") {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
            Log.d("XXX", notificationId.toString())
            notificationManager.cancel(notificationId)

        } else {
            sendNotification(context)

        }
    }

    private fun sendNotification(context: Context) {
        loadAvatar("https://upload.wikimedia.org/wikipedia/commons/1/15/Cat_August_2010-4.jpg")
        isLoadingAvatarComplete.observeForever {
            if (it == true) {
                val notificationId = System.currentTimeMillis().toInt()
                buildChannel(context)
                val builder = makeNotificationBuilder(context, notificationId)

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                with(notificationManager) {
                    val notification = builder.build()
                    notification.flags = Notification.FLAG_INSISTENT; // repeat sound
                    notify(
                        notificationId,
                        notification
                    ) // use System.currentTimeMillis().toInt() because id should be uniq
                }
            }
        }
    }

    private fun loadAvatar(url: String) {
        Picasso.get()
            .load(url)
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: LoadedFrom?) {
                    bitmapAvatar = bitmap
                    isLoadingAvatarComplete.value = true
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    isLoadingAvatarComplete.value = true
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }

            })
    }


    private fun makeNotificationBuilder(
        context: Context,
        notificationId: Int
    ): NotificationCompat.Builder {
        val callScreenIntent = Intent(context, CallActivity::class.java)
        val callScreenIntentPendingIntent = PendingIntent.getActivity(
            context,
            ACCEPT_REQUEST_CODE,
            callScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val dismissIntent = Intent(context, NotificationReceiver::class.java)
        dismissIntent.action = "DISMISS"
        dismissIntent.putExtra(NOTIFICATION_ID, notificationId)
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE // was PendingIntent.FLAG_MUTABLE
        )
        // I pass here notificationId because if use constant i get error when  intent.getIntExtra(NOTIFICATION_ID, 0) in Receive has always old value

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.arrow_up_float)
//            .setContentTitle("My notification")
//            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_MAX) // must be at least HIGH
            .setSound(soundUri)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // not sure that is needed
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(callScreenIntentPendingIntent)
            .setFullScreenIntent(callScreenIntentPendingIntent, true)

        val remoteView =
            RemoteViews("com.example.callnotificationscreen", R.layout.custom_notification_v2)
        remoteView.setOnClickPendingIntent(R.id.accept_button, callScreenIntentPendingIntent) // TODO on open activity hide notification (hands on effect)
        remoteView.setOnClickPendingIntent(R.id.decline_button, dismissPendingIntent)

        builder.setCustomContentView(remoteView)
        remoteView.setImageViewBitmap(R.id.avatar, bitmapAvatar);
        remoteView.setTextViewText(R.id.person_name, "Lubov")

        return builder
    }


    private fun buildChannel(context: Context) {
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
            channel.importance = NotificationManager.IMPORTANCE_HIGH
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "1"
        const val NOTIFICATION_ID = "notificationId"
        const val ACCEPT_REQUEST_CODE = 0
        fun build(context: Context): Intent {
            return Intent(context, NotificationReceiver::class.java)
        }
    }
}


//        class DownloadImageTask : AsyncTask<String, Void, Bitmap>() {
//            override fun doInBackground(vararg urls: String): Bitmap? {
//                val imageUrl = urls[0]
//                var bitmap: Bitmap? = null
//                try {
//                    val connection = URL(imageUrl).openConnection() as HttpURLConnection
//                    connection.connect()
//                    val input = connection.inputStream
//                    bitmap = BitmapFactory.decodeStream(input)
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//                return bitmap
//            }
//
//            override fun onPostExecute(result: Bitmap?) {
//                if (result != null) {
//                    val remoteView =
//                        RemoteViews("com.example.callnotificationscreen", R.layout.custom_notification_v2)
//                    remoteView.setOnClickPendingIntent(R.id.accept_button, fullScreenPendingIntent)
//                    builder.setCustomContentView(remoteView)
//
//                    remoteView.setImageViewBitmap(R.id.avatar, result);
//                    val notificationManager =
//                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//                    with(notificationManager) {
//                        buildChannel(context)
//                        val notification = builder.build()
//                        notification.flags = Notification.FLAG_INSISTENT; // repeat sound
//                        notify(
//                            System.currentTimeMillis().toInt(),
//                            notification
//                        ) // use System.currentTimeMillis().toInt() because id should be uniq
//                    }
//                }
//            }
//        }
//        DownloadImageTask().execute("https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Cat03.jpg/1200px-Cat03.jpg")