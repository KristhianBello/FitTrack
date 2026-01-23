package com.example.fittrack

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.widget.TextView
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnGoogleLogin: MaterialButton
    private lateinit var tvRegister: TextView
    private lateinit var tvForgotPassword: TextView
    private val authManager = AuthManager()
    private lateinit var googleSignInHelper: GoogleSignInHelper

    // Launcher para Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleGoogleSignInResult(result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Google Sign-In Helper
        googleSignInHelper = GoogleSignInHelper(this)

        // Inicializar vistas
        initViews()

        // Configurar listeners
        setupListeners()
    }

    private fun initViews() {
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin)
        tvRegister = findViewById(R.id.tvRegister)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
    }

    private fun setupListeners() {
        // Botón de login
        btnLogin.setOnClickListener {
            if (validateInputs()) {
                performLogin()
            }
        }

        // Botón de Google Login
        btnGoogleLogin.setOnClickListener {
            startGoogleSignIn()
        }

        // Ir a registro
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Olvidé mi contraseña
        tvForgotPassword.setOnClickListener {
            Toast.makeText(
                this,
                "Funcionalidad de recuperación de contraseña próximamente",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Limpiar errores al escribir
        etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilEmail.error = null
        }

        etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilPassword.error = null
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validar email
        val email = etEmail.text.toString().trim()
        if (email.isEmpty()) {
            tilEmail.error = "El email es requerido"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Email inválido"
            isValid = false
        } else {
            tilEmail.error = null
        }

        // Validar contraseña
        val password = etPassword.text.toString()
        if (password.isEmpty()) {
            tilPassword.error = "La contraseña es requerida"
            isValid = false
        } else if (password.length < 6) {
            tilPassword.error = "La contraseña debe tener al menos 6 caracteres"
            isValid = false
        } else {
            tilPassword.error = null
        }

        return isValid
    }

    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        // Deshabilitar botón mientras se procesa
        btnLogin.isEnabled = false
        btnLogin.text = "Iniciando sesión..."

        lifecycleScope.launch {
            val result = authManager.signIn(email, password)
            
            btnLogin.isEnabled = true
            btnLogin.text = getString(R.string.login)
            
            result.onSuccess {
                Toast.makeText(
                    this@LoginActivity,
                    "¡Bienvenido!",
                    Toast.LENGTH_SHORT
                ).show()

                // Navegar a MainActivity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }.onFailure { error ->
                val errorMessage = when {
                    error.message?.contains("Invalid login credentials") == true ->
                        "Credenciales inválidas. Verifica tu email y contraseña."
                    error.message?.contains("Email not confirmed") == true ->
                        "Por favor, confirma tu email antes de iniciar sesión."
                    error.message?.contains("network", ignoreCase = true) == true ->
                        "Error de conexión. Verifica tu internet."
                    else -> "Error al iniciar sesión: ${error.message}"
                }
                
                Toast.makeText(
                    this@LoginActivity,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Inicia el flujo de Google Sign-In
     */
    private fun startGoogleSignIn() {
        android.util.Log.d("LoginActivity", "=== INICIANDO GOOGLE SIGN-IN ===")
        android.util.Log.d("LoginActivity", "Deshabilitando botón...")
        btnGoogleLogin.isEnabled = false

        try {
            val signInIntent = googleSignInHelper.getSignInIntent()
            android.util.Log.d("LoginActivity", "Intent obtenido, lanzando...")
            googleSignInLauncher.launch(signInIntent)
            android.util.Log.d("LoginActivity", "Launcher ejecutado")
        } catch (e: Exception) {
            android.util.Log.e("LoginActivity", "ERROR al lanzar Google Sign-In", e)
            btnGoogleLogin.isEnabled = true
            Toast.makeText(
                this,
                "Error al iniciar Google Sign-In: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Maneja el resultado de Google Sign-In
     */
    private fun handleGoogleSignInResult(data: Intent?) {
        android.util.Log.d("LoginActivity", "=== RESULTADO DE GOOGLE SIGN-IN ===")
        android.util.Log.d("LoginActivity", "Data es null: ${data == null}")

        val account = googleSignInHelper.handleSignInResult(data)

        if (account != null) {
            android.util.Log.d("LoginActivity", "✅ Cuenta obtenida: ${account.email}")
            android.util.Log.d("LoginActivity", "Nombre: ${account.displayName}")

            val idToken = googleSignInHelper.getIdToken(account)
            android.util.Log.d("LoginActivity", "ID Token presente: ${idToken != null}")

            if (idToken != null) {
                android.util.Log.d("LoginActivity", "Autenticando con Supabase...")
                // Autenticar con Supabase usando el ID Token
                performGoogleSignIn(idToken)
            } else {
                // ID Token es null = problema de configuración
                btnGoogleLogin.isEnabled = true
                android.util.Log.e("LoginActivity", "❌ ID Token NULL - Problema de configuración")
                android.util.Log.e("LoginActivity", "SHA-1 no configurado en Google Cloud Console")

                Toast.makeText(
                    this,
                    "⚠️ Configuración incompleta\n\n" +
                    "Google detectó: ${account.email}\n" +
                    "Pero falta SHA-1 en Google Cloud Console\n\n" +
                    "MODO PRUEBA: Entrando sin Supabase...",
                    Toast.LENGTH_LONG
                ).show()

                // MODO PRUEBA: Navegar sin Supabase
                android.util.Log.d("LoginActivity", "MODO PRUEBA: Sin autenticación Supabase")
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        } else {
            btnGoogleLogin.isEnabled = true
            android.util.Log.e("LoginActivity", "❌ Account NULL - Sign-In falló completamente")
            Toast.makeText(
                this,
                "Inicio de sesión con Google cancelado.\n" +
                "Revisa Logcat para ver el error específico.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Autentica con Supabase usando el ID Token de Google
     */
    private fun performGoogleSignIn(idToken: String) {
        lifecycleScope.launch {
            val result = authManager.signInWithGoogle(idToken)

            btnGoogleLogin.isEnabled = true

            result.onSuccess {
                Toast.makeText(
                    this@LoginActivity,
                    "¡Bienvenido!",
                    Toast.LENGTH_SHORT
                ).show()

                // Navegar a MainActivity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }.onFailure { error ->
                Toast.makeText(
                    this@LoginActivity,
                    "Error al iniciar sesión con Google: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
