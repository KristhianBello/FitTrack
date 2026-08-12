package com.example.fittrack.shared

import com.example.fittrack.shared.auth.AuthManager
import com.example.fittrack.shared.config.SupabaseConfig
import com.example.fittrack.shared.data.ProfileRepository
import com.example.fittrack.shared.data.RoutineRepository
import com.example.fittrack.shared.data.WeightRepository
import com.example.fittrack.shared.session.FitTrackSession

object FitTrackSdk {
    val auth: AuthManager = AuthManager()

    private val profiles = ProfileRepository(auth)
    private val weights = WeightRepository(auth)
    private val routines = RoutineRepository(auth)

    val session: FitTrackSession = FitTrackSession(profiles, weights, routines)

    fun initialize(supabaseUrl: String, supabaseAnonKey: String) {
        require(supabaseUrl.isNotBlank()) { "SUPABASE_URL está vacío" }
        require(supabaseAnonKey.isNotBlank()) { "SUPABASE_ANON_KEY está vacío" }
        SupabaseConfig.initialize(supabaseUrl, supabaseAnonKey)
    }

    suspend fun onAuthenticated(displayName: String? = null) {
        session.start(displayName)
    }

    suspend fun signOut() {
        session.clear()
        auth.signOut()
    }
}
