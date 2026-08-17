package com.example.fittrack

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fittrack.shared.FitTrackSdk
import com.example.fittrack.shared.domain.Exercise
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ExercisePickerActivity : AppCompatActivity() {

    private lateinit var containerExercises: LinearLayout
    private lateinit var btnConfirmSelection: MaterialButton

    private val selectedIds = mutableSetOf<String>()
    private var exercises: List<Exercise> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_picker)

        containerExercises = findViewById(R.id.containerExercises)
        btnConfirmSelection = findViewById(R.id.btnConfirmSelection)

        intent.getStringArrayListExtra(EXTRA_SELECTED_IDS)?.let { selectedIds.addAll(it) }

        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        btnConfirmSelection.setOnClickListener { confirmSelection() }

        loadExercises()
        updateConfirmLabel()
    }

    private fun loadExercises() {
        val cached = FitTrackSdk.session.exercises.value
        if (cached.isNotEmpty()) {
            bindExercises(cached)
            return
        }
        lifecycleScope.launch {
            FitTrackSdk.session.refreshExercises()
            val loaded = FitTrackSdk.session.exercises.value
            if (loaded.isEmpty()) {
                Toast.makeText(
                    this@ExercisePickerActivity,
                    "No se pudo cargar el catálogo de ejercicios",
                    Toast.LENGTH_LONG,
                ).show()
            }
            bindExercises(loaded)
        }
    }

    private fun bindExercises(loaded: List<Exercise>) {
        exercises = loaded
        containerExercises.removeAllViews()
        val inflater = layoutInflater
        loaded
            .groupBy { it.muscleGroup ?: "Otros" }
            .toSortedMap()
            .forEach { (group, groupExercises) ->
                containerExercises.addView(groupHeader(group))
                groupExercises.forEach { exercise ->
                    containerExercises.addView(exerciseRow(inflater, exercise))
                }
            }
    }

    private fun groupHeader(title: String): TextView = TextView(this).apply {
        text = title.replaceFirstChar { it.uppercase() }
        setTextColor(FitTrackColor.fitGlow)
        textSize = 14f
        setPadding(0, 24, 0, 8)
        setTypeface(typeface, android.graphics.Typeface.BOLD)
    }

    private fun exerciseRow(inflater: LayoutInflater, exercise: Exercise): android.view.View {
        val row = inflater.inflate(R.layout.item_exercise_pick, containerExercises, false)
        val checkBox = row.findViewById<CheckBox>(R.id.cbExercise)
        row.findViewById<TextView>(R.id.tvExerciseName).text = exercise.name
        row.findViewById<TextView>(R.id.tvExerciseMeta).text =
            listOfNotNull(exercise.type, exercise.level).joinToString(" • ")

        checkBox.isChecked = exercise.id in selectedIds
        row.setOnClickListener {
            val nowChecked = !checkBox.isChecked
            checkBox.isChecked = nowChecked
            if (nowChecked) selectedIds.add(exercise.id) else selectedIds.remove(exercise.id)
            updateConfirmLabel()
        }
        return row
    }

    private fun updateConfirmLabel() {
        btnConfirmSelection.text = if (selectedIds.isEmpty()) {
            getString(R.string.confirm_selection)
        } else {
            "${getString(R.string.confirm_selection)} (${selectedIds.size})"
        }
    }

    private fun confirmSelection() {
        if (selectedIds.isEmpty()) {
            Toast.makeText(this, "Elige al menos un ejercicio", Toast.LENGTH_SHORT).show()
            return
        }
        val names = exercises.filter { it.id in selectedIds }.map { it.name }
        val result = Intent().apply {
            putStringArrayListExtra(EXTRA_SELECTED_IDS, ArrayList(selectedIds))
            putStringArrayListExtra(EXTRA_SELECTED_NAMES, ArrayList(names))
        }
        setResult(RESULT_OK, result)
        finish()
    }

    companion object {
        const val EXTRA_SELECTED_IDS = "extra_selected_exercise_ids"
        const val EXTRA_SELECTED_NAMES = "extra_selected_exercise_names"
    }
}
