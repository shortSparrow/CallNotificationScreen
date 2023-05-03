package com.example.callnotificationscreen.presentation

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.callnotificationscreen.R
import com.example.callnotificationscreen.utils.NOTIFICATION_ID

class ConversationActivity : AppCompatActivity() {
    // TODO here you can implement your call logic
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
        if(notificationId != 0 ) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        }

        setContentView(R.layout.activity_conversation)
    }
}