package com.example.callnotificationscreen.utils

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.PowerManager
import androidx.appcompat.app.AppCompatActivity
import java.lang.reflect.Method

class XiomiHelper(val context: Context) {
    fun navigateToLockScreenPermission() {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.setClassName(
            "com.miui.securitycenter",
            "com.miui.permcenter.permissions.PermissionsEditorActivity"
        )
        intent.putExtra("extra_pkgname", context.packageName)
        context.startActivity(intent)
    }

    fun isShowOnLockScreenPermissionEnable(): Boolean? {
        return try {
            val manager =
                context.getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
            val method: Method = AppOpsManager::class.java.getDeclaredMethod(
                "checkOpNoThrow",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java
            )
            val result = method.invoke(
                manager,
                IS_SOWN_ON_LOCK_SCREEN,
                Binder.getCallingUid(),
                context.packageName
            ) as Int
            AppOpsManager.MODE_ALLOWED == result
        } catch (e: Exception) {
            null
        }
    }

    fun isIgnoringBatteryOptimization(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }


    fun isXiaomi(): Boolean {
        return "xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)
    }

    companion object {
        const val IS_SOWN_ON_LOCK_SCREEN = 10020
    }
}