package com.example.fittrack.shared.data

import com.example.fittrack.shared.config.SupabaseConfig
import com.example.fittrack.shared.data.dto.ExerciseDto
import com.example.fittrack.shared.data.schema.DbTables
import com.example.fittrack.shared.domain.Exercise
import com.example.fittrack.shared.logError
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class ExerciseRepository {
    private var cache: List<Exercise> = emptyList()

    /**
     * Catálogo de solo lectura. A diferencia de [RoutineRepository], si la red falla
     * NO se inventan ejercicios: se devuelve la última copia en caché (o vacía),
     * porque un ejercicio inexistente en la base rompería el insert en `routine_exercises`.
     */
    suspend fun list(): List<Exercise> {
        return try {
            val remote = SupabaseConfig.client.from(DbTables.EXERCISES)
                .select {
                    order("nombre", Order.ASCENDING)
                }
                .decodeList<ExerciseDto>()
                .map { it.toDomain() }
            cache = remote
            remote
        } catch (e: Exception) {
            logError(TAG, "Error al listar ejercicios; se usa cache local", e)
            cache
        }
    }

    private fun ExerciseDto.toDomain() = Exercise(
        id = id,
        name = nombre,
        description = descripcion,
        muscleGroup = grupoMuscular,
        level = nivel,
        type = tipo,
    )

    private companion object {
        const val TAG = "ExerciseRepository"
    }
}
