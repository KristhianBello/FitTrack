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
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class HomeFragment2 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.fragment_home2, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val greeting = view.findViewById<TextView>(R.id.tvGreeting)
        val progress = view.findViewById<TextView>(R.id.tvProgressFraction)
        view.findViewById<MaterialButton>(R.id.btnIniciarEntrenamiento)?.setOnClickListener {
            Toast.makeText(context, "Función de entrenamiento próximamente disponible", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<CardView>(R.id.progressCard)?.setOnClickListener {
            Toast.makeText(context, "Detalles de progreso próximamente disponibles", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<CardView>(R.id.routineCard)?.setOnClickListener {
            Toast.makeText(context, "Ver detalles de rutina próximamente disponible", Toast.LENGTH_SHORT).show()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    FitTrackSdk.session.profile.collect { profile ->
                        val name = profile?.name ?: "Atleta"
                        greeting.text = "¡Hola, $name!"
                    }
                }
                launch {
                    FitTrackSdk.session.routines.collect { routines ->
                        progress.text = routines.size.toString()
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment2()
    }
}
