package com.example.callnotificationscreen.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.callnotificationscreen.domain.IncomingCallHandler
import com.example.callnotificationscreen.utils.NOTIFICATION_ID

// Needed only for collapsing android notification panel
// context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) work only for Android 11 and below
// so create empty activity, this close notification panel and then we finish one
class DismissDummyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra(NOTIFICATION_ID, 0)
        IncomingCallHandler.notifyDismissWasPressed(
            IncomingCallHandler.getNotificationParsedData(id)?.notificationId
        )
        finish()
    }


    companion object {
        fun createNewIntent(context: Context, notificationId: Int?): Intent {
            val dismissIntent = Intent(context, DismissDummyActivity::class.java)
            dismissIntent.putExtra(NOTIFICATION_ID, notificationId)
            return dismissIntent
        }
    }
}