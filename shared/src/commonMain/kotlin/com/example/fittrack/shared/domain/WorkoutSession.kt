package com.example.fittrack.shared.domain

data class WorkoutSession(
    val id: String,
    val routineId: String?,
    val routineName: String,
    val durationMinutes: Int?,
    val calories: Int?,
    val isoTimestamp: String,
    val dateLabel: String,
    val completed: Boolean,
    val category: String? = null,
)
