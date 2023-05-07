package com.example.callnotificationscreen.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.callnotificationscreen.CallNotificationApp.Companion.TAG
import com.example.callnotificationscreen.R
import com.example.callnotificationscreen.domain.NotificationReceiver
import com.example.callnotificationscreen.utils.SCHEDULE_TIME
import com.example.callnotificationscreen.utils.XiomiHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import xyz.kumaraswamy.autostart.Autostart
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.showFullScreenIntentLockScreenWithDelayButton).setOnClickListener {
            makeScheduleNotification()
        }

        val xiomiHelper = XiomiHelper(this)
        if (xiomiHelper.isXiaomi()) {
            if (xiomiHelper.isShowOnLockScreenPermissionEnable() != true) {
                // need for show incomingCall activity on lock screen for xiomi
                xiomiHelper.lockScreenPermissionDialog()
            }

            // need for ability get fcm push anytime
            val autostart = Autostart(applicationContext)
            val state: Autostart.State = autostart.autoStartState
            if (state === Autostart.State.DISABLED) {
                xiomiHelper.showAutostartPermissionDialog()
            }
        }


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            Log.d(TAG, task.result)
        })

    }

    private fun makeScheduleNotification() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timeInMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(SCHEDULE_TIME)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            NotificationReceiver.build(this),
            PendingIntent.FLAG_IMMUTABLE
        )

        with(alarmManager) {
            setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }
    }

}