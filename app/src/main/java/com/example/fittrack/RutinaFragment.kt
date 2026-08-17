package com.example.fittrack

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fittrack.shared.FitTrackSdk
import com.example.fittrack.shared.domain.FitnessValidator
import com.example.fittrack.shared.domain.Routine
import com.example.fittrack.shared.domain.RoutineCategory
import com.example.fittrack.shared.domain.WorkoutSession
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RutinaFragment : Fragment() {

    private lateinit var tabMisRutinas: LinearLayout
    private lateinit var tabHistorial: LinearLayout
    private lateinit var textMisRutinas: TextView
    private lateinit var textHistorial: TextView
    private lateinit var lineMisRutinas: View
    private lateinit var lineHistorial: View
    private lateinit var scrollMisRutinas: ScrollView
    private lateinit var scrollHistorial: ScrollView
    private lateinit var containerRutinas: LinearLayout
    private lateinit var containerHistorial: LinearLayout
    private lateinit var cardNuevaRutina: CardView
    private lateinit var etNombreRutina: TextInputEditText
    private lateinit var etDuracionRutina: TextInputEditText
    private lateinit var rgCategoriaRutina: RadioGroup
    private lateinit var btnElegirEjercicios: Button
    private lateinit var etFrecuenciaSemanal: TextInputEditText
    private lateinit var btnNuevaRutina: Button
    private lateinit var btnCancelarRutina: Button
    private lateinit var btnGuardarRutina: Button

    private var selectedExerciseIds: List<String> = emptyList()

    private val exercisePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result -> handleExercisePickerResult(result) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.fragment_rutina, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupTabs()
        setupNuevaRutinaForm()
        showRutinasTab()
        observeSession()
    }

    private fun initializeViews(view: View) {
        tabMisRutinas = view.findViewById(R.id.tabMisRutinas)
        tabHistorial = view.findViewById(R.id.tabHistorial)
        textMisRutinas = view.findViewById(R.id.textMisRutinas)
        textHistorial = view.findViewById(R.id.textHistorial)
        lineMisRutinas = view.findViewById(R.id.lineMisRutinas)
        lineHistorial = view.findViewById(R.id.lineHistorial)
        scrollMisRutinas = view.findViewById(R.id.scrollMisRutinas)
        scrollHistorial = view.findViewById(R.id.scrollHistorial)
        containerRutinas = view.findViewById(R.id.containerRutinas)
        containerHistorial = view.findViewById(R.id.containerHistorial)
        cardNuevaRutina = view.findViewById(R.id.cardNuevaRutina)
        etNombreRutina = view.findViewById(R.id.etNombreRutina)
        etDuracionRutina = view.findViewById(R.id.etDuracionRutina)
        rgCategoriaRutina = view.findViewById(R.id.rgCategoriaRutina)
        btnElegirEjercicios = view.findViewById(R.id.btnElegirEjercicios)
        etFrecuenciaSemanal = view.findViewById(R.id.etFrecuenciaSemanal)
        btnNuevaRutina = view.findViewById(R.id.btnNuevaRutina)
        btnCancelarRutina = view.findViewById(R.id.btnCancelarRutina)
        btnGuardarRutina = view.findViewById(R.id.btnGuardarRutina)
    }

    private fun observeSession() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    FitTrackSdk.session.routines.collect { renderRoutines(it) }
                }
                launch {
                    FitTrackSdk.session.workouts.collect { renderWorkouts(it) }
                }
            }
        }
    }

    private fun renderWorkouts(workouts: List<WorkoutSession>) {
        containerHistorial.removeAllViews()
        if (workouts.isEmpty()) {
            val empty = TextView(requireContext()).apply {
                text = "Aún no hay entrenamientos. Inicia uno desde Inicio."
                setTextColor(ContextCompat.getColor(requireContext(), R.color.track_night))
                textSize = 16f
            }
            containerHistorial.addView(empty)
            return
        }
        val inflater = layoutInflater
        workouts.forEach { workout ->
            val card = inflater.inflate(R.layout.item_entrenamiento, containerHistorial, false)
            card.findViewById<TextView>(R.id.tvWorkoutName).text = workout.routineName
            val duration = workout.durationMinutes?.let { "$it min" } ?: "—"
            card.findViewById<TextView>(R.id.tvWorkoutMeta).text = "${workout.dateLabel} • $duration"
            containerHistorial.addView(card)
        }
    }

    private fun renderRoutines(routines: List<Routine>) {
        containerRutinas.removeAllViews()
        if (routines.isEmpty()) {
            val empty = TextView(requireContext()).apply {
                text = "Aún no tienes rutinas. Crea la primera."
                setTextColor(ContextCompat.getColor(requireContext(), R.color.track_night))
                textSize = 16f
            }
            containerRutinas.addView(empty)
            return
        }
        val inflater = layoutInflater
        routines.forEach { routine ->
            val card = inflater.inflate(R.layout.item_rutina, containerRutinas, false)
            card.findViewById<TextView>(R.id.tvRoutineName).text = routine.name
            card.findViewById<TextView>(R.id.tvRoutineMeta).text =
                "${routine.durationMinutes} min • ${routine.exerciseCount} Ejercicios"
            card.findViewById<TextView>(R.id.tvRoutineFrequency).text =
                "${routine.weeklyFrequency}x/Semana"
            card.setOnClickListener {
                RoutineDetailDialogFragment.newInstance(routine.id)
                    .show(childFragmentManager, "routine_detail")
            }
            containerRutinas.addView(card)
        }
    }

    private fun setupTabs() {
        tabMisRutinas.setOnClickListener { showRutinasTab() }
        tabHistorial.setOnClickListener { showHistorialTab() }
    }

    private fun setupNuevaRutinaForm() {
        btnNuevaRutina.setOnClickListener { mostrarFormularioNuevaRutina() }
        btnCancelarRutina.setOnClickListener { ocultarFormularioNuevaRutina() }
        btnGuardarRutina.setOnClickListener { guardarNuevaRutina() }
        btnElegirEjercicios.setOnClickListener { abrirSelectorEjercicios() }
    }

    private fun abrirSelectorEjercicios() {
        val intent = Intent(requireContext(), ExercisePickerActivity::class.java)
        intent.putStringArrayListExtra(ExercisePickerActivity.EXTRA_SELECTED_IDS, ArrayList(selectedExerciseIds))
        exercisePickerLauncher.launch(intent)
    }

    private fun handleExercisePickerResult(result: androidx.activity.result.ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) return
        val ids = result.data?.getStringArrayListExtra(ExercisePickerActivity.EXTRA_SELECTED_IDS) ?: return
        selectedExerciseIds = ids.toList()
        updateElegirEjerciciosLabel()
    }

    private fun updateElegirEjerciciosLabel() {
        btnElegirEjercicios.text = if (selectedExerciseIds.isEmpty()) {
            getString(R.string.choose_exercises_title)
        } else {
            "${getString(R.string.choose_exercises_title)} (${selectedExerciseIds.size})"
        }
    }

    private fun selectedCategory(): String = when (rgCategoriaRutina.checkedRadioButtonId) {
        R.id.rbCategoriaTrenSuperior -> RoutineCategory.TREN_SUPERIOR
        R.id.rbCategoriaTrenInferior -> RoutineCategory.TREN_INFERIOR
        R.id.rbCategoriaDescanso -> RoutineCategory.DESCANSO
        else -> RoutineCategory.FULL_BODY
    }

    private fun mostrarFormularioNuevaRutina() {
        cardNuevaRutina.visibility = View.VISIBLE
        etNombreRutina.requestFocus()
    }

    private fun ocultarFormularioNuevaRutina() {
        cardNuevaRutina.visibility = View.GONE
        etNombreRutina.text?.clear()
        etDuracionRutina.text?.clear()
        etFrecuenciaSemanal.text?.clear()
        rgCategoriaRutina.check(R.id.rbCategoriaTrenSuperior)
        selectedExerciseIds = emptyList()
        updateElegirEjerciciosLabel()
    }

    private fun guardarNuevaRutina() {
        val nombre = etNombreRutina.text.toString().trim()
        val nameError = FitnessValidator.routineNameError(nombre)
        if (nameError != null) {
            showToast(nameError)
            etNombreRutina.requestFocus()
            return
        }
        val duracion = etDuracionRutina.text.toString().toIntOrNull()
        val frecuencia = etFrecuenciaSemanal.text.toString().toIntOrNull()
        if (duracion == null || frecuencia == null) {
            showToast("Por favor ingresa valores numéricos válidos")
            return
        }
        FitnessValidator.durationError(duracion)?.let { showToast(it); return }
        FitnessValidator.exerciseCountError(selectedExerciseIds.size)?.let { showToast(it); return }
        FitnessValidator.weeklyFrequencyError(frecuencia)?.let { showToast(it); return }
        val category = selectedCategory()

        viewLifecycleOwner.lifecycleScope.launch {
            FitTrackSdk.session.addRoutine(nombre, duracion, frecuencia, category, selectedExerciseIds)
                .onSuccess {
                    showToast("Rutina '$nombre' creada")
                    ocultarFormularioNuevaRutina()
                }
                .onFailure {
                    showToast(it.message ?: "No se pudo crear la rutina")
                }
        }
    }

    private fun showRutinasTab() {
        textMisRutinas.setTextColor(ContextCompat.getColor(requireContext(), R.color.fit_glow))
        textMisRutinas.alpha = 1f
        lineMisRutinas.visibility = View.VISIBLE
        textHistorial.setTextColor(ContextCompat.getColor(requireContext(), R.color.track_night))
        textHistorial.alpha = 0.6f
        lineHistorial.visibility = View.INVISIBLE
        scrollMisRutinas.visibility = View.VISIBLE
        scrollHistorial.visibility = View.GONE
        btnNuevaRutina.visibility = View.VISIBLE
    }

    private fun showHistorialTab() {
        textHistorial.setTextColor(ContextCompat.getColor(requireContext(), R.color.fit_glow))
        textHistorial.alpha = 1f
        lineHistorial.visibility = View.VISIBLE
        textMisRutinas.setTextColor(ContextCompat.getColor(requireContext(), R.color.track_night))
        textMisRutinas.alpha = 0.6f
        lineMisRutinas.visibility = View.INVISIBLE
        scrollMisRutinas.visibility = View.GONE
        scrollHistorial.visibility = View.VISIBLE
        btnNuevaRutina.visibility = View.GONE
        if (cardNuevaRutina.isVisible) {
            ocultarFormularioNuevaRutina()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = RutinaFragment()
    }
}
