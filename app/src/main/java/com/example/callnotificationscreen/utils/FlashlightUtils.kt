package com.example.callnotificationscreen.utils

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.util.Log
import com.example.callnotificationscreen.CallNotificationApp
import com.example.callnotificationscreen.CallNotificationApp.Companion.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object FlashlightUtils {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var isFlashlightBlinking = false


    fun flashlightStart() {
        // crash on emulator so prevent toggleFlashlight
        if (isProbablyRunningOnEmulator) return

        val context = CallNotificationApp.getContext()
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            isFlashlightBlinking = true
            scope.launch {
                while (isFlashlightBlinking) {
                    toggleFlashlight(true)
                    delay(200)
                    toggleFlashlight(false)
                    delay(200)
                }
            }
        }
    }

    fun flashlightStop() {
        // crash on emulator so prevent toggleFlashlight
        if (isProbablyRunningOnEmulator) return

        isFlashlightBlinking = false
        toggleFlashlight(false)
    }

    private fun toggleFlashlight(status: Boolean) {
        val context = CallNotificationApp.getContext()
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0]
        try {
            cameraManager.setTorchMode(cameraId, status)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.message.toString())
        }
    }
}
