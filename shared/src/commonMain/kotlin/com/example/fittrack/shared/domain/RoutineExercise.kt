package com.example.fittrack.shared.domain

data class RoutineExercise(
    val id: String,
    val routineId: String,
    val exerciseId: String,
    val exerciseName: String,
    val order: Int,
    val sets: Int,
    val reps: Int,
)
