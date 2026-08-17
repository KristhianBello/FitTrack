package com.example.fittrack

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.fittrack.shared.FitTrackSdk
import com.example.fittrack.shared.domain.RoutineExercise
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class RoutineDetailDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val routineId = requireArguments().getString(ARG_ROUTINE_ID).orEmpty()
        val view = layoutInflater.inflate(R.layout.dialog_routine_detail, null)

        val tvName = view.findViewById<TextView>(R.id.tvDialogRoutineName)
        val tvCategory = view.findViewById<TextView>(R.id.tvDialogRoutineCategory)
        val tvMeta = view.findViewById<TextView>(R.id.tvDialogRoutineMeta)
        val containerExercises = view.findViewById<LinearLayout>(R.id.containerDialogExercises)

        val routine = FitTrackSdk.session.routines.value.firstOrNull { it.id == routineId }
        tvName.text = routine?.name ?: "Rutina"
        tvCategory.text = getString(RoutineCategoryUi.labelRes(routine?.category))
        tvCategory.setTextColor(RoutineCategoryUi.color(routine?.category))
        tvMeta.text = if (routine != null) {
            "${routine.durationMinutes} min • ${routine.exerciseCount} ejercicios • ${routine.weeklyFrequency}x/semana"
        } else {
            ""
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .setPositiveButton("Cerrar", null)
            .create()

        if (routineId.isNotBlank()) {
            loadExercises(routineId, containerExercises)
        }

        return dialog
    }

    private fun loadExercises(routineId: String, container: LinearLayout) {
        lifecycleScope.launch {
            val exercises = FitTrackSdk.session.routineExercises(routineId)
            bindExercises(container, exercises)
        }
    }

    private fun bindExercises(container: LinearLayout, exercises: List<RoutineExercise>) {
        container.removeAllViews()
        if (exercises.isEmpty()) {
            container.addView(TextView(requireContext()).apply {
                text = "Esta rutina no tiene ejercicios registrados."
                setTextColor(FitTrackColor.trackNight)
                alpha = 0.7f
            })
            return
        }
        exercises.sortedBy { it.order }.forEach { exercise ->
            container.addView(TextView(requireContext()).apply {
                text = "• ${exercise.exerciseName} — ${exercise.sets}x${exercise.reps}"
                setTextColor(FitTrackColor.trackNight)
                textSize = 15f
                setPadding(0, 6, 0, 6)
            })
        }
    }

    companion object {
        private const val ARG_ROUTINE_ID = "arg_routine_id"

        fun newInstance(routineId: String) = RoutineDetailDialogFragment().apply {
            arguments = bundleOf(ARG_ROUTINE_ID to routineId)
        }
    }
}
