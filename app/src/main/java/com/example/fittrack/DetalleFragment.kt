package com.example.fittrack

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.fittrack.shared.domain.WeightRecord
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment para el control de peso
 * Muestra métricas clave, gráfico de evolución y últimos registros
 */
class DetalleFragment : Fragment() {

    private lateinit var tvPesoActual: TextView
    private lateinit var tvPesoMeta: TextView
    private lateinit var btnRegistrarPeso: Button
    private lateinit var cardIngresoPeso: CardView
    private lateinit var etNuevoPeso: TextInputEditText
    private lateinit var btnCancelarIngreso: Button
    private lateinit var btnGuardarPeso: Button

    // TextViews de registros
    private lateinit var tvRegistro1Fecha: TextView
    private lateinit var tvRegistro1Peso: TextView
    private lateinit var tvRegistro2Fecha: TextView
    private lateinit var tvRegistro2Peso: TextView
    private lateinit var tvRegistro3Fecha: TextView
    private lateinit var tvRegistro3Peso: TextView
    private lateinit var tvRegistro4Fecha: TextView
    private lateinit var tvRegistro4Peso: TextView

    // Botones de editar
    private lateinit var btnEditarRegistro1: Button
    private lateinit var btnEditarRegistro2: Button
    private lateinit var btnEditarRegistro3: Button
    private lateinit var btnEditarRegistro4: Button

    // Datos de ejemplo (en una app real, esto vendría de una base de datos)
    private var pesoActual = 85.5f
    private var pesoMeta = 80.0f

    // Lista de registros (fecha, peso)
    private val registros = mutableListOf(
        WeightRecord("20 Nov 2024", 85.5f),
        WeightRecord("18 Nov 2024", 85.8f),
        WeightRecord("15 Nov 2024", 86.2f),
        WeightRecord("12 Nov 2024", 86.5f)
    )

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
        cardIngresoPeso = view.findViewById(R.id.card_ingreso_peso)
        etNuevoPeso = view.findViewById(R.id.et_nuevo_peso)
        btnCancelarIngreso = view.findViewById(R.id.btn_cancelar_ingreso)
        btnGuardarPeso = view.findViewById(R.id.btn_guardar_peso)

        // Registros
        tvRegistro1Fecha = view.findViewById(R.id.tv_registro1_fecha)
        tvRegistro1Peso = view.findViewById(R.id.tv_registro1_peso)
        tvRegistro2Fecha = view.findViewById(R.id.tv_registro2_fecha)
        tvRegistro2Peso = view.findViewById(R.id.tv_registro2_peso)
        tvRegistro3Fecha = view.findViewById(R.id.tv_registro3_fecha)
        tvRegistro3Peso = view.findViewById(R.id.tv_registro3_peso)
        tvRegistro4Fecha = view.findViewById(R.id.tv_registro4_fecha)
        tvRegistro4Peso = view.findViewById(R.id.tv_registro4_peso)

