package com.example.fittrack.shared.domain

data class UserProfile(
    val name: String,
    val email: String?,
    val totalWorkouts: Int = 0,
    val streakRecord: Int = 0,
    val activeHoursLabel: String = "0 hrs",
)
