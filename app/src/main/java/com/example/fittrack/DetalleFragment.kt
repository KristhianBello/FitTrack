package com.example.fittrack

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

/**
 * Fragment para el control de peso
 * Muestra métricas clave, gráfico de evolución y últimos registros
 */
class DetalleFragment : Fragment() {

    private lateinit var tvPesoActual: TextView
    private lateinit var tvPesoMeta: TextView
    private lateinit var btnRegistrarPeso: Button

    // Datos de ejemplo
    private var pesoActual = 85.5f
    private var pesoMeta = 80.0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detalle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        initViews(view)

        // Configurar datos
        setupData()

        // Configurar listeners
        setupListeners()
    }

    private fun initViews(view: View) {
        tvPesoActual = view.findViewById(R.id.tv_peso_actual)
        tvPesoMeta = view.findViewById(R.id.tv_peso_meta)
        btnRegistrarPeso = view.findViewById(R.id.btn_registrar_peso)
    }

    private fun setupData() {
        // Mostrar peso actual en Cian Brillante
        tvPesoActual.text = String.format("%.1f kg", pesoActual)

        // Mostrar meta en color suave
        tvPesoMeta.text = String.format("%.1f kg", pesoMeta)
    }

    private fun setupListeners() {
        btnRegistrarPeso.setOnClickListener {
            // Acción para registrar peso
            mostrarDialogoRegistrarPeso()
        }
    }

    private fun mostrarDialogoRegistrarPeso() {
        // Por ahora, solo mostrar
        // En una implementación real, aquí iría un diálogo para ingresar el peso
        Toast.makeText(
            context,
            "Función de registro de peso (próximamente)",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Método para actualizar el peso (será útil cuando se implemente mas adelante)
    private fun actualizarPeso(nuevoPeso: Float) {
        pesoActual = nuevoPeso
        tvPesoActual.text = String.format("%.1f kg", pesoActual)
    }

    companion object {
        /**
         * Factory method para crear una nueva instancia del fragment
         */
        @JvmStatic
        fun newInstance() = DetalleFragment()
    }
}