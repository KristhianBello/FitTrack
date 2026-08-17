package com.example.fittrack.shared.data.schema

/**
 * Nombres de tabla del esquema KMP en supabase/migrations.
 * Deben coincidir con public.* en 20260812174000_fittrack_kmp.sql
 */
object DbTables {
    const val PROFILES = "profiles"
    const val EXERCISES = "exercises"
    const val ROUTINES = "routines"
    const val ROUTINE_EXERCISES = "routine_exercises"
    const val WEIGHT_RECORDS = "weight_records"
    const val WORKOUT_HISTORY = "workout_history"
}
