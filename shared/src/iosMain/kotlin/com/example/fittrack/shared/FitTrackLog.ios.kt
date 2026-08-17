package com.example.fittrack.shared

internal actual fun logDebug(tag: String, message: String) {
    println("D/$tag: $message")
}

internal actual fun logError(tag: String, message: String, throwable: Throwable?) {
    println("E/$tag: $message")
    throwable?.printStackTrace()
}
