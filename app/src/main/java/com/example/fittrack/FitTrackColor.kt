package com.example.fittrack

import android.graphics.Color

/**
 * Paleta de colores de FitTrack como constantes de Kotlin, para código que necesita
 * un Int de color (setBackgroundColor, setTextColor, Canvas, etc.) sin depender de un
 * Context. Debe mantenerse en sync con res/values/colors.xml.
 */
object FitTrackColor {
    // Paleta base (Material default)
    val purple200 = Color.parseColor("#FFBB86FC")
    val purple500 = Color.parseColor("#FF6200EE")
    val purple700 = Color.parseColor("#FF3700B3")
    val teal200 = Color.parseColor("#FF03DAC5")
    val teal700 = Color.parseColor("#FF018786")
    val black = Color.parseColor("#FF000000")
    val white = Color.parseColor("#FFFFFFFF")

    // Azul Oscuro/Navy - "Track Night" - Principal/Base
    val trackNight = Color.parseColor("#0C2B5B")
    val primary = trackNight

    // Cian/Turquesa Brillante - "Fit Glow" - Acento/Primario
    val fitGlow = Color.parseColor("#00C2E0")
    val accentPrimary = fitGlow

    // Verde Neón/Lima - "Progress Neon" - Acento/Secundario
    val progressNeon = Color.parseColor("#A7F000")
    val accentSecondary = progressNeon

    // Blanco - "Clean Stat" - Texto/Fondo Secundario
    val cleanStat = Color.parseColor("#FFFFFFFF")

    // Gris Claro - "Soft Metric" - Soporte/Contraste Suave
    val softMetric = Color.parseColor("#C0C0C0")

    // Alias de compatibilidad
    val lavender = fitGlow

    // Colores específicos para navegación
    val bottomNavIconActive = trackNight
    val bottomNavIconInactive = fitGlow
    val bottomNavTextActive = cleanStat
    val bottomNavTextInactive = Color.parseColor("#B3FFFFFF")
    val bottomNavIndicator = cleanStat

    // Colores para temas
    val appBackground = Color.parseColor("#C0C0C0")
    val appSurface = Color.parseColor("#C0C0C0")
    val appSurfaceVariant = Color.parseColor("#FFFFFFFF")

    // Colores para Login/Register
    val backgroundDark = Color.parseColor("#1A1A2E")
    val cardBackground = Color.parseColor("#16213E")
    val textSecondary = Color.parseColor("#94A3B8")

    // Colores para alerta de sacudida (Sensores)
    val shakeAlertBackground = Color.parseColor("#FFFF0000")
    val shakeIdleBodyText = Color.parseColor("#FF333333")
}
