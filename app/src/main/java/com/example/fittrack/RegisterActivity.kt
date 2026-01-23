package com.example.fittrack

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tilName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var cbTerms: CheckBox
    private lateinit var btnRegister: MaterialButton
    private lateinit var tvLogin: TextView
    private val authManager = AuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar vistas
        initViews()

        // Configurar listeners
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tilName = findViewById(R.id.tilName)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        cbTerms = findViewById(R.id.cbTerms)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
    }

    private fun setupListeners() {
        // Botón de retroceso
        btnBack.setOnClickListener {
            finish()
        }

        // Botón de registro
        btnRegister.setOnClickListener {
            if (validateInputs()) {
                performRegister()
            }
        }

        // Ir a login
        tvLogin.setOnClickListener {
            finish()
        }

        // Limpiar errores al escribir
        etName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilName.error = null
        }

        etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilEmail.error = null
        }

        etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilPassword.error = null
        }

        etConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilConfirmPassword.error = null
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validar nombre
        val name = etName.text.toString().trim()
        if (name.isEmpty()) {
            tilName.error = "El nombre es requerido"
            isValid = false
        } else if (name.length < 2) {
            tilName.error = "El nombre debe tener al menos 2 caracteres"
            isValid = false
        } else {
            tilName.error = null
        }

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
        } else if (!password.matches(".*[A-Z].*".toRegex())) {
            tilPassword.error = "Debe contener al menos una mayúscula"
            isValid = false
        } else if (!password.matches(".*[0-9].*".toRegex())) {
            tilPassword.error = "Debe contener al menos un número"
            isValid = false
        } else {
            tilPassword.error = null
        }

        // Validar confirmación de contraseña
        val confirmPassword = etConfirmPassword.text.toString()
        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error = "Confirma tu contraseña"
            isValid = false
        } else if (password != confirmPassword) {
            tilConfirmPassword.error = "Las contraseñas no coinciden"
            isValid = false
        } else {
            tilConfirmPassword.error = null
        }

        // Validar términos y condiciones
        if (!cbTerms.isChecked) {
            Toast.makeText(
                this,
                "Debes aceptar los términos y condiciones",
                Toast.LENGTH_SHORT
            ).show()
            isValid = false
        }

        return isValid
    }

    private fun performRegister() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        // Deshabilitar botón mientras se procesa
        btnRegister.isEnabled = false
        btnRegister.text = "Registrando..."

        lifecycleScope.launch {
            val result = authManager.signUp(email, password, name)
            
            btnRegister.isEnabled = true
            btnRegister.text = getString(R.string.register)
            
            result.onSuccess {
                Toast.makeText(
                    this@RegisterActivity,
                    "¡Registro exitoso! Revisa tu email para confirmar tu cuenta.",
                    Toast.LENGTH_LONG
                ).show()

                // Volver a LoginActivity
                finish()
            }.onFailure { error ->
                val errorMessage = when {
                    error.message?.contains("already registered", ignoreCase = true) == true ->
                        "Este email ya está registrado. Intenta iniciar sesión."
                    error.message?.contains("Password should be at least") == true ->
                        "La contraseña es muy débil. Usa al menos 6 caracteres."
                    error.message?.contains("network", ignoreCase = true) == true ->
                        "Error de conexión. Verifica tu internet."
                    else -> "Error al registrar: ${error.message}"
                }
                
                Toast.makeText(
                    this@RegisterActivity,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
