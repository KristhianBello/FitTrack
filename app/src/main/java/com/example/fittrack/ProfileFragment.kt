package com.example.fittrack

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment para el perfil de usuario
 * Muestra avatar, estadísticas personales y opciones de configuración
 */
class ProfileFragment : Fragment() {

    private lateinit var tvUserName: TextView
    private lateinit var tvMemberSince: TextView
    private lateinit var tvTotalWorkouts: TextView
    private lateinit var tvStreakRecord: TextView
    private lateinit var tvActiveHours: TextView
    private lateinit var ivAvatar: ImageView

    // Layouts clicables
    private lateinit var llMetaDatos: LinearLayout
    private lateinit var llUnidades: LinearLayout
    private lateinit var llNotificaciones: LinearLayout
    private lateinit var llCerrarSesion: LinearLayout

    // Datos del usuario (en una app real vendrían de SharedPreferences o base de datos)
    private val userName = "Pako"
    private val totalWorkouts = 85
    private val streakRecord = 12
    private val activeHours = "75 hrs"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        initViews(view)

        // Configurar datos del usuario
        setupUserData()

        // Configurar listeners para las opciones
        setupOptionListeners()
    }

    private fun initViews(view: View) {
        // TextViews de información
        tvUserName = view.findViewById(R.id.tv_user_name)
        tvMemberSince = view.findViewById(R.id.tv_member_since)
        tvTotalWorkouts = view.findViewById(R.id.tv_total_workouts)
        tvStreakRecord = view.findViewById(R.id.tv_streak_record)
        tvActiveHours = view.findViewById(R.id.tv_active_hours)
        ivAvatar = view.findViewById(R.id.iv_avatar)

        // Layouts de opciones
        llMetaDatos = view.findViewById(R.id.ll_meta_datos)
        llUnidades = view.findViewById(R.id.ll_unidades)
        llNotificaciones = view.findViewById(R.id.ll_notificaciones)
        llCerrarSesion = view.findViewById(R.id.ll_cerrar_sesion)
    }

    private fun setupUserData() {
        // Configurar nombre de usuario
        tvUserName.text = userName

        // Configurar fecha de miembro (usando fecha actual como ejemplo)
        val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val memberDate = dateFormat.format(Date())
        tvMemberSince.text = "Miembro desde $memberDate"

        // Configurar estadísticas con valores en Verde Neón
        tvTotalWorkouts.text = totalWorkouts.toString()
        tvStreakRecord.text = streakRecord.toString()
        tvActiveHours.text = activeHours
    }

    private fun setupOptionListeners() {
        // Mi Meta y Datos
        llMetaDatos.setOnClickListener {
            showToast("Configurar Meta y Datos Personales")
            // En una implementación real, abriría un diálogo o nueva actividad
        }

        // Ajustes de Unidades
        llUnidades.setOnClickListener {
            showToast("Cambiar Unidades (Kg/Lb)")
            // En una implementación real, abriría configuración de unidades
        }

        // Notificaciones y Alertas
        llNotificaciones.setOnClickListener {
            showToast("Configurar Notificaciones")
            // En una implementación real, abriría configuración de notificaciones
        }

        // Cerrar Sesión
        llCerrarSesion.setOnClickListener {
            showToast("Cerrando sesión...")
            // En una implementación real, cerraría la sesión del usuario
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // Métodos para actualizar estadísticas (útiles para futuras implementaciones)
    fun updateWorkoutCount(newCount: Int) {
        totalWorkouts + newCount
        tvTotalWorkouts.text = totalWorkouts.toString()
    }

    fun updateStreakRecord(newStreak: Int) {
        if (newStreak > streakRecord) {
            tvStreakRecord.text = newStreak.toString()
        }
    }

    companion object {
        /**
         * Factory method para crear una nueva instancia del fragment
         */
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}