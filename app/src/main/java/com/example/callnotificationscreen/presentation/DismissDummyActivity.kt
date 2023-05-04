package com.example.callnotificationscreen.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.callnotificationscreen.domain.IncomingCallHandler

// Needed only for collapsing android notification panel
// context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) work only for Android 11 and below
// so create empty activity, this close notification panel and then we finish one
class DismissDummyActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IncomingCallHandler.notifyDismissWasPressed(this)
        finish()
    }
}