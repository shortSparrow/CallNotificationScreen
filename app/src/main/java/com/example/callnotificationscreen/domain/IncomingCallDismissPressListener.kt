package com.example.callnotificationscreen.domain

// handle behavior after pressing dismiss button in notification/activity (not handle click itself!)
open class IncomingCallDismissPressListener {
    private val dismissListener = mutableListOf<() -> Unit>()

    fun notifyDismissWasPressed(notificationId: Int?) {
        IncomingCallHandler.cancelNotification(notificationId)
        dismissListener.forEach {
            it()
        }
    }

    fun addDismissPressListener(listener: () -> Unit) {
        dismissListener.add(listener)
    }

    fun removeDismissPressListener(listener: () -> Unit) {
        dismissListener.removeAll { it == listener }
    }
}