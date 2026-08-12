package com.example.fittrack

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fittrack.shared.FitTrackSdk
import com.example.fittrack.shared.domain.UserProfile
import kotlinx.coroutines.launch
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

    private var profile = UserProfile(
        id = "",
        name = "Atleta",
        email = FitTrackSdk.auth.getCurrentUserEmail(),
    )

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
        setupUserData()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                FitTrackSdk.session.profile.collect { loaded ->
                    if (loaded != null) {
                        profile = loaded
                        setupUserData()
                    }
                }
            }
        }

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
        tvUserName.text = profile.name

        val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val memberDate = dateFormat.format(Date())
        tvMemberSince.text = "Miembro desde $memberDate"

        tvTotalWorkouts.text = profile.totalWorkouts.toString()
        tvStreakRecord.text = profile.streakRecord.toString()
        tvActiveHours.text = profile.activeHoursLabel
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

        llCerrarSesion.setOnClickListener {
            showToast("Cerrando sesión...")
            viewLifecycleOwner.lifecycleScope.launch {
                FitTrackSdk.signOut()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // Métodos para actualizar estadísticas (útiles para futuras implementaciones)
    fun updateWorkoutCount(newCount: Int) {
        profile = profile.copy(totalWorkouts = newCount)
        if (::tvTotalWorkouts.isInitialized) {
            tvTotalWorkouts.text = profile.totalWorkouts.toString()
        }
    }

    fun updateStreakRecord(newStreak: Int) {
        if (newStreak > profile.streakRecord) {
            profile = profile.copy(streakRecord = newStreak)
            if (::tvStreakRecord.isInitialized) {
                tvStreakRecord.text = profile.streakRecord.toString()
            }
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