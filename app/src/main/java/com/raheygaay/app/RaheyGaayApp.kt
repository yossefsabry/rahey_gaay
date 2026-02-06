package com.raheygaay.app

import android.app.Application
import android.util.Log
import com.mapbox.maps.MapboxOptions
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RaheyGaayApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val token = BuildConfig.MAPBOX_TOKEN
        if (token.isBlank()) {
            Log.w("RaheyGaayApp", "MAPBOX_TOKEN is missing. Add it to .env.")
        } else {
            MapboxOptions.accessToken = token
        }
    }
}
