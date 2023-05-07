package com.example.callnotificationscreen.domain

// handle behavior after pressing dismiss button in notification/activity (not handle click itself!)
open class IncomingCallDismissListener {
    private val dismissListener = mutableListOf<() -> Unit>()

    fun incomingCallWasDismissed(notificationId: Int?) {
        IncomingCallHandler.flashlightStop()
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