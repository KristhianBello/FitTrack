package com.example.fittrack.shared.domain

data class MonthlySummary(
    val totalMinutes: Int,
    val upperBodyMinutes: Int,
    val lowerBodyMinutes: Int,
    /** Días del mes visible (hasta hoy) sin ningún entrenamiento completado. */
    val restDays: Int,
    /** restDays * 24, tal como lo pidió el usuario para el resumen mensual. */
    val restHours: Int,
)

object CalendarStats {

    fun workoutsByDate(workouts: List<WorkoutSession>): Map<String, List<WorkoutSession>> =
        workouts
            .filter { it.completed && it.isoTimestamp.length >= 10 }
            .groupBy { it.isoTimestamp.take(10) }

    /**
     * @param monthPrefix "yyyy-MM" del mes visible.
     * @param elapsedDaysInMonth fechas ISO ("yyyy-MM-dd") del mes visible que ya ocurrieron
     *   (hasta hoy inclusive); días futuros del mes NO deben incluirse acá, así nunca cuentan
     *   como "día de descanso".
     */
    fun monthlySummary(
        workouts: List<WorkoutSession>,
        monthPrefix: String,
        elapsedDaysInMonth: List<String>,
    ): MonthlySummary {
        val completedInMonth = workouts.filter {
            it.completed && it.isoTimestamp.take(7) == monthPrefix
        }

        val totalMinutes = completedInMonth.sumOf { it.durationMinutes ?: 0 }
        val upperBodyMinutes = completedInMonth
            .filter { it.category == RoutineCategory.TREN_SUPERIOR }
            .sumOf { it.durationMinutes ?: 0 }
        val lowerBodyMinutes = completedInMonth
            .filter { it.category == RoutineCategory.TREN_INFERIOR }
            .sumOf { it.durationMinutes ?: 0 }

        val trainedDates = completedInMonth.map { it.isoTimestamp.take(10) }.toSet()
        val restDays = elapsedDaysInMonth.count { it !in trainedDates }

        return MonthlySummary(
            totalMinutes = totalMinutes,
            upperBodyMinutes = upperBodyMinutes,
            lowerBodyMinutes = lowerBodyMinutes,
            restDays = restDays,
            restHours = restDays * 24,
        )
    }
}
