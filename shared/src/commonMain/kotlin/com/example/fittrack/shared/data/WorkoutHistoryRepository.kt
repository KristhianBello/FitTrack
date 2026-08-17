package com.example.fittrack.shared.data

import com.example.fittrack.shared.auth.AuthManager
import com.example.fittrack.shared.config.SupabaseConfig
import com.example.fittrack.shared.data.dto.WorkoutHistoryDto
import com.example.fittrack.shared.data.schema.DbTables
import com.example.fittrack.shared.domain.Routine
import com.example.fittrack.shared.domain.WorkoutSession
import com.example.fittrack.shared.logError
import com.example.fittrack.shared.platform.formatIsoTimestamp
import com.example.fittrack.shared.platform.nowIsoTimestamp
import com.example.fittrack.shared.platform.randomUuid
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class WorkoutHistoryRepository(
    private val auth: AuthManager,
) {
    private val memory = mutableListOf<WorkoutSession>()

    suspend fun list(routines: List<Routine> = emptyList()): List<WorkoutSession> {
        val userId = auth.getCurrentUser()?.id ?: return memory.toList()
        return try {
            val remote = SupabaseConfig.client.from(DbTables.WORKOUT_HISTORY)
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                    order("fecha", Order.DESCENDING)
                }
                .decodeList<WorkoutHistoryDto>()
                .map { it.toDomain(routines) }
            memory.clear()
            memory.addAll(remote)
            remote
        } catch (e: Exception) {
            logError(TAG, "Error al listar entrenamientos; se usa cache local", e)
            memory.toList()
        }
    }

    suspend fun add(
        routine: Routine,
        durationMinutes: Int = routine.durationMinutes,
    ): WorkoutSession {
        val userId = requireUserId()
        val nowIso = nowIsoTimestamp()
        val local = WorkoutSession(
            id = randomUuid(),
            routineId = routine.id,
            routineName = routine.name,
            durationMinutes = durationMinutes,
            calories = null,
            isoTimestamp = nowIso,
            dateLabel = "Ahora",
            completed = true,
            category = routine.category,
        )
        return try {
            val dto = WorkoutHistoryDto(
                userId = userId,
                routineId = routine.id,
                duracionReal = durationMinutes,
                completed = true,
            )
            val created = SupabaseConfig.client.from(DbTables.WORKOUT_HISTORY)
                .insert(dto) {
                    select()
                }
                .decodeList<WorkoutHistoryDto>()
                .firstOrNull()
                ?.toDomain(listOf(routine))
                ?: local
            memory.add(0, created)
            created
        } catch (e: Exception) {
            logError(TAG, "Error al guardar entrenamiento; queda en cache local", e)
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

    private fun WorkoutHistoryDto.toDomain(routines: List<Routine>): WorkoutSession {
        val stamp = fecha.orEmpty()
        val routine = routines.firstOrNull { it.id == routineId }
        return WorkoutSession(
            id = id ?: randomUuid(),
            routineId = routineId,
            routineName = routine?.name ?: "Entrenamiento",
            durationMinutes = duracionReal,
            calories = caloriasQuemadas,
            isoTimestamp = stamp,
            dateLabel = if (stamp.isBlank()) "Ahora" else formatIsoTimestamp(stamp),
            completed = completed,
            category = routine?.category,
        )
    }

    private companion object {
        const val TAG = "WorkoutHistoryRepository"
    }
}
