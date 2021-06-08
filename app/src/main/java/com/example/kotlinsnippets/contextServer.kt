package com.example.kotlinsnippets

import android.app.Application
import android.content.Context

class contextServer : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}