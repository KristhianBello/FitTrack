package com.example.fittrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton

/**
 * Fragment principal de la pantalla de inicio
 * Muestra resumen del progreso y acciones principales
 */
class HomeFragment2 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar botón principal de entrenamiento
        setupStartButton(view)

        // Configurar tarjetas clicables
        setupCards(view)
    }

    private fun setupStartButton(view: View) {
        val startButton = view.findViewById<MaterialButton>(R.id.btnIniciarEntrenamiento)
        startButton?.setOnClickListener {
            showToast("Función de entrenamiento próximamente disponible")
        }
    }

    private fun setupCards(view: View) {
        // Configurar tarjeta de progreso semanal
        val progressCard = view.findViewById<CardView>(R.id.progressCard)
        progressCard?.setOnClickListener {
            showToast("Detalles de progreso próximamente disponibles")
        }

        // Configurar tarjeta de rutina recomendada
        val routineCard = view.findViewById<CardView>(R.id.routineCard)
        routineCard?.setOnClickListener {
            showToast("Ver detalles de rutina próximamente disponible")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        /**
         * Factory method para crear una nueva instancia del fragment
         */
        @JvmStatic
        fun newInstance() = HomeFragment2()
    }
}
