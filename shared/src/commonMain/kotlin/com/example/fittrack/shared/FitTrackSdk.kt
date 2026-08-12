package com.example.fittrack.shared

import com.example.fittrack.shared.auth.AuthManager
import com.example.fittrack.shared.config.SupabaseConfig

object FitTrackSdk {
    val auth: AuthManager = AuthManager()

    fun initialize(supabaseUrl: String, supabaseAnonKey: String) {
        require(supabaseUrl.isNotBlank()) { "SUPABASE_URL está vacío" }
        require(supabaseAnonKey.isNotBlank()) { "SUPABASE_ANON_KEY está vacío" }
        SupabaseConfig.initialize(supabaseUrl, supabaseAnonKey)
    }
}
