package com.example.fittrack

import android.util.Log
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Clase para gestionar la autenticación con Supabase
 */
class AuthManager {

    companion object {
        private const val TAG = "AuthManager"
    }

    /**
     * Registra un nuevo usuario en Supabase
     */
    suspend fun signUp(email: String, password: String, name: String): Result<String> {
        return try {
            val result = SupabaseConfig.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("name", name)
                }
            }
            
            Log.d(TAG, "Usuario registrado exitosamente: ${result?.id}")
            Result.success("Usuario registrado exitosamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al registrar usuario", e)
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión con email y contraseña
     */
    suspend fun signIn(email: String, password: String): Result<String> {
        return try {
            SupabaseConfig.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            
            val currentUser = SupabaseConfig.auth.currentUserOrNull()
            Log.d(TAG, "Inicio de sesión exitoso: ${currentUser?.id}")
            Result.success("Inicio de sesión exitoso")
        } catch (e: Exception) {
            Log.e(TAG, "Error al iniciar sesión", e)
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión con Google usando el ID Token
     */
    suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            SupabaseConfig.auth.signInWith(IDToken) {
                this.idToken = idToken
                provider = Google
            }

            val currentUser = SupabaseConfig.auth.currentUserOrNull()
            Log.d(TAG, "Inicio de sesión con Google exitoso: ${currentUser?.id}")
            Result.success("Inicio de sesión con Google exitoso")
        } catch (e: Exception) {
            Log.e(TAG, "Error al iniciar sesión con Google", e)
            Result.failure(e)
        }
    }

    /**
     * Cierra la sesión del usuario actual
     */
    suspend fun signOut(): Result<String> {
        return try {
            SupabaseConfig.auth.signOut()
            Log.d(TAG, "Sesión cerrada exitosamente")
            Result.success("Sesión cerrada exitosamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al cerrar sesión", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene el usuario actual
     */
    fun getCurrentUser() = SupabaseConfig.auth.currentUserOrNull()

    /**
     * Verifica si hay un usuario con sesión activa
     */
    fun isUserLoggedIn(): Boolean = SupabaseConfig.auth.currentUserOrNull() != null

    /**
     * Obtiene el email del usuario actual
     */
    fun getCurrentUserEmail(): String? = getCurrentUser()?.email

    /**
     * Envía un email para restablecer contraseña
     */
    suspend fun resetPassword(email: String): Result<String> {
        return try {
            SupabaseConfig.auth.resetPasswordForEmail(email)
            Log.d(TAG, "Email de restablecimiento enviado")
            Result.success("Email de restablecimiento enviado")
        } catch (e: Exception) {
            Log.e(TAG, "Error al enviar email de restablecimiento", e)
            Result.failure(e)
        }
    }
}
