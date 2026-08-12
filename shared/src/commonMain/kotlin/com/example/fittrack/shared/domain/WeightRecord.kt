package com.example.fittrack.shared.domain

data class WeightRecord(
    val id: String,
    val isoDate: String,
    val kilograms: Float,
    val dateLabel: String,
)
