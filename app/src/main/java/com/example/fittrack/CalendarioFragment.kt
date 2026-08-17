package com.example.fittrack

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fittrack.shared.FitTrackSdk
import com.example.fittrack.shared.domain.CalendarStats
import com.example.fittrack.shared.domain.MonthlySummary
import com.example.fittrack.shared.domain.Routine
import com.example.fittrack.shared.domain.WorkoutSession
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarioFragment : Fragment() {

    private lateinit var tvMesAnio: TextView
    private lateinit var rowDiasSemana: LinearLayout
    private lateinit var gridDias: GridLayout
    private lateinit var tvDetalleDiaTitulo: TextView
    private lateinit var tvDetalleDiaContenido: TextView
    private lateinit var containerProximasRutinas: LinearLayout

    private lateinit var rowStatTotal: TextView
    private lateinit var rowStatSuperior: TextView
    private lateinit var rowStatInferior: TextView
    private lateinit var rowStatDescanso: TextView

    private val calendar = Calendar.getInstance()
    private var selectedIsoDate: String = todayIso()

    private var workouts: List<WorkoutSession> = emptyList()
    private var routines: List<Routine> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.fragment_calendario, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        buildWeekdayHeader()
        setupMonthNavigation(view)
        observeSession()
    }

    private fun initViews(view: View) {
        tvMesAnio = view.findViewById(R.id.tvMesAnio)
        rowDiasSemana = view.findViewById(R.id.rowDiasSemana)
        gridDias = view.findViewById(R.id.gridDias)
        tvDetalleDiaTitulo = view.findViewById(R.id.tvDetalleDiaTitulo)
        tvDetalleDiaContenido = view.findViewById(R.id.tvDetalleDiaContenido)
        containerProximasRutinas = view.findViewById(R.id.containerProximasRutinas)

        rowStatTotal = view.findViewById<View>(R.id.rowStatTotal).findViewById(R.id.tvStatValue)
        view.findViewById<View>(R.id.rowStatTotal).findViewById<TextView>(R.id.tvStatLabel).text =
            getString(R.string.stat_total_hours)

        rowStatSuperior = view.findViewById<View>(R.id.rowStatSuperior).findViewById(R.id.tvStatValue)
        view.findViewById<View>(R.id.rowStatSuperior).findViewById<TextView>(R.id.tvStatLabel).text =
            getString(R.string.stat_upper_body_hours)

        rowStatInferior = view.findViewById<View>(R.id.rowStatInferior).findViewById(R.id.tvStatValue)
        view.findViewById<View>(R.id.rowStatInferior).findViewById<TextView>(R.id.tvStatLabel).text =
            getString(R.string.stat_lower_body_hours)

        rowStatDescanso = view.findViewById<View>(R.id.rowStatDescanso).findViewById(R.id.tvStatValue)
        view.findViewById<View>(R.id.rowStatDescanso).findViewById<TextView>(R.id.tvStatLabel).text =
            getString(R.string.stat_rest_hours)
    }

    private fun setupMonthNavigation(view: View) {
        view.findViewById<View>(R.id.btnMesAnterior).setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            renderAll()
        }
        view.findViewById<View>(R.id.btnMesSiguiente).setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            renderAll()
        }
    }

    private fun observeSession() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    FitTrackSdk.session.workouts.collect {
                        workouts = it
                        renderAll()
                    }
                }
                launch {
                    FitTrackSdk.session.routines.collect {
                        routines = it
                        renderProximasRutinas()
                    }
                }
            }
        }
    }

    private fun buildWeekdayHeader() {
        rowDiasSemana.removeAllViews()
        listOf("L", "M", "M", "J", "V", "S", "D").forEach { label ->
            rowDiasSemana.addView(
                TextView(requireContext()).apply {
                    text = label
                    gravity = android.view.Gravity.CENTER
                    setTextColor(FitTrackColor.trackNight)
                    textSize = 12f
                    alpha = 0.6f
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                },
            )
        }
    }

    private fun renderAll() {
        if (!isAdded) return
        renderMonthHeader()
        renderGrid()
        renderDayDetail()
        renderSummary()
    }

    private fun renderMonthHeader() {
        val formatter = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
        tvMesAnio.text = formatter.format(calendar.time).replaceFirstChar { it.uppercase() }
    }

    private fun renderGrid() {
        gridDias.removeAllViews()
        val byDate = CalendarStats.workoutsByDate(workouts)

        val monthCalendar = calendar.clone() as Calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstWeekday = (monthCalendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 // 0=lunes .. 6=domingo
        val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val cellSize = dpToPx(40)

        repeat(firstWeekday) {
            gridDias.addView(emptyCell(cellSize))
        }

        for (day in 1..daysInMonth) {
            val iso = isoFor(monthCalendar.get(Calendar.YEAR), monthCalendar.get(Calendar.MONTH), day)
            val dayWorkouts = byDate[iso].orEmpty()
            gridDias.addView(dayCell(day, iso, dayWorkouts, cellSize))
        }
    }

    private fun emptyCell(size: Int): View = View(requireContext()).apply {
        layoutParams = gridCellParams(size)
    }

    private fun dayCell(day: Int, iso: String, dayWorkouts: List<WorkoutSession>, size: Int): TextView {
        return TextView(requireContext()).apply {
            text = day.toString()
            gravity = android.view.Gravity.CENTER
            textSize = 13f
            layoutParams = gridCellParams(size)
            val isSelected = iso == selectedIsoDate
            val hasWorkout = dayWorkouts.isNotEmpty()
            when {
                isSelected -> {
                    background = circleDrawable(FitTrackColor.trackNight)
                    setTextColor(FitTrackColor.white)
                }
                hasWorkout -> {
                    background = circleDrawable(RoutineCategoryUi.color(dayWorkouts.first().category))
                    setTextColor(FitTrackColor.white)
                }
                else -> {
                    background = null
                    setTextColor(FitTrackColor.trackNight)
                }
            }
            setOnClickListener {
                selectedIsoDate = iso
                renderGrid()
                renderDayDetail()
            }
        }
    }

    private fun gridCellParams(size: Int): GridLayout.LayoutParams {
        return GridLayout.LayoutParams(
            GridLayout.spec(GridLayout.UNDEFINED, 1f),
            GridLayout.spec(GridLayout.UNDEFINED, 1f),
        ).apply {
            width = 0
            height = size
            setMargins(2, 2, 2, 2)
        }
    }

    private fun circleDrawable(color: Int): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(color)
    }

    private fun renderDayDetail() {
        val today = todayIso()
        val dayWorkouts = CalendarStats.workoutsByDate(workouts)[selectedIsoDate].orEmpty()
        tvDetalleDiaTitulo.text = displayDate(selectedIsoDate)

        tvDetalleDiaContenido.text = when {
            dayWorkouts.isNotEmpty() -> dayWorkouts.joinToString("\n") { session ->
                val categoryLabel = getString(RoutineCategoryUi.labelRes(session.category))
                "${session.routineName} • ${session.durationMinutes ?: 0} min • $categoryLabel"
            }
            selectedIsoDate > today -> "Día futuro"
            else -> "Sin entrenamiento este día"
        }

        val routineId = dayWorkouts.firstOrNull()?.routineId
        tvDetalleDiaContenido.setOnClickListener(
            if (routineId != null) {
                View.OnClickListener {
                    RoutineDetailDialogFragment.newInstance(routineId)
                        .show(childFragmentManager, "routine_detail")
                }
            } else {
                null
            },
        )
    }

    private fun renderProximasRutinas() {
        containerProximasRutinas.removeAllViews()
        if (routines.isEmpty()) {
            containerProximasRutinas.addView(
                TextView(requireContext()).apply {
                    text = "Aún no tienes rutinas guardadas."
                    setTextColor(FitTrackColor.trackNight)
                    alpha = 0.7f
                },
            )
            return
        }
        val inflater = layoutInflater
        routines.forEach { routine ->
            val card = inflater.inflate(R.layout.item_rutina, containerProximasRutinas, false)
            card.findViewById<TextView>(R.id.tvRoutineName).text = routine.name
            card.findViewById<TextView>(R.id.tvRoutineMeta).text =
                "${routine.durationMinutes} min • ${routine.exerciseCount} Ejercicios"
            card.findViewById<TextView>(R.id.tvRoutineFrequency).text =
                getString(RoutineCategoryUi.labelRes(routine.category))
            card.setOnClickListener {
                RoutineDetailDialogFragment.newInstance(routine.id)
                    .show(childFragmentManager, "routine_detail")
            }
            containerProximasRutinas.addView(card)
        }
    }

    private fun renderSummary() {
        val monthCalendar = calendar.clone() as Calendar
        val year = monthCalendar.get(Calendar.YEAR)
        val month = monthCalendar.get(Calendar.MONTH)
        val monthPrefix = "%04d-%02d".format(year, month + 1)
        val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val today = Calendar.getInstance()
        val elapsedDays: List<String> = when {
            year < today.get(Calendar.YEAR) || (year == today.get(Calendar.YEAR) && month < today.get(Calendar.MONTH)) ->
                (1..daysInMonth).map { isoFor(year, month, it) }
            year == today.get(Calendar.YEAR) && month == today.get(Calendar.MONTH) ->
                (1..today.get(Calendar.DAY_OF_MONTH)).map { isoFor(year, month, it) }
            else -> emptyList()
        }

        val summary: MonthlySummary = CalendarStats.monthlySummary(workouts, monthPrefix, elapsedDays)
        rowStatTotal.text = hoursLabel(summary.totalMinutes)
        rowStatSuperior.text = hoursLabel(summary.upperBodyMinutes)
        rowStatInferior.text = hoursLabel(summary.lowerBodyMinutes)
        rowStatDescanso.text = "${summary.restHours} h"
    }

    private fun hoursLabel(minutes: Int): String {
        val hours = minutes / 60f
        return "%.1f h".format(hours)
    }

    private fun displayDate(iso: String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val display = SimpleDateFormat("d 'de' MMMM", Locale("es", "ES"))
        val date = parser.parse(iso) ?: return iso
        return display.format(date)
    }

    private fun isoFor(year: Int, month: Int, day: Int): String =
        "%04d-%02d-%02d".format(year, month + 1, day)

    private fun todayIso(): String {
        val now = Calendar.getInstance()
        return isoFor(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

    companion object {
        @JvmStatic
        fun newInstance() = CalendarioFragment()
    }
}
