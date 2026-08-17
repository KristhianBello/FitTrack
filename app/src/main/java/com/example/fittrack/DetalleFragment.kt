package com.example.fittrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fittrack.shared.FitTrackSdk
import com.example.fittrack.shared.domain.FitnessValidator
import com.example.fittrack.shared.domain.WeightRecord
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class DetalleFragment : Fragment() {

    private lateinit var tvPesoActual: TextView
    private lateinit var tvPesoMeta: TextView
    private lateinit var btnRegistrarPeso: Button
    private lateinit var cardIngresoPeso: CardView
    private lateinit var etNuevoPeso: TextInputEditText
    private lateinit var btnCancelarIngreso: Button
    private lateinit var btnGuardarPeso: Button
    private lateinit var tvRegistro1Fecha: TextView
    private lateinit var tvRegistro1Peso: TextView
    private lateinit var tvRegistro2Fecha: TextView
    private lateinit var tvRegistro2Peso: TextView
    private lateinit var tvRegistro3Fecha: TextView
    private lateinit var tvRegistro3Peso: TextView
    private lateinit var tvRegistro4Fecha: TextView
    private lateinit var tvRegistro4Peso: TextView
    private lateinit var btnEditarRegistro1: Button
    private lateinit var btnEditarRegistro2: Button
    private lateinit var btnEditarRegistro3: Button
    private lateinit var btnEditarRegistro4: Button

    private var editingId: String? = null
    private var visibleRecords: List<WeightRecord> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.fragment_detalle, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupListeners()
        observeSession()
    }

    private fun initViews(view: View) {
        tvPesoActual = view.findViewById(R.id.tv_peso_actual)
        tvPesoMeta = view.findViewById(R.id.tv_peso_meta)
        btnRegistrarPeso = view.findViewById(R.id.btn_registrar_peso)
        cardIngresoPeso = view.findViewById(R.id.card_ingreso_peso)
        etNuevoPeso = view.findViewById(R.id.et_nuevo_peso)
        btnCancelarIngreso = view.findViewById(R.id.btn_cancelar_ingreso)
        btnGuardarPeso = view.findViewById(R.id.btn_guardar_peso)
        tvRegistro1Fecha = view.findViewById(R.id.tv_registro1_fecha)
        tvRegistro1Peso = view.findViewById(R.id.tv_registro1_peso)
        tvRegistro2Fecha = view.findViewById(R.id.tv_registro2_fecha)
        tvRegistro2Peso = view.findViewById(R.id.tv_registro2_peso)
        tvRegistro3Fecha = view.findViewById(R.id.tv_registro3_fecha)
        tvRegistro3Peso = view.findViewById(R.id.tv_registro3_peso)
        tvRegistro4Fecha = view.findViewById(R.id.tv_registro4_fecha)
        tvRegistro4Peso = view.findViewById(R.id.tv_registro4_peso)
        btnEditarRegistro1 = view.findViewById(R.id.btn_editar_registro1)
        btnEditarRegistro2 = view.findViewById(R.id.btn_editar_registro2)
        btnEditarRegistro3 = view.findViewById(R.id.btn_editar_registro3)
        btnEditarRegistro4 = view.findViewById(R.id.btn_editar_registro4)
    }

    private fun observeSession() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    FitTrackSdk.session.weightRecords.collect { records ->
                        visibleRecords = records.take(4)
                        bindRecords(records)
                    }
                }
                launch {
                    FitTrackSdk.session.profile.collect { profile ->
                        val goal = profile?.goalKg ?: 80f
                        tvPesoMeta.text = String.format("%.1f kg", goal)
                    }
                }
            }
        }
    }

    private fun bindRecords(records: List<WeightRecord>) {
        val latest = records.firstOrNull()
        tvPesoActual.text = latest?.let { String.format("%.1f kg", it.kilograms) } ?: "-- kg"
        bindSlot(0, tvRegistro1Fecha, tvRegistro1Peso)
        bindSlot(1, tvRegistro2Fecha, tvRegistro2Peso)
        bindSlot(2, tvRegistro3Fecha, tvRegistro3Peso)
        bindSlot(3, tvRegistro4Fecha, tvRegistro4Peso)
    }

    private fun bindSlot(index: Int, fecha: TextView, peso: TextView) {
        val record = visibleRecords.getOrNull(index)
        fecha.text = record?.dateLabel ?: "—"
        peso.text = record?.let { String.format("%.1f kg", it.kilograms) } ?: "—"
    }

    private fun setupListeners() {
        btnRegistrarPeso.setOnClickListener {
            editingId = null
            mostrarTarjetaIngreso()
        }
        btnCancelarIngreso.setOnClickListener {
            editingId = null
            ocultarTarjetaIngreso()
        }
        btnGuardarPeso.setOnClickListener { guardarPeso() }
        btnEditarRegistro1.setOnClickListener { editarRegistro(0) }
        btnEditarRegistro2.setOnClickListener { editarRegistro(1) }
        btnEditarRegistro3.setOnClickListener { editarRegistro(2) }
        btnEditarRegistro4.setOnClickListener { editarRegistro(3) }
    }

    private fun mostrarTarjetaIngreso() {
        cardIngresoPeso.visibility = View.VISIBLE
        etNuevoPeso.requestFocus()
    }

    private fun ocultarTarjetaIngreso() {
        cardIngresoPeso.visibility = View.GONE
        etNuevoPeso.text?.clear()
    }

    private fun editarRegistro(posicion: Int) {
        val record = visibleRecords.getOrNull(posicion)
        if (record == null) {
            Toast.makeText(context, "Registro no encontrado", Toast.LENGTH_SHORT).show()
            return
        }
        editingId = record.id
        mostrarTarjetaIngreso()
        etNuevoPeso.setText(record.kilograms.toString())
        etNuevoPeso.selectAll()
    }

    private fun guardarPeso() {
        val pesoTexto = etNuevoPeso.text.toString()
        if (pesoTexto.isEmpty()) {
            Toast.makeText(context, "Por favor ingresa un peso", Toast.LENGTH_SHORT).show()
            return
        }
        val nuevoPeso = pesoTexto.toFloatOrNull()
        if (nuevoPeso == null) {
            Toast.makeText(context, "Por favor ingresa un número válido", Toast.LENGTH_SHORT).show()
            return
        }
        val error = FitnessValidator.weightError(nuevoPeso)
        if (error != null) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            val result = editingId?.let { FitTrackSdk.session.updateWeight(it, nuevoPeso) }
                ?: FitTrackSdk.session.addWeight(nuevoPeso)
            result.onSuccess {
                editingId = null
                ocultarTarjetaIngreso()
                Toast.makeText(context, "Peso guardado: $nuevoPeso kg", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(context, it.message ?: "No se pudo guardar el peso", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DetalleFragment()
    }
}
