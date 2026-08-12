package com.example.fittrack.shared.domain

data class UserProfile(
    val id: String,
    val name: String,
    val email: String?,
    val goalKg: Float? = 80f,
    val totalWorkouts: Int = 0,
    val streakRecord: Int = 0,
    val activeHoursLabel: String = "0 hrs",
)
