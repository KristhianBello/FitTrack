package com.example.fittrack

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.fittrack.shared.FitTrackSdk
import com.example.fittrack.shared.domain.SensorKinds
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class SensoresFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var sensorProximidad: Sensor? = null
    private var sensorLuz: Sensor? = null
    private var sensorAcelerometro: Sensor? = null

    private lateinit var progressProximidad: ProgressBar
    private lateinit var textProximidad: TextView
    private lateinit var progressLuz: ProgressBar
    private lateinit var textLuz: TextView
    private lateinit var textLuzDesc: TextView
    private lateinit var progressAcelX: ProgressBar
    private lateinit var textAcelX: TextView
    private lateinit var progressAcelY: ProgressBar
    private lateinit var textAcelY: TextView
    private lateinit var progressAcelZ: ProgressBar
    private lateinit var textAcelZ: TextView
    private lateinit var textMagnitud: TextView
    private lateinit var cardShake: CardView
    private lateinit var shakeContainer: android.widget.LinearLayout
    private lateinit var textShakeTitle: TextView
    private lateinit var textShake: TextView

    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var lastShakeTime = 0L
    private var isShaking = false

    companion object {
        private const val MAX_ACELEROMETRO = 20f
        private const val SHAKE_THRESHOLD = 15f
        private const val SHAKE_COOLDOWN = 500L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sensores, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        initializeSensors()
    }

    private fun initializeViews(view: View) {
        progressProximidad = view.findViewById(R.id.progressProximidad)
        textProximidad = view.findViewById(R.id.textProximidad)
        progressLuz = view.findViewById(R.id.progressLuz)
        textLuz = view.findViewById(R.id.textLuz)
        textLuzDesc = view.findViewById(R.id.textLuzDesc)
        progressAcelX = view.findViewById(R.id.progressAcelX)
        textAcelX = view.findViewById(R.id.textAcelX)
        progressAcelY = view.findViewById(R.id.progressAcelY)
        textAcelY = view.findViewById(R.id.textAcelY)
        progressAcelZ = view.findViewById(R.id.progressAcelZ)
        textAcelZ = view.findViewById(R.id.textAcelZ)
        textMagnitud = view.findViewById(R.id.textMagnitud)
        cardShake = view.findViewById(R.id.cardShake)
        shakeContainer = view.findViewById(R.id.shakeContainer)
        textShakeTitle = view.findViewById(R.id.textShakeTitle)
        textShake = view.findViewById(R.id.textShake)
    }

    private fun initializeSensors() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorProximidad = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        sensorLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        sensorAcelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorProximidad?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        sensorLuz?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        sensorAcelerometro?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_PROXIMITY -> handleProximity(it)
                Sensor.TYPE_LIGHT -> handleLight(it)
                Sensor.TYPE_ACCELEROMETER -> handleAccelerometer(it)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun handleProximity(event: SensorEvent) {
        val distance = event.values[0]
        val maxRange = event.sensor.maximumRange
        val progress = ((maxRange - distance) / maxRange * 100).toInt().coerceIn(0, 100)
        progressProximidad.progress = progress
        textProximidad.text = "Distancia: ${"%.2f".format(distance)} cm"
    }

    private fun handleLight(event: SensorEvent) {
        val light = event.values[0]
        val progress = (light / 5000f * 100).toInt().coerceIn(0, 100)
        progressLuz.progress = progress
        textLuz.text = "Intensidad: ${"%.1f".format(light)} lux"
        val description = when {
            light < 50 -> "🌙 Muy oscuro"
            light < 200 -> "🌑 Oscuro"
            light < 500 -> "🌤️ Débil"
            light < 2000 -> "☀️ Normal"
            else -> "☀️☀️ Muy brillante"
        }
        textLuzDesc.text = description
    }

    private fun handleAccelerometer(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        updateAccelerometerBar(progressAcelX, textAcelX, x)
        updateAccelerometerBar(progressAcelY, textAcelY, y)
        updateAccelerometerBar(progressAcelZ, textAcelZ, z)

        val magnitude = sqrt(x * x + y * y + z * z)
        textMagnitud.text = "Magnitud: ${"%.2f".format(magnitude)} m/s²"

        detectShake(x, y, z)

        lastX = x
        lastY = y
        lastZ = z
    }

    private fun updateAccelerometerBar(progressBar: ProgressBar, textView: TextView, value: Float) {
        val normalized = ((value + MAX_ACELEROMETRO) / (2 * MAX_ACELEROMETRO) * 100)
            .toInt()
            .coerceIn(0, 100)
        progressBar.progress = normalized
        textView.text = "${"%.2f".format(value)} m/s²"
    }

    private fun detectShake(x: Float, y: Float, z: Float) {
        val deltaX = x - lastX
        val deltaY = y - lastY
        val deltaZ = z - lastZ
        val acceleration = sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)
        val currentTime = System.currentTimeMillis()

        android.util.Log.d("SensoresFragment", "Aceleración: $acceleration, Umbral: $SHAKE_THRESHOLD")

        if (acceleration > SHAKE_THRESHOLD && (currentTime - lastShakeTime) > SHAKE_COOLDOWN) {
            android.util.Log.d("SensoresFragment", "¡SACUDIDA DETECTADA! Aceleración: $acceleration")
            isShaking = true
            lastShakeTime = currentTime
            persistShake(acceleration)

            try {
                // Cambiar el fondo del contenedor interno a ROJO
                shakeContainer.setBackgroundColor(FitTrackColor.shakeAlertBackground)

                // Cambiar textos a BLANCO con tamaño más grande para asegurar visibilidad
                textShakeTitle.setTextColor(FitTrackColor.white)
                textShakeTitle.textSize = 20f

                textShake.text = "¡SACUDIDA DETECTADA! 🚀"
                textShake.setTextColor(FitTrackColor.white)
                textShake.textSize = 16f

                android.util.Log.d("SensoresFragment", "Color rojo aplicado")
            } catch (e: Exception) {
                android.util.Log.e("SensoresFragment", "Error al cambiar color: ${e.message}")
            }

            cardShake.removeCallbacks(resetShakeRunnable)
            cardShake.postDelayed(resetShakeRunnable, 500)
        }
    }

    private fun persistShake(acceleration: Float) {
        viewLifecycleOwner.lifecycleScope.launch {
            FitTrackSdk.session.recordSensor(
                kind = SensorKinds.SHAKE,
                value = acceleration.toDouble(),
                unit = "m/s²",
            )
        }
    }

    private val resetShakeRunnable = Runnable {
        isShaking = false
        try {
            // Resetear el fondo del contenedor a BLANCO
            shakeContainer.setBackgroundColor(FitTrackColor.white)

            // Resetear textos a sus colores y tamaños originales
            textShakeTitle.setTextColor(FitTrackColor.black)
            textShakeTitle.textSize = 18f

            textShake.text = "Agita tu dispositivo..."
            textShake.setTextColor(FitTrackColor.shakeIdleBodyText)
            textShake.textSize = 14f

            android.util.Log.d("SensoresFragment", "Color reseteado a blanco")
        } catch (e: Exception) {
            android.util.Log.e("SensoresFragment", "Error resetting shake: ${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        sensorProximidad?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        sensorLuz?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        sensorAcelerometro?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}

