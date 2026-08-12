package com.example.fittrack.shared

internal expect fun logDebug(tag: String, message: String)

internal expect fun logError(tag: String, message: String, throwable: Throwable? = null)
