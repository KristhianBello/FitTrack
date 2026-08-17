package com.example.fittrack.shared.config

import com.example.fittrack.shared.logDebug
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseConfig {
    private const val TAG = "SupabaseConfig"

    @Volatile
    private var initializedUrl: String? = null

    val client: SupabaseClient
        get() = requireClient()

    val auth: Auth
        get() = client.pluginManager.getPlugin(Auth)

    fun initialize(supabaseUrl: String, supabaseAnonKey: String) {
        if (initializedUrl == supabaseUrl && holder != null) {
            return
        }
        holder = createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseAnonKey,
        ) {
            install(Auth)
            install(Postgrest)
        }
        initializedUrl = supabaseUrl
        logDebug(TAG, "Cliente Supabase inicializado")
    }

    fun isInitialized(): Boolean = holder != null

    private var holder: SupabaseClient? = null

    private fun requireClient(): SupabaseClient {
        return holder
            ?: error("FitTrackSdk.initialize() debe llamarse antes de usar Supabase")
    }
}
