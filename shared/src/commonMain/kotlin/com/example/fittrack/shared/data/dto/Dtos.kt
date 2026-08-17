package com.example.fittrack.shared.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val name: String,
    val email: String,
    @SerialName("peso_meta") val pesoMeta: Double? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val altura: Double? = null,
    @SerialName("fecha_nacimiento") val fechaNacimiento: String? = null,
    val genero: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class ProfilePatchDto(
    val altura: Double,
    @SerialName("peso_meta") val pesoMeta: Double,
    @SerialName("fecha_nacimiento") val fechaNacimiento: String,
    val genero: String,
)

@Serializable
data class WeightRecordDto(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    val peso: Double,
    val fecha: String,
    val notas: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class WeightPatchDto(
    val peso: Double,
)

@Serializable
data class RoutineDto(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    val nombre: String,
    val duracion: Int,
    @SerialName("cantidad_ejercicios") val cantidadEjercicios: Int,
    @SerialName("frecuencia_semanal") val frecuenciaSemanal: Int,
    val categoria: String,
    val descripcion: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class ExerciseDto(
    val id: String,
    val nombre: String,
    val descripcion: String? = null,
    @SerialName("grupo_muscular") val grupoMuscular: String? = null,
    val nivel: String? = null,
    val tipo: String? = null,
)

@Serializable
data class RoutineExerciseInsertDto(
    @SerialName("routine_id") val routineId: String,
    @SerialName("exercise_id") val exerciseId: String,
    val orden: Int,
    val series: Int,
    val repeticiones: Int,
)

@Serializable
data class RoutineExerciseDto(
    val id: String? = null,
    @SerialName("routine_id") val routineId: String,
    @SerialName("exercise_id") val exerciseId: String,
    val orden: Int,
    val series: Int,
    val repeticiones: Int,
)

@Serializable
data class WorkoutHistoryDto(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("routine_id") val routineId: String? = null,
    val fecha: String? = null,
    @SerialName("duracion_real") val duracionReal: Int? = null,
    @SerialName("calorias_quemadas") val caloriasQuemadas: Int? = null,
    val notas: String? = null,
    val completed: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
)
