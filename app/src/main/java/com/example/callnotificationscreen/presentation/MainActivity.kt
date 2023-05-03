package com.example.callnotificationscreen.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.callnotificationscreen.domain.NotificationHandler
import com.example.callnotificationscreen.domain.NotificationReceiver
import com.example.callnotificationscreen.R
import com.example.callnotificationscreen.utils.SCHEDULE_TIME
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    fun dd() {
        Log.d("XXX", "Canceled MainActivity")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.showFullScreenIntentLockScreenWithDelayButton).setOnClickListener {
            makeScheduleNotification()
        }

        NotificationHandler.setDismissPressListener(::dd)
        NotificationHandler.removeDismissPressListener(::dd)

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