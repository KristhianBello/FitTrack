package com.example.fittrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fittrack.shared.FitTrackSdk
import com.example.fittrack.shared.domain.Routine
import com.example.fittrack.shared.domain.WorkoutSession
import com.example.fittrack.shared.domain.WorkoutStats
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class HomeFragment2 : Fragment() {

    private lateinit var greeting: TextView
    private lateinit var progress: TextView
    private lateinit var progressPercent: TextView
    private lateinit var todayName: TextView
    private lateinit var todayMeta: TextView
    private lateinit var startButton: MaterialButton

    private var routines: List<Routine> = emptyList()
    private var workouts: List<WorkoutSession> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.fragment_home2, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        greeting = view.findViewById(R.id.tvGreeting)
        progress = view.findViewById(R.id.tvProgressFraction)
        progressPercent = view.findViewById(R.id.tvProgressPercent)
        todayName = view.findViewById(R.id.tvTodayRoutineName)
        todayMeta = view.findViewById(R.id.tvTodayRoutineMeta)
        startButton = view.findViewById(R.id.btnIniciarEntrenamiento)

        startButton.setOnClickListener { startWorkout() }
        view.findViewById<CardView>(R.id.progressCard)?.setOnClickListener {
            Toast.makeText(context, "Completados esta semana: ${progress.text}", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<CardView>(R.id.routineCard)?.setOnClickListener {
            val routine = routines.firstOrNull()
            Toast.makeText(
                context,
                routine?.name ?: "Crea una rutina en la pestaña Ejercicio",
                Toast.LENGTH_SHORT,
            ).show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    FitTrackSdk.session.profile.collect { profile ->
                        greeting.text = "¡Hola, ${profile?.name ?: "Atleta"}!"
                    }
                }
                launch {
                    FitTrackSdk.session.routines.collect { loaded ->
                        routines = loaded
                        bindHome()
                    }
                }
                launch {
                    FitTrackSdk.session.workouts.collect { loaded ->
                        workouts = loaded
                        bindHome()
                    }
                }
            }
        }
    }

    private fun bindHome() {
        val completed = WorkoutStats.completedThisWeek(workouts)
        val goal = WorkoutStats.weeklyGoal(routines)
        val percent = WorkoutStats.percent(completed, goal)
        progress.text = completed.toString()
        progressPercent.text = "$percent%"

        val routine = routines.firstOrNull()
        if (routine == null) {
            todayName.text = "Crea tu primera rutina"
            todayMeta.text = "Aún no hay entrenamiento programado"
            startButton.isEnabled = false
        } else {
            todayName.text = routine.name
            todayMeta.text = "${routine.durationMinutes} min • ${routine.exerciseCount} Ejercicios"
            startButton.isEnabled = true
        }
    }

    private fun startWorkout() {
        val routine = routines.firstOrNull()
        if (routine == null) {
            Toast.makeText(context, "Crea una rutina antes de entrenar", Toast.LENGTH_SHORT).show()
            return
        }
        startButton.isEnabled = false
        viewLifecycleOwner.lifecycleScope.launch {
            FitTrackSdk.session.completeWorkout(routine)
                .onSuccess {
                    Toast.makeText(context, "Entrenamiento '${routine.name}' registrado", Toast.LENGTH_SHORT).show()
                }
                .onFailure {
                    Toast.makeText(context, it.message ?: "No se pudo registrar el entrenamiento", Toast.LENGTH_LONG).show()
                }
            startButton.isEnabled = routines.isNotEmpty()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment2()
    }
}
