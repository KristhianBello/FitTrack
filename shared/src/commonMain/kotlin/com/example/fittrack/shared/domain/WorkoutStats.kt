package com.example.fittrack.shared.domain

import com.example.fittrack.shared.platform.isoDateDaysAgo

object WorkoutStats {
    fun weeklyGoal(routines: List<Routine>): Int =
        routines.sumOf { it.weeklyFrequency }.coerceAtLeast(0)

    fun completedThisWeek(workouts: List<WorkoutSession>): Int =
        completedSince(workouts, isoDateDaysAgo(6))

    fun completedSince(workouts: List<WorkoutSession>, fromIsoDate: String): Int =
        workouts.count { session ->
            session.completed && session.isoTimestamp.take(10) >= fromIsoDate
        }

    fun percent(completed: Int, goal: Int): Int {
        if (goal <= 0) return 0
        return ((completed * 100) / goal).coerceIn(0, 100)
    }
}
