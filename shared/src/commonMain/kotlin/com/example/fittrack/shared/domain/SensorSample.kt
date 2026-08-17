package com.example.fittrack.shared.domain

data class SensorSample(
    val id: String,
    val kind: String,
    val value: Double,
    val unit: String?,
    val isoTimestamp: String,
    val dateLabel: String,
)

object SensorKinds {
    const val PROXIMIDAD = "proximidad"
    const val LUZ = "luz"
    const val ACELEROMETRO = "acelerometro"
    const val SHAKE = "shake"
}
