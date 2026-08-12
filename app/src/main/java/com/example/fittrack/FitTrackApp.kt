package com.example.fittrack

import android.app.Application
import com.example.fittrack.shared.FitTrackSdk

class FitTrackApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FitTrackSdk.initialize(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseAnonKey = BuildConfig.SUPABASE_ANON_KEY,
        )
    }
}
