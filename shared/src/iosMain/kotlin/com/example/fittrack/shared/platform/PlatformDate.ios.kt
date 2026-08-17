package com.example.fittrack.shared.platform

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSUUID

internal actual fun randomUuid(): String = NSUUID().UUIDString()

internal actual fun todayIsoDate(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd"
    formatter.locale = NSLocale(localeIdentifier = "en_US_POSIX")
    return formatter.stringFromDate(NSDate())
}

internal actual fun nowIsoTimestamp(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
    formatter.locale = NSLocale(localeIdentifier = "en_US_POSIX")
    return formatter.stringFromDate(NSDate())
}

internal actual fun isoDateDaysAgo(days: Int): String {
    val calendar = platform.Foundation.NSCalendar.currentCalendar
    val date = calendar.dateByAddingUnit(
        unit = platform.Foundation.NSCalendarUnitDay,
        value = -days.toLong(),
        toDate = NSDate(),
        options = 0u,
    ) ?: NSDate()
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd"
    formatter.locale = NSLocale(localeIdentifier = "en_US_POSIX")
    return formatter.stringFromDate(date)
}

internal actual fun formatIsoDate(isoDate: String): String {
    val parser = NSDateFormatter()
    parser.dateFormat = "yyyy-MM-dd"
    parser.locale = NSLocale(localeIdentifier = "en_US_POSIX")
    val date = parser.dateFromString(isoDate) ?: return isoDate
    val display = NSDateFormatter()
    display.dateFormat = "dd MMM yyyy"
    display.locale = NSLocale(localeIdentifier = "es_ES")
    return display.stringFromDate(date)
}
