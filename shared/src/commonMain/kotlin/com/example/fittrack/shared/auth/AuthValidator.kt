package com.example.fittrack.shared.auth

object AuthValidator {
    private val emailRegex = Regex(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
    )

    fun nameError(name: String): String? = when {
        name.isBlank() -> "El nombre es requerido"
        name.trim().length < 2 -> "El nombre debe tener al menos 2 caracteres"
        else -> null
    }

    fun emailError(email: String): String? = when {
        email.isBlank() -> "El email es requerido"
        !emailRegex.matches(email.trim()) -> "Email inválido"
        else -> null
    }

    fun passwordError(password: String, requireStrong: Boolean = false): String? = when {
        password.isEmpty() -> "La contraseña es requerida"
        password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
        requireStrong && !password.any { it.isUpperCase() } ->
            "Debe contener al menos una mayúscula"
        requireStrong && !password.any { it.isDigit() } ->
            "Debe contener al menos un número"
        else -> null
    }

    fun confirmPasswordError(password: String, confirmPassword: String): String? = when {
        confirmPassword.isEmpty() -> "Confirma tu contraseña"
        password != confirmPassword -> "Las contraseñas no coinciden"
        else -> null
    }
}
