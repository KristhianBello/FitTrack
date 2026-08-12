package com.example.fittrack.shared.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AuthValidatorTest {

    @Test
    fun emailError_rejectsBlankAndInvalid() {
        assertEquals("El email es requerido", AuthValidator.emailError(""))
        assertEquals("Email inválido", AuthValidator.emailError("hola"))
        assertNull(AuthValidator.emailError("user@fittrack.com"))
    }

    @Test
    fun passwordError_requiresLengthAndOptionalStrength() {
        assertEquals("La contraseña es requerida", AuthValidator.passwordError(""))
        assertEquals(
            "La contraseña debe tener al menos 6 caracteres",
            AuthValidator.passwordError("abc"),
        )
        assertNull(AuthValidator.passwordError("secret1"))
        assertEquals(
            "Debe contener al menos una mayúscula",
            AuthValidator.passwordError("secret1", requireStrong = true),
        )
        assertNull(AuthValidator.passwordError("Secret1", requireStrong = true))
    }

    @Test
    fun confirmPasswordError_detectsMismatch() {
        assertEquals("Las contraseñas no coinciden", AuthValidator.confirmPasswordError("a", "b"))
        assertNull(AuthValidator.confirmPasswordError("Secret1", "Secret1"))
    }
}
