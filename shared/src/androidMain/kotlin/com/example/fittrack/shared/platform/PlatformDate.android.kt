package com.example.fittrack.shared.platform

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

internal actual fun randomUuid(): String = UUID.randomUUID().toString()

internal actual fun todayIsoDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}

internal actual fun nowIsoTimestamp(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    return formatter.format(Date())
}

internal actual fun isoDateDaysAgo(days: Int): String {
    val calendar = java.util.Calendar.getInstance()
    calendar.add(java.util.Calendar.DAY_OF_YEAR, -days)
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
}

internal actual fun formatIsoDate(isoDate: String): String {
    return try {
        val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(isoDate) ?: return isoDate
        SimpleDateFormat("dd MMM yyyy", Locale("es", "ES")).format(parsed)
    } catch (_: Exception) {
        isoDate
    }
}
