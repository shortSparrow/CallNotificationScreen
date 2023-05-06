package com.example.callnotificationscreen

import android.app.Application
import android.content.Context


class CallNotificationApp : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: Application? = null

        fun getContext(): Context {
            return instance!!
        }

        const val TAG = "CallNotificationApp"
    }
}