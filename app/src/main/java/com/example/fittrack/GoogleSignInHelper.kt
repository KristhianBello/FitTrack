package com.example.fittrack

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

/**
 * Helper para gestionar Google Sign-In
 */
class GoogleSignInHelper(private val context: Context) {

    companion object {
        private const val TAG = "GoogleSignInHelper"
        // Web Client ID de Google Cloud Console / Firebase
        private const val WEB_CLIENT_ID = "432759824913-7o7tbpe5roo6hsni5senrq4pr2iknf1s.apps.googleusercontent.com"
    }

    private val googleSignInClient: GoogleSignInClient

    init {
        // Verificar si WEB_CLIENT_ID está configurado
        if (WEB_CLIENT_ID == "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com") {
            Log.e(TAG, "⚠️ ERROR: WEB_CLIENT_ID NO ESTÁ CONFIGURADO!")
            Log.e(TAG, "Necesitas reemplazarlo con tu ID real de Firebase")
            Log.e(TAG, "Instrucciones: Ver SOLUCION_GOOGLE_SIGNIN.md")
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
        Log.d(TAG, "GoogleSignInClient inicializado")
    }

    /**
     * Obtiene el intent para iniciar el flujo de Google Sign-In
     */
    fun getSignInIntent(): Intent {
        Log.d(TAG, "Obteniendo signInIntent...")
        Log.d(TAG, "WEB_CLIENT_ID configurado: ${WEB_CLIENT_ID.take(20)}...")
        val intent = googleSignInClient.signInIntent
        Log.d(TAG, "SignInIntent obtenido correctamente")
        return intent
    }

    /**
     * Procesa el resultado de Google Sign-In
     * @return GoogleSignInAccount si fue exitoso, null si falló
     */
    fun handleSignInResult(data: Intent?): GoogleSignInAccount? {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

            Log.d(TAG, "✅ Google Sign-In exitoso!")
            Log.d(TAG, "Email: ${account?.email}")
            Log.d(TAG, "Nombre: ${account?.displayName}")
            Log.d(TAG, "ID Token presente: ${account?.idToken != null}")

            if (account?.idToken == null) {
                Log.e(TAG, "⚠️ ADVERTENCIA: ID Token es NULL")
                Log.e(TAG, "Esto significa que WEB_CLIENT_ID no está configurado correctamente")
                Log.e(TAG, "Ver archivo: SOLUCION_GOOGLE_SIGNIN.md")
            }

            account
        } catch (e: ApiException) {
            Log.e(TAG, "❌ Error en Google Sign-In")
            Log.e(TAG, "Código de error: ${e.statusCode}")
            Log.e(TAG, "Mensaje: ${e.message}")

            // Explicar códigos de error comunes
            when (e.statusCode) {
                10 -> {
                    Log.e(TAG, "ERROR 10 (Developer Error):")
                    Log.e(TAG, "  • SHA-1 no configurado en Firebase")
                    Log.e(TAG, "  • WEB_CLIENT_ID incorrecto")
                    Log.e(TAG, "  • google-services.json faltante o incorrecto")
                    Log.e(TAG, "Solución: Ver SOLUCION_GOOGLE_SIGNIN.md")
                }
                12501 -> {
                    Log.e(TAG, "ERROR 12501 (Sign-In cancelado por usuario)")
                    Log.e(TAG, "  • Usuario cerró el diálogo")
                    Log.e(TAG, "  • O el popup no se mostró correctamente")
                }
                7 -> {
                    Log.e(TAG, "ERROR 7 (Network Error):")
                    Log.e(TAG, "  • Sin conexión a Internet")
                    Log.e(TAG, "  • Firewall bloqueando Google Services")
                }
                12500 -> {
                    Log.e(TAG, "ERROR 12500 (Sign-In falló):")
                    Log.e(TAG, "  • google-services.json no está en app/")
                    Log.e(TAG, "  • Configuración incorrecta de Firebase")
                }
                else -> {
                    Log.e(TAG, "Error desconocido. Busca el código ${e.statusCode} en:")
                    Log.e(TAG, "https://developers.google.com/android/reference/com/google/android/gms/common/api/CommonStatusCodes")
                }
            }

            null
        }
    }

    /**
     * Obtiene el ID Token de la cuenta de Google
     */
    fun getIdToken(account: GoogleSignInAccount): String? {
        val token = account.idToken
        if (token == null) {
            Log.e(TAG, "❌ ID Token es NULL")
            Log.e(TAG, "WEB_CLIENT_ID actual: $WEB_CLIENT_ID")
            Log.e(TAG, "Debe ser un ID real de Firebase, no el placeholder")
        } else {
            Log.d(TAG, "✅ ID Token obtenido correctamente")
        }
        return token
    }

    /**
     * Cierra sesión de Google
     */
    fun signOut(onComplete: () -> Unit) {
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d(TAG, "Google Sign-Out exitoso")
            onComplete()
        }
    }

    /**
     * Revoca el acceso de Google
     */
    fun revokeAccess(onComplete: () -> Unit) {
        googleSignInClient.revokeAccess().addOnCompleteListener {
            Log.d(TAG, "Google Access revocado")
            onComplete()
        }
    }

    /**
     * Verifica si hay una sesión de Google activa
     */
    fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }
}
