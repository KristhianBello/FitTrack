package com.example.fittrack

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fittrack.shared.FitTrackSdk
import com.example.fittrack.shared.domain.FitnessValidator
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.Calendar

class PersonalDataActivity : AppCompatActivity() {

    private lateinit var tilBirthDate: TextInputLayout
    private lateinit var etBirthDate: TextInputEditText
    private lateinit var rgGender: RadioGroup
    private lateinit var tilHeight: TextInputLayout
    private lateinit var etHeight: TextInputEditText
    private lateinit var tilCurrentWeight: TextInputLayout
    private lateinit var etCurrentWeight: TextInputEditText
    private lateinit var tilGoalWeight: TextInputLayout
    private lateinit var etGoalWeight: TextInputEditText
    private lateinit var btnSave: MaterialButton

    private var birthIsoDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_data)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        tilBirthDate = findViewById(R.id.tilBirthDate)
        etBirthDate = findViewById(R.id.etBirthDate)
        rgGender = findViewById(R.id.rgGender)
        tilHeight = findViewById(R.id.tilHeight)
        etHeight = findViewById(R.id.etHeight)
        tilCurrentWeight = findViewById(R.id.tilCurrentWeight)
        etCurrentWeight = findViewById(R.id.etCurrentWeight)
        tilGoalWeight = findViewById(R.id.tilGoalWeight)
        etGoalWeight = findViewById(R.id.etGoalWeight)
        btnSave = findViewById(R.id.btnSavePersonalData)
    }

    private fun setupListeners() {
        etBirthDate.setOnClickListener { showDatePicker() }
        tilBirthDate.setEndIconOnClickListener { showDatePicker() }
        btnSave.setOnClickListener { validateAndSave() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -18)
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                birthIsoDate = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
                etBirthDate.setText("%02d/%02d/%04d".format(dayOfMonth, month + 1, year))
                tilBirthDate.error = null
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        ).show()
    }

    private fun selectedGender(): String? = when (rgGender.checkedRadioButtonId) {
        R.id.rbMasculino -> "masculino"
        R.id.rbFemenino -> "femenino"
        R.id.rbOtro -> "otro"
        R.id.rbPrefieroNoDecir -> "prefiero_no_decir"
        else -> null
    }

    private fun validateAndSave() {
        val birthDateError = FitnessValidator.birthDateError(birthIsoDate)
        if (birthDateError != null) {
            tilBirthDate.error = birthDateError
            return
        }
        tilBirthDate.error = null

        val gender = selectedGender()
        if (gender == null) {
            Toast.makeText(this, "Selecciona tu género", Toast.LENGTH_SHORT).show()
            return
        }

        val heightText = etHeight.text.toString()
        val height = heightText.toFloatOrNull()
        if (height == null) {
            tilHeight.error = "Ingresa un número válido"
            return
        }
        val heightError = FitnessValidator.heightError(height)
        if (heightError != null) {
            tilHeight.error = heightError
            return
        }
        tilHeight.error = null

        val currentWeightText = etCurrentWeight.text.toString()
        val currentWeight = currentWeightText.toFloatOrNull()
        if (currentWeight == null) {
            tilCurrentWeight.error = "Ingresa un número válido"
            return
        }
        val currentWeightError = FitnessValidator.weightError(currentWeight)
        if (currentWeightError != null) {
            tilCurrentWeight.error = currentWeightError
            return
        }
        tilCurrentWeight.error = null

        val goalWeightText = etGoalWeight.text.toString()
        val goalWeight = goalWeightText.toFloatOrNull()
        if (goalWeight == null) {
            tilGoalWeight.error = "Ingresa un número válido"
            return
        }
        val goalWeightError = FitnessValidator.weightError(goalWeight)
        if (goalWeightError != null) {
            tilGoalWeight.error = goalWeightError
            return
        }
        tilGoalWeight.error = null

        savePersonalData(height, currentWeight, goalWeight, gender)
    }

    private fun savePersonalData(height: Float, currentWeight: Float, goalWeight: Float, gender: String) {
        btnSave.isEnabled = false
        btnSave.text = "Guardando..."

        lifecycleScope.launch {
            val result = FitTrackSdk.session.savePersonalData(
                heightCm = height,
                currentWeightKg = currentWeight,
                goalWeightKg = goalWeight,
                birthIsoDate = birthIsoDate,
                gender = gender,
            )

            btnSave.isEnabled = true
            btnSave.text = getString(R.string.save_and_continue)

            result.onSuccess {
                goToMain()
            }.onFailure { error ->
                Toast.makeText(
                    this@PersonalDataActivity,
                    error.message ?: "No se pudieron guardar tus datos",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
