package com.example.fittrack.shared.domain

data class Routine(
    val name: String,
    val durationMinutes: Int,
    val exerciseCount: Int,
    val weeklyFrequency: Int,
)
