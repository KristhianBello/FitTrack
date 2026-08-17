package com.example.fittrack.shared.platform

internal expect fun randomUuid(): String

internal expect fun todayIsoDate(): String

internal expect fun nowIsoTimestamp(): String

internal expect fun isoDateDaysAgo(days: Int): String

internal expect fun formatIsoDate(isoDate: String): String

internal fun formatIsoTimestamp(isoTimestamp: String): String {
    val datePart = isoTimestamp.take(10)
    val timePart = isoTimestamp.drop(11).take(5)
    val dateLabel = formatIsoDate(datePart)
    return if (timePart.length == 5) "$dateLabel · $timePart" else dateLabel
}

