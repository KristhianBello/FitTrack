package com.example.fittrack

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ScrollView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
            // Aquí se puede agregar la funcionalidad para crear una nueva rutina
            // Por ejemplo: mostrar un diálogo o navegar a una pantalla de creación
            android.util.Log.d("RutinaFragment", "FAB Principal clickeado")
        }

        debugFab.setOnClickListener {
            // FAB de debug para asegurar que funciona
            android.util.Log.d("RutinaFragment", "FAB Debug clickeado")
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
}