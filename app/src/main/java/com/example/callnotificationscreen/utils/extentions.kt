package com.example.callnotificationscreen.utils

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity


fun Activity.turnScreenOnAndKeyguardOff() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
    } else {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
    }

    with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
        }
    }
}

// я хз якак між ними різниця, але наче працюють обидва
fun Activity.turnScreenOnAndKeyguardOff_V2() {
    val window = window
    window.addFlags(
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
    )

    // to wake up the screen
    val pm = applicationContext.getSystemService(AppCompatActivity.POWER_SERVICE) as PowerManager
    val wakeLock = pm.newWakeLock(
        (PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP),
        "myapp:useless_tag"
    )
    wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)

    // to release the screen lock
    val keyguardManager =
        applicationContext.getSystemService(AppCompatActivity.KEYGUARD_SERVICE) as KeyguardManager
    val keyguardLock = keyguardManager.newKeyguardLock("TAG")
    keyguardLock.disableKeyguard()
}