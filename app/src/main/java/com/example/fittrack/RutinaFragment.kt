package com.example.fittrack

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ScrollView
import android.widget.Toast
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText

/**
 * Fragment para gestión de rutinas de ejercicio
 * Incluye pestañas para "Mis Rutinas" e "Historial"
 */
class RutinaFragment : Fragment() {

    // UI Components - Tabs
    private lateinit var tabMisRutinas: LinearLayout
    private lateinit var tabHistorial: LinearLayout
    private lateinit var textMisRutinas: TextView
    private lateinit var textHistorial: TextView
    private lateinit var lineMisRutinas: View
    private lateinit var lineHistorial: View
    private lateinit var scrollMisRutinas: ScrollView
    private lateinit var scrollHistorial: ScrollView

    // UI Components - Nueva Rutina
    private lateinit var cardNuevaRutina: CardView
    private lateinit var etNombreRutina: TextInputEditText
    private lateinit var etDuracionRutina: TextInputEditText
    private lateinit var etCantidadEjercicios: TextInputEditText
    private lateinit var etFrecuenciaSemanal: TextInputEditText
    private lateinit var btnCancelarRutina: Button
    private lateinit var btnGuardarRutina: Button

    // Lista de rutinas (en memoria por ahora)
    private val rutinas = mutableListOf(
        Rutina("Full Body Día 1", 30, 6, 3),
        Rutina("Pecho y Tríceps", 45, 8, 2),
        Rutina("Espalda y Bíceps", 40, 7, 2),
        Rutina("Piernas Completas", 50, 9, 1)
    )

    // Estado de las pestañas
    private var isRutinasTabActive = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rutina, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        initializeViews(view)

        // Configurar pestañas
        setupTabs()


        // Configurar formulario de nueva rutina
        setupNuevaRutinaForm()

        // Configurar tarjetas de rutinas
        setupRoutineCards(view)

