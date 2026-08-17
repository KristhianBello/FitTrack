package com.example.fittrack.shared.domain

import com.example.fittrack.shared.platform.isoDateDaysAgo
import com.example.fittrack.shared.platform.todayIsoDate

object FitnessValidator {
    val genderOptions = listOf("masculino", "femenino", "otro", "prefiero_no_decir")

    fun weightError(kilograms: Float): String? = when {
        kilograms <= 0f || kilograms > 300f -> "Por favor ingresa un peso válido (1-300 kg)"
        else -> null
    }

    fun heightError(centimeters: Float): String? = when {
        centimeters < 50f || centimeters > 250f -> "Por favor ingresa una estatura válida (50-250 cm)"
        else -> null
    }

    fun birthDateError(isoDate: String): String? {
        val today = todayIsoDate()
        val minDate = isoDateDaysAgo(36500) // ~100 años
        val maxDate = isoDateDaysAgo(3650) // ~10 años
        return when {
            isoDate.isBlank() -> "Selecciona tu fecha de nacimiento"
            isoDate > today -> "La fecha de nacimiento no puede ser futura"
            isoDate > maxDate -> "Debes tener al menos 10 años"
            isoDate < minDate -> "Ingresa una fecha de nacimiento válida"
            else -> null
        }
    }

    fun genderError(value: String): String? =
        if (value in genderOptions) null else "Selecciona una opción válida"

    fun routineNameError(name: String): String? =
        if (name.isBlank()) "Por favor ingresa el nombre de la rutina" else null

    fun durationError(minutes: Int): String? =
        if (minutes in 1..300) null else "La duración debe estar entre 1 y 300 minutos"

    fun exerciseCountError(count: Int): String? =
        if (count in 1..50) null else "Elige entre 1 y 50 ejercicios para la rutina"

    fun weeklyFrequencyError(times: Int): String? =
        if (times in 1..7) null else "La frecuencia debe estar entre 1 y 7 veces por semana"

    fun categoryError(value: String): String? =
        if (value in RoutineCategory.all) null else "Selecciona una categoría válida"
}
