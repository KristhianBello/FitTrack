package com.example.fittrack.shared.platform

internal expect fun randomUuid(): String

internal expect fun todayIsoDate(): String

internal expect fun formatIsoDate(isoDate: String): String
