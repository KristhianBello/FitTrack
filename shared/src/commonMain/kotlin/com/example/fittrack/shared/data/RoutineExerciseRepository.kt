package com.example.fittrack.shared.data

import com.example.fittrack.shared.config.SupabaseConfig
import com.example.fittrack.shared.data.dto.RoutineExerciseDto
import com.example.fittrack.shared.data.dto.RoutineExerciseInsertDto
import com.example.fittrack.shared.data.schema.DbTables
import com.example.fittrack.shared.domain.Exercise
import com.example.fittrack.shared.domain.RoutineExercise
import com.example.fittrack.shared.logError
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class RoutineExerciseRepository {

    private companion object {
        const val TAG = "RoutineExerciseRepository"
        const val DEFAULT_SETS = 3
        const val DEFAULT_REPS = 10
    }

    suspend fun listForRoutine(routineId: String, exercises: List<Exercise>): List<RoutineExercise> {
        return try {
            SupabaseConfig.client.from(DbTables.ROUTINE_EXERCISES)
                .select {
                    filter {
                        eq("routine_id", routineId)
                    }
                    order("orden", Order.ASCENDING)
                }
                .decodeList<RoutineExerciseDto>()
                .map { it.toDomain(exercises) }
        } catch (e: Exception) {
            logError(TAG, "Error al listar ejercicios de la rutina", e)
            emptyList()
        }
    }

    /**
     * Crea las filas de `routine_exercises` para una rutina recién creada.
     * Si falla, la rutina queda sin ejercicios asociados (no hay caché local:
     * el `routineId` es real y podría reintentarse más adelante).
     */
    suspend fun insertAll(routineId: String, exerciseIds: List<String>, exercises: List<Exercise>): List<RoutineExercise> {
        if (exerciseIds.isEmpty()) return emptyList()
        val inserts = exerciseIds.mapIndexed { index, exerciseId ->
            RoutineExerciseInsertDto(
                routineId = routineId,
                exerciseId = exerciseId,
                orden = index + 1,
                series = DEFAULT_SETS,
                repeticiones = DEFAULT_REPS,
            )
        }
        return try {
            SupabaseConfig.client.from(DbTables.ROUTINE_EXERCISES)
                .insert(inserts) {
                    select()
                }
                .decodeList<RoutineExerciseDto>()
                .map { it.toDomain(exercises) }
        } catch (e: Exception) {
            logError(TAG, "Error al guardar ejercicios de la rutina", e)
            emptyList()
        }
    }

    private fun RoutineExerciseDto.toDomain(exercises: List<Exercise>) = RoutineExercise(
        id = id ?: exerciseId,
        routineId = routineId,
        exerciseId = exerciseId,
        exerciseName = exercises.firstOrNull { it.id == exerciseId }?.name ?: "Ejercicio",
        order = orden,
        sets = series,
        reps = repeticiones,
    )
}
