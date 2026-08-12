package com.example.fittrack.shared.domain

object FitnessValidator {
    fun weightError(kilograms: Float): String? = when {
        kilograms <= 0f || kilograms > 300f -> "Por favor ingresa un peso válido (1-300 kg)"
        else -> null
    }

    fun routineNameError(name: String): String? =
        if (name.isBlank()) "Por favor ingresa el nombre de la rutina" else null

    fun durationError(minutes: Int): String? =
        if (minutes in 1..300) null else "La duración debe estar entre 1 y 300 minutos"

    fun exerciseCountError(count: Int): String? =
        if (count in 1..50) null else "La cantidad de ejercicios debe estar entre 1 y 50"

    fun weeklyFrequencyError(times: Int): String? =
        if (times in 1..7) null else "La frecuencia debe estar entre 1 y 7 veces por semana"
}
