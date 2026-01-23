package com.example.fittrack

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseConfig {
    
    // TODO: Reemplaza estos valores con tus credenciales de Supabase
    private const val SUPABASE_URL = "https://dpwelxavdsrswunzkfct.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRwd2VseGF2ZHNyc3d1bnprZmN0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjkxNDY2MDQsImV4cCI6MjA4NDcyMjYwNH0.XENUvdTasSEUTfMyGePhADsHjVrFS_cFyPQddqL0L6Q"
    
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
    
    val auth get() = client.pluginManager.getPlugin(Auth)
}
