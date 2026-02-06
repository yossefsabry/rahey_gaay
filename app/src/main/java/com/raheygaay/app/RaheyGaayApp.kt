package com.raheygaay.app

import android.app.Application
import android.util.Log
import com.mapbox.maps.ResourceOptionsManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RaheyGaayApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val token = BuildConfig.MAPBOX_TOKEN
        if (token.isBlank()) {
            Log.w("RaheyGaayApp", "MAPBOX_TOKEN is missing. Add it to .env.")
        } else {
            ResourceOptionsManager.getDefault(this, token)
        }
    }
}
