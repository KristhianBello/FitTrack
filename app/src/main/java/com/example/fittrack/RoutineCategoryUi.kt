package com.example.fittrack

import com.example.fittrack.shared.domain.RoutineCategory

object RoutineCategoryUi {
    fun labelRes(category: String?): Int = when (category) {
        RoutineCategory.TREN_SUPERIOR -> R.string.category_tren_superior
        RoutineCategory.TREN_INFERIOR -> R.string.category_tren_inferior
        RoutineCategory.DESCANSO -> R.string.category_descanso
        else -> R.string.category_full_body
    }

    fun color(category: String?): Int = when (category) {
        RoutineCategory.TREN_SUPERIOR -> FitTrackColor.fitGlow
        RoutineCategory.TREN_INFERIOR -> FitTrackColor.progressNeon
        RoutineCategory.DESCANSO -> FitTrackColor.softMetric
        else -> FitTrackColor.trackNight
    }
}
