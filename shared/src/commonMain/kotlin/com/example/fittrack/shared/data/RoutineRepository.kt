package com.example.fittrack.shared.data

import com.example.fittrack.shared.auth.AuthManager
import com.example.fittrack.shared.config.SupabaseConfig
import com.example.fittrack.shared.data.dto.RoutineDto
import com.example.fittrack.shared.data.schema.DbTables
import com.example.fittrack.shared.domain.Routine
import com.example.fittrack.shared.logError
import com.example.fittrack.shared.platform.randomUuid
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class RoutineRepository(
    private val auth: AuthManager,
) {
    private val memory = mutableListOf<Routine>()

    suspend fun list(): List<Routine> {
        val userId = auth.getCurrentUser()?.id ?: return memory.toList()
        return try {
            val remote = SupabaseConfig.client.from(DbTables.ROUTINES)
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<RoutineDto>()
                .map { it.toDomain() }
            memory.clear()
            memory.addAll(remote)
            remote
        } catch (e: Exception) {
            logError(TAG, "Error al listar rutinas; se usa cache local", e)
            if (memory.isEmpty()) {
                memory.addAll(seedRoutines())
            }
            memory.toList()
        }
    }

    suspend fun add(
        name: String,
        durationMinutes: Int,
        exerciseCount: Int,
        weeklyFrequency: Int,
    ): Routine {
        val userId = requireUserId()
        val local = Routine(
            id = randomUuid(),
            name = name,
            durationMinutes = durationMinutes,
            exerciseCount = exerciseCount,
            weeklyFrequency = weeklyFrequency,
        )
        return try {
            val dto = RoutineDto(
                userId = userId,
                nombre = name,
                duracion = durationMinutes,
                cantidadEjercicios = exerciseCount,
                frecuenciaSemanal = weeklyFrequency,
            )
            val created = SupabaseConfig.client.from(DbTables.ROUTINES)
                .insert(dto) {
                    select()
                }
                .decodeList<RoutineDto>()
                .firstOrNull()
                ?.toDomain()
                ?: local
            memory.add(0, created)
            created
        } catch (e: Exception) {
            logError(TAG, "Error al guardar rutina; queda en cache local", e)
            memory.add(0, local)
            local
        }
    }

    fun clear() {
        memory.clear()
    }

    private fun requireUserId(): String {
        return auth.getCurrentUser()?.id
            ?: error("No hay sesión activa")
    }

    private fun RoutineDto.toDomain() = Routine(
        id = id ?: randomUuid(),
        name = nombre,
        durationMinutes = duracion,
        exerciseCount = cantidadEjercicios,
        weeklyFrequency = frecuenciaSemanal,
    )

    private fun seedRoutines() = listOf(
        Routine(randomUuid(), "Full Body Día 1", 30, 6, 3),
        Routine(randomUuid(), "Pecho y Tríceps", 45, 8, 2),
        Routine(randomUuid(), "Espalda y Bíceps", 40, 7, 2),
        Routine(randomUuid(), "Piernas Completas", 50, 9, 1),
    )

    private companion object {
        const val TAG = "RoutineRepository"
    }
}
