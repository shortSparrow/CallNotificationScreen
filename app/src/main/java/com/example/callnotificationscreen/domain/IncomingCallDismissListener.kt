package com.example.callnotificationscreen.domain

import com.example.callnotificationscreen.utils.FlashlightUtils

// handle behavior after pressing dismiss button in notification/activity (not handle click itself!)
open class IncomingCallDismissListener {
    private val dismissListener = mutableListOf<() -> Unit>()

    fun incomingCallWasDismissed(notificationId: Int?) {
        FlashlightUtils.flashlightStop()
        IncomingCallHandler.cancelNotification(notificationId)
        dismissListener.forEach {
            it()
        }
    }

    fun addCallDismissListener(listener: () -> Unit) {
        dismissListener.add(listener)
    }

    fun removeCallDismissListener(listener: () -> Unit) {
        dismissListener.removeAll { it == listener }
    }
}