package com.example.fittrack.shared.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FitnessValidatorTest {

    @Test
    fun weightError_rejectsOutOfRange() {
        assertEquals(
            "Por favor ingresa un peso válido (1-300 kg)",
            FitnessValidator.weightError(0f),
        )
        assertNull(FitnessValidator.weightError(80.5f))
    }

    @Test
    fun routineFields_validateRanges() {
        assertEquals(
            "Por favor ingresa el nombre de la rutina",
            FitnessValidator.routineNameError("  "),
        )
        assertNull(FitnessValidator.durationError(45))
        assertEquals(
            "La frecuencia debe estar entre 1 y 7 veces por semana",
            FitnessValidator.weeklyFrequencyError(8),
        )
    }
}
