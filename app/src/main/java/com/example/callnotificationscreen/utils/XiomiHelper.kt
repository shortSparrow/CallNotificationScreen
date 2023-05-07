package com.example.callnotificationscreen.utils

import android.app.AlertDialog
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.PowerManager
import androidx.appcompat.app.AppCompatActivity
import java.lang.reflect.Method

class XiomiHelper(val context: Context) {
    fun navigateToLockScreenPermission() {
        val intent = Intent(APP_PERM_EDITOR)
        intent.setClassName(
            PACKAGE_XIAOMI_MAIN,
            PERMISSION_EDITOR_ACTIVITY
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


    fun showAutostartPermissionDialog() {
        AlertDialog.Builder(context)
            .setTitle("Allow AutoStart")
            .setMessage("Please enable auto start in settings.")
            .setPositiveButton("Allow") { _, _ -> navigateToAutoStart() }
            .setNegativeButton("No", null)
            .show()
            .setCancelable(true)
    }

    private fun navigateToAutoStart() {
        val intent = Intent()
        intent.component = ComponentName(PACKAGE_XIAOMI_MAIN, AUTO_START_MANAGEMENT_ACTIVITY)
        context.startActivity(intent)
    }


    fun isXiaomi(): Boolean {
        return "xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)
    }

    // maybe is also useful but for me working fine without it
    fun isIgnoringBatteryOptimization(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }


    companion object {
        private const val IS_SOWN_ON_LOCK_SCREEN = 10020
        private const val PACKAGE_XIAOMI_MAIN = "com.miui.securitycenter"
        private const val AUTO_START_MANAGEMENT_ACTIVITY =
            "com.miui.permcenter.autostart.AutoStartManagementActivity"
        const val PERMISSION_EDITOR_ACTIVITY =
            "com.miui.permcenter.permissions.PermissionsEditorActivity"
        const val APP_PERM_EDITOR = "miui.intent.action.APP_PERM_EDITOR"
    }
}