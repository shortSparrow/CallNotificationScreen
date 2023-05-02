package com.example.callnotificationscreen

import android.app.NotificationManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class ConversationActivity : AppCompatActivity() {
    // TODO here ypu can implement your call logic
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationId = intent.getIntExtra(NotificationReceiver.NOTIFICATION_ID, 0)
        Log.d("XXX", notificationId.toString())
        if(notificationId != 0 ) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        }

        setContentView(R.layout.activity_conversation)
    }
}