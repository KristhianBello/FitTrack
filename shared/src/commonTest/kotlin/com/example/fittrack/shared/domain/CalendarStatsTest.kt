package com.example.fittrack.shared.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class CalendarStatsTest {

    @Test
    fun workoutsByDate_groupsByDatePrefixAndIgnoresIncompleteOrBlank() {
        val workouts = listOf(
            session("2026-08-10T09:00:00", category = RoutineCategory.TREN_SUPERIOR),
            session("2026-08-10T18:00:00", category = RoutineCategory.TREN_INFERIOR),
            session("2026-08-12T09:00:00", category = RoutineCategory.FULL_BODY),
            session("2026-08-13T09:00:00", completed = false),
            session("", category = RoutineCategory.FULL_BODY),
        )

        val byDate = CalendarStats.workoutsByDate(workouts)

        assertEquals(2, byDate["2026-08-10"]?.size)
        assertEquals(1, byDate["2026-08-12"]?.size)
        assertEquals(null, byDate["2026-08-13"])
        assertEquals(2, byDate.size)
    }

    @Test
    fun monthlySummary_sumsMinutesByCategoryWithinMonthOnly() {
        val workouts = listOf(
            session("2026-08-01T09:00:00", durationMinutes = 40, category = RoutineCategory.TREN_SUPERIOR),
            session("2026-08-05T09:00:00", durationMinutes = 50, category = RoutineCategory.TREN_INFERIOR),
            session("2026-08-08T09:00:00", durationMinutes = 30, category = RoutineCategory.FULL_BODY),
            session("2026-07-31T09:00:00", durationMinutes = 999, category = RoutineCategory.TREN_SUPERIOR),
            session("2026-08-09T09:00:00", durationMinutes = 999, completed = false),
        )

        val summary = CalendarStats.monthlySummary(
            workouts = workouts,
            monthPrefix = "2026-08",
            elapsedDaysInMonth = (1..10).map { "2026-08-" + it.toString().padStart(2, '0') },
        )

        assertEquals(120, summary.totalMinutes)
        assertEquals(40, summary.upperBodyMinutes)
        assertEquals(50, summary.lowerBodyMinutes)
    }

    @Test
    fun monthlySummary_restDaysCountsElapsedDaysWithNoWorkout() {
        val workouts = listOf(
            session("2026-08-01T09:00:00", durationMinutes = 40, category = RoutineCategory.TREN_SUPERIOR),
            session("2026-08-03T09:00:00", durationMinutes = 40, category = RoutineCategory.TREN_INFERIOR),
        )

        val summary = CalendarStats.monthlySummary(
            workouts = workouts,
            monthPrefix = "2026-08",
            elapsedDaysInMonth = listOf("2026-08-01", "2026-08-02", "2026-08-03", "2026-08-04", "2026-08-05"),
        )

        assertEquals(3, summary.restDays)
        assertEquals(72, summary.restHours)
    }

    @Test
    fun monthlySummary_futureDaysNeverCountAsRest() {
        // El caller nunca debe incluir días futuros en elapsedDaysInMonth; si el mes
        // recién empezó, la lista de días transcurridos debe ser corta y no todo el mes.
        val summary = CalendarStats.monthlySummary(
            workouts = emptyList(),
            monthPrefix = "2026-08",
            elapsedDaysInMonth = listOf("2026-08-01", "2026-08-02"),
        )

        assertEquals(2, summary.restDays)
        assertEquals(48, summary.restHours)
    }

    private fun session(
        isoTimestamp: String,
        durationMinutes: Int = 30,
        completed: Boolean = true,
        category: String? = null,
    ) = WorkoutSession(
        id = isoTimestamp.ifBlank { "blank" } + category,
        routineId = null,
        routineName = "Rutina",
        durationMinutes = durationMinutes,
        calories = null,
        isoTimestamp = isoTimestamp,
        dateLabel = isoTimestamp,
        completed = completed,
        category = category,
    )
}
