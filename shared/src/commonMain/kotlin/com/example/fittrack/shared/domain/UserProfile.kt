package com.example.fittrack.shared.domain

data class UserProfile(
    val id: String,
    val name: String,
    val email: String?,
    val goalKg: Float? = 80f,
    val heightCm: Float? = null,
    val birthDate: String? = null,
    val gender: String? = null,
    val totalWorkouts: Int = 0,
    val streakRecord: Int = 0,
    val activeHoursLabel: String = "0 hrs",
) {
    val hasPersonalData: Boolean
        get() = heightCm != null && goalKg != null && birthDate != null && gender != null
}
