package com.example.fittrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class HomeFragment2 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar botón de entrenamiento
        val startButton = view.findViewById<MaterialButton>(R.id.btnIniciarEntrenamiento)
        startButton?.setOnClickListener {
            // Aquí se puede agregar la funcionalidad para iniciar entrenamiento
            // Por ejemplo: navegar a la pantalla de rutina
        }
    }
}
