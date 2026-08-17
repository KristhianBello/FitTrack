package com.example.fittrack.shared.domain

data class Exercise(
    val id: String,
    val name: String,
    val description: String?,
    val muscleGroup: String?,
    val level: String?,
    val type: String?,
)
