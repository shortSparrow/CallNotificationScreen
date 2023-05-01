package com.example.callnotificationscreen

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.showFullScreenIntentLockScreenWithDelayButton).setOnClickListener {
            makeScheduleNotification()
        }
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

    companion object {
        const val SCHEDULE_TIME = 5L
    }
}