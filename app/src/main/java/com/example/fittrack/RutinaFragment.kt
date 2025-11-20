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
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Fragment para gestión de rutinas de ejercicio
 * Incluye pestañas para "Mis Rutinas" e "Historial"
 */
class RutinaFragment : Fragment() {

    // UI Components
    private lateinit var tabMisRutinas: LinearLayout
    private lateinit var tabHistorial: LinearLayout
    private lateinit var textMisRutinas: TextView
    private lateinit var textHistorial: TextView
    private lateinit var lineMisRutinas: View
    private lateinit var lineHistorial: View
    private lateinit var scrollMisRutinas: ScrollView
    private lateinit var scrollHistorial: ScrollView
    private lateinit var recyclerViewRutinas: RecyclerView
    private lateinit var fabAddRutina: FloatingActionButton
    private lateinit var debugFab: TextView

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

        // Configurar FAB
        setupFAB()

        // Configurar tarjetas de rutinas
        setupRoutineCards(view)

        // Mostrar pestaña inicial (Mis Rutinas)
        showRutinasTab()
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
        recyclerViewRutinas = view.findViewById(R.id.recyclerViewRutinas)
        fabAddRutina = view.findViewById(R.id.fabAddRutina)
        debugFab = view.findViewById(R.id.debugFab)
    }

    private fun setupTabs() {
        tabMisRutinas.setOnClickListener {
            showRutinasTab()
        }

        tabHistorial.setOnClickListener {
            showHistorialTab()
        }
    }

    private fun setupFAB() {
        fabAddRutina.setOnClickListener {
            showToast("Crear nueva rutina próximamente disponible")
        }

        debugFab.setOnClickListener {
            showToast("Agregar rutina (en desarrollo)")
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
                when (index) {
                    0 -> showToast("Rutina 'Full Body Día 1' próximamente disponible")
                    1 -> showToast("Rutina 'Pecho y Tríceps' próximamente disponible")
                    2 -> showToast("Rutina 'Espalda y Bíceps' próximamente disponible")
                    3 -> showToast("Rutina 'Piernas Completas' próximamente disponible")
                    else -> showToast("Rutina próximamente disponible")
                }
            }
        }
    }

    private fun showRutinasTab() {
        isRutinasTabActive = true

        // Actualizar estilos de pestañas
        textMisRutinas.setTextColor(resources.getColor(R.color.fit_glow, null))
        textMisRutinas.alpha = 1f
        lineMisRutinas.visibility = View.VISIBLE

        textHistorial.setTextColor(resources.getColor(R.color.track_night, null))
        textHistorial.alpha = 0.6f
        lineHistorial.visibility = View.INVISIBLE

        // Mostrar contenido correspondiente
        scrollMisRutinas.visibility = View.VISIBLE
        scrollHistorial.visibility = View.GONE
        recyclerViewRutinas.visibility = View.GONE
    }

    private fun showHistorialTab() {
        isRutinasTabActive = false

        // Actualizar estilos de pestañas
        textHistorial.setTextColor(resources.getColor(R.color.fit_glow, null))
        textHistorial.alpha = 1f
        lineHistorial.visibility = View.VISIBLE

        textMisRutinas.setTextColor(resources.getColor(R.color.track_night, null))
        textMisRutinas.alpha = 0.6f
        lineMisRutinas.visibility = View.INVISIBLE

        // Mostrar contenido correspondiente
        scrollHistorial.visibility = View.VISIBLE
        scrollMisRutinas.visibility = View.GONE
        recyclerViewRutinas.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        /**
         * Factory method para crear una nueva instancia del fragment
         */
        @JvmStatic
        fun newInstance() = RutinaFragment()
    }
}