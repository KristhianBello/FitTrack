package com.example.fittrack.shared.domain

object RoutineCategory {
    const val TREN_SUPERIOR = "tren_superior"
    const val TREN_INFERIOR = "tren_inferior"
    const val FULL_BODY = "full_body"
    const val DESCANSO = "descanso"

    val all = listOf(TREN_SUPERIOR, TREN_INFERIOR, FULL_BODY, DESCANSO)
}