        // Mostrar pestaña inicial (Mis Rutinas)
        showRutinasTab()
    }

    private fun initializeViews(view: View) {
        // Tabs
        tabMisRutinas = view.findViewById(R.id.tabMisRutinas)
        tabHistorial = view.findViewById(R.id.tabHistorial)
        textMisRutinas = view.findViewById(R.id.textMisRutinas)
        textHistorial = view.findViewById(R.id.textHistorial)
        lineMisRutinas = view.findViewById(R.id.lineMisRutinas)
        lineHistorial = view.findViewById(R.id.lineHistorial)
        scrollMisRutinas = view.findViewById(R.id.scrollMisRutinas)
        scrollHistorial = view.findViewById(R.id.scrollHistorial)

        // Nueva Rutina
        cardNuevaRutina = view.findViewById(R.id.cardNuevaRutina)
        etNombreRutina = view.findViewById(R.id.etNombreRutina)
        etDuracionRutina = view.findViewById(R.id.etDuracionRutina)
        etCantidadEjercicios = view.findViewById(R.id.etCantidadEjercicios)
        etFrecuenciaSemanal = view.findViewById(R.id.etFrecuenciaSemanal)
        btnCancelarRutina = view.findViewById(R.id.btnCancelarRutina)
        btnGuardarRutina = view.findViewById(R.id.btnGuardarRutina)
    }

    private fun setupTabs() {
        tabMisRutinas.setOnClickListener {
            showRutinasTab()
        }

        tabHistorial.setOnClickListener {
            showHistorialTab()
        }
    }


    private fun setupNuevaRutinaForm() {
        btnCancelarRutina.setOnClickListener {
            ocultarFormularioNuevaRutina()
        }

        btnGuardarRutina.setOnClickListener {
            guardarNuevaRutina()
        }
    }

    private fun setupRoutineCards(view: View) {
        // Configurar clicks en las tarjetas de rutinas existentes
        val routineCards = mutableListOf<CardView>()

        // Buscar todas las CardView en el scrollMisRutinas
        val scrollView = view.findViewById<ScrollView>(R.id.scrollMisRutinas)
        val linearLayout = scrollView.getChildAt(0) as? LinearLayout

        linearLayout?.let { container ->
            for (i in 0 until container.childCount) {
                val child = container.getChildAt(i)
                if (child is CardView) {
                    routineCards.add(child)
                }
            }
        }

        // Configurar listeners para cada tarjeta
        routineCards.forEachIndexed { index, cardView ->
            cardView.setOnClickListener {
                if (index < rutinas.size) {
                    val rutina = rutinas[index]
                    showToast("Abriendo '${rutina.nombre}'")
                } else {
                    showToast("Rutina próximamente disponible")
                }
            }
        }
    }

    private fun mostrarFormularioNuevaRutina() {
        cardNuevaRutina.visibility = View.VISIBLE
        etNombreRutina.requestFocus()
    }

    private fun ocultarFormularioNuevaRutina() {
        cardNuevaRutina.visibility = View.GONE
        limpiarFormulario()
    }

    private fun limpiarFormulario() {
        etNombreRutina.text?.clear()
        etDuracionRutina.text?.clear()
        etCantidadEjercicios.text?.clear()
        etFrecuenciaSemanal.text?.clear()
    }

    private fun guardarNuevaRutina() {
        val nombre = etNombreRutina.text.toString().trim()
        val duracionStr = etDuracionRutina.text.toString().trim()
        val ejerciciosStr = etCantidadEjercicios.text.toString().trim()
        val frecuenciaStr = etFrecuenciaSemanal.text.toString().trim()

        // Validaciones
        if (nombre.isEmpty()) {
            showToast("Por favor ingresa el nombre de la rutina")
            etNombreRutina.requestFocus()
            return
        }

        if (duracionStr.isEmpty()) {
            showToast("Por favor ingresa la duración")
            etDuracionRutina.requestFocus()
            return
        }

        if (ejerciciosStr.isEmpty()) {
            showToast("Por favor ingresa la cantidad de ejercicios")
            etCantidadEjercicios.requestFocus()
            return
        }

        if (frecuenciaStr.isEmpty()) {
            showToast("Por favor ingresa la frecuencia semanal")
            etFrecuenciaSemanal.requestFocus()
            return
        }

        try {
            val duracion = duracionStr.toInt()
            val ejercicios = ejerciciosStr.toInt()
            val frecuencia = frecuenciaStr.toInt()

            // Validar rangos
            if (duracion <= 0 || duracion > 300) {
                showToast("La duración debe estar entre 1 y 300 minutos")
                return
            }

            if (ejercicios <= 0 || ejercicios > 50) {
                showToast("La cantidad de ejercicios debe estar entre 1 y 50")
                return
            }

            if (frecuencia <= 0 || frecuencia > 7) {
                showToast("La frecuencia debe estar entre 1 y 7 veces por semana")
                return
            }

            // Crear nueva rutina
            val nuevaRutina = Rutina(nombre, duracion, ejercicios, frecuencia)
            rutinas.add(0, nuevaRutina) // Agregar al inicio

            // Actualizar UI (en una implementación real, se usaría RecyclerView)
            showToast("Rutina '$nombre' creada exitosamente")

            // Ocultar formulario
            ocultarFormularioNuevaRutina()

            // Nota: Aquí deberías actualizar la lista de tarjetas visualmente
            // Por ahora, solo mostramos el mensaje de éxito

        } catch (_: NumberFormatException) {
            showToast("Por favor ingresa valores numéricos válidos")
        }
    }

    private fun showRutinasTab() {
        isRutinasTabActive = true

        // Actualizar estilos de pestañas
        textMisRutinas.setTextColor(ContextCompat.getColor(requireContext(), R.color.fit_glow))
        textMisRutinas.alpha = 1f
        lineMisRutinas.visibility = View.VISIBLE

        textHistorial.setTextColor(ContextCompat.getColor(requireContext(), R.color.track_night))
        textHistorial.alpha = 0.6f
        lineHistorial.visibility = View.INVISIBLE

        // Mostrar contenido correspondiente
        scrollMisRutinas.visibility = View.VISIBLE
        scrollHistorial.visibility = View.GONE
    }

    private fun showHistorialTab() {
        isRutinasTabActive = false

        // Actualizar estilos de pestañas
        textHistorial.setTextColor(ContextCompat.getColor(requireContext(), R.color.fit_glow))
        textHistorial.alpha = 1f
        lineHistorial.visibility = View.VISIBLE

        textMisRutinas.setTextColor(ContextCompat.getColor(requireContext(), R.color.track_night))
        textMisRutinas.alpha = 0.6f
        lineMisRutinas.visibility = View.INVISIBLE

        // Mostrar contenido correspondiente
        scrollMisRutinas.visibility = View.GONE
        scrollHistorial.visibility = View.VISIBLE

        // Ocultar formulario si está visible
        if (cardNuevaRutina.isVisible) {
            ocultarFormularioNuevaRutina()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Data class para representar una rutina
     */
    data class Rutina(
        val nombre: String,
        val duracionMinutos: Int,
        val cantidadEjercicios: Int,
        val frecuenciaSemanal: Int
    )

    companion object {
        @JvmStatic
        fun newInstance() = RutinaFragment()
    }
}

