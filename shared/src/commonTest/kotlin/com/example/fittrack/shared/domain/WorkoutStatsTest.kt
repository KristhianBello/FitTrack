package com.example.fittrack.shared.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class WorkoutStatsTest {

    @Test
    fun weeklyGoal_sumsFrequencies() {
        val routines = listOf(
            Routine("1", "A", 30, 6, 3),
            Routine("2", "B", 40, 7, 2),
        )
        assertEquals(5, WorkoutStats.weeklyGoal(routines))
    }

    @Test
    fun completedSince_countsByDate() {
        val workouts = listOf(
            session("2026-08-12T10:00:00Z"),
            session("2026-08-04T10:00:00Z"),
            session("2026-08-10T10:00:00Z", completed = false),
        )
        assertEquals(1, WorkoutStats.completedSince(workouts, "2026-08-05"))
    }

    @Test
    fun percent_capsAt100() {
        assertEquals(0, WorkoutStats.percent(1, 0))
        assertEquals(50, WorkoutStats.percent(2, 4))
        assertEquals(100, WorkoutStats.percent(9, 4))
    }

    private fun session(iso: String, completed: Boolean = true) = WorkoutSession(
        id = iso,
        routineId = null,
        routineName = "A",
        durationMinutes = 30,
        calories = null,
        isoTimestamp = iso,
        dateLabel = iso,
        completed = completed,
    )
}
