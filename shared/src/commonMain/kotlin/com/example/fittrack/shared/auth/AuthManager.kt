package com.example.fittrack.shared.auth

import com.example.fittrack.shared.config.SupabaseConfig
import com.example.fittrack.shared.logDebug
import com.example.fittrack.shared.logError
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthManager {

    suspend fun signUp(email: String, password: String, name: String): Result<String> {
        return try {
            val result = SupabaseConfig.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("name", name)
                }
            }
            logDebug(TAG, "Usuario registrado: ${result?.id}")
            Result.success("Usuario registrado exitosamente")
        } catch (e: Exception) {
            logError(TAG, "Error al registrar usuario", e)
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<String> {
        return try {
            SupabaseConfig.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val currentUser = SupabaseConfig.auth.currentUserOrNull()
            logDebug(TAG, "Inicio de sesión exitoso: ${currentUser?.id}")
            Result.success("Inicio de sesión exitoso")
        } catch (e: Exception) {
            logError(TAG, "Error al iniciar sesión", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            SupabaseConfig.auth.signInWith(IDToken) {
                this.idToken = idToken
                provider = Google
            }
            val currentUser = SupabaseConfig.auth.currentUserOrNull()
            logDebug(TAG, "Inicio de sesión con Google: ${currentUser?.id}")
            Result.success("Inicio de sesión con Google exitoso")
        } catch (e: Exception) {
            logError(TAG, "Error al iniciar sesión con Google", e)
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<String> {
        return try {
            SupabaseConfig.auth.signOut()
            logDebug(TAG, "Sesión cerrada")
            Result.success("Sesión cerrada exitosamente")
        } catch (e: Exception) {
            logError(TAG, "Error al cerrar sesión", e)
            Result.failure(e)
        }
    }

    fun getCurrentUser(): UserInfo? = SupabaseConfig.auth.currentUserOrNull()

    fun isUserLoggedIn(): Boolean = getCurrentUser() != null

    fun getCurrentUserEmail(): String? = getCurrentUser()?.email

    suspend fun resetPassword(email: String): Result<String> {
        return try {
            SupabaseConfig.auth.resetPasswordForEmail(email)
            logDebug(TAG, "Email de restablecimiento enviado")
            Result.success("Email de restablecimiento enviado")
        } catch (e: Exception) {
            logError(TAG, "Error al enviar email de restablecimiento", e)
            Result.failure(e)
        }
    }

    private companion object {
        const val TAG = "AuthManager"
    }
}