        // Botones de editar
        btnEditarRegistro1 = view.findViewById(R.id.btn_editar_registro1)
        btnEditarRegistro2 = view.findViewById(R.id.btn_editar_registro2)
        btnEditarRegistro3 = view.findViewById(R.id.btn_editar_registro3)
        btnEditarRegistro4 = view.findViewById(R.id.btn_editar_registro4)
    }

    private fun setupData() {
        // Mostrar peso actual
        tvPesoActual.text = String.format("%.1f kg", pesoActual)

        // Mostrar meta
        tvPesoMeta.text = String.format("%.1f kg", pesoMeta)

        // Actualizar registros en la UI
        actualizarRegistrosUI()
    }

    private fun setupListeners() {
        // Botón para mostrar tarjeta de ingreso
        btnRegistrarPeso.setOnClickListener {
            mostrarTarjetaIngreso()
        }

        // Botón cancelar
        btnCancelarIngreso.setOnClickListener {
            ocultarTarjetaIngreso()
        }

        // Botón guardar
        btnGuardarPeso.setOnClickListener {
            guardarNuevoPeso()
        }

        // Botones de editar registros
        btnEditarRegistro1.setOnClickListener {
            editarRegistro(0)
        }

        btnEditarRegistro2.setOnClickListener {
            editarRegistro(1)
        }

        btnEditarRegistro3.setOnClickListener {
            editarRegistro(2)
        }

        btnEditarRegistro4.setOnClickListener {
            editarRegistro(3)
        }
    }

    private fun mostrarTarjetaIngreso() {
        cardIngresoPeso.visibility = View.VISIBLE
        etNuevoPeso.requestFocus()
    }

    private fun ocultarTarjetaIngreso() {
        cardIngresoPeso.visibility = View.GONE
        etNuevoPeso.text?.clear()
    }

    private fun guardarNuevoPeso() {
        val pesoTexto = etNuevoPeso.text.toString()

        if (pesoTexto.isEmpty()) {
            Toast.makeText(context, "Por favor ingresa un peso", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val nuevoPeso = pesoTexto.toFloat()

            if (nuevoPeso <= 0 || nuevoPeso > 300) {
                Toast.makeText(context, "Por favor ingresa un peso válido (1-300 kg)", Toast.LENGTH_SHORT).show()
                return
            }

            // Actualizar peso actual
            pesoActual = nuevoPeso
            tvPesoActual.text = String.format("%.1f kg", pesoActual)

            // Agregar nuevo registro al inicio de la lista
            val fechaActual = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES")).format(Date())
            registros.add(0, WeightRecord(fechaActual, nuevoPeso))

            // Mantener solo los últimos 4 registros
            if (registros.size > 4) {
                registros.removeAt(4)
            }

            // Actualizar UI de registros
            actualizarRegistrosUI()

            // Ocultar tarjeta
            ocultarTarjetaIngreso()

            Toast.makeText(context, "Peso registrado: $nuevoPeso kg", Toast.LENGTH_SHORT).show()

        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Por favor ingresa un número válido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editarRegistro(posicion: Int) {
        if (posicion >= registros.size) {
            Toast.makeText(context, "Registro no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val registro = registros[posicion]

        // Mostrar tarjeta con el peso actual
        mostrarTarjetaIngreso()
        etNuevoPeso.setText(registro.kilograms.toString())
        etNuevoPeso.selectAll()

        // Cambiar el comportamiento del botón guardar temporalmente
        btnGuardarPeso.setOnClickListener {
            actualizarRegistroExistente(posicion)
        }
    }

    private fun actualizarRegistroExistente(posicion: Int) {
        val pesoTexto = etNuevoPeso.text.toString()

        if (pesoTexto.isEmpty()) {
            Toast.makeText(context, "Por favor ingresa un peso", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val nuevoPeso = pesoTexto.toFloat()

            if (nuevoPeso <= 0 || nuevoPeso > 300) {
                Toast.makeText(context, "Por favor ingresa un peso válido (1-300 kg)", Toast.LENGTH_SHORT).show()
                return
            }

            // Actualizar el registro
            val fechaActual = registros[posicion].dateLabel
            registros[posicion] = WeightRecord(fechaActual, nuevoPeso)

            // Si es el primer registro, actualizar también el peso actual
            if (posicion == 0) {
                pesoActual = nuevoPeso
                tvPesoActual.text = String.format("%.1f kg", pesoActual)
            }

            // Actualizar UI de registros
            actualizarRegistrosUI()

            // Ocultar tarjeta
            ocultarTarjetaIngreso()

            // Restaurar el listener normal del botón guardar
            btnGuardarPeso.setOnClickListener {
                guardarNuevoPeso()
            }

            Toast.makeText(context, "Registro actualizado: $nuevoPeso kg", Toast.LENGTH_SHORT).show()

        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Por favor ingresa un número válido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarRegistrosUI() {
        // Actualizar registro 1
        if (registros.size > 0) {
            tvRegistro1Fecha.text = registros[0].dateLabel
            tvRegistro1Peso.text = String.format("%.1f kg", registros[0].kilograms)
        }

        if (registros.size > 1) {
            tvRegistro2Fecha.text = registros[1].dateLabel
            tvRegistro2Peso.text = String.format("%.1f kg", registros[1].kilograms)
        }

        if (registros.size > 2) {
            tvRegistro3Fecha.text = registros[2].dateLabel
            tvRegistro3Peso.text = String.format("%.1f kg", registros[2].kilograms)
        }

        if (registros.size > 3) {
            tvRegistro4Fecha.text = registros[3].dateLabel
            tvRegistro4Peso.text = String.format("%.1f kg", registros[3].kilograms)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DetalleFragment()
    }
}

