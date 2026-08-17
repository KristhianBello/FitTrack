package com.example.fittrack.shared.data

import com.example.fittrack.shared.auth.AuthManager
import com.example.fittrack.shared.config.SupabaseConfig
import com.example.fittrack.shared.data.dto.SensorDataDto
import com.example.fittrack.shared.data.schema.DbTables
import com.example.fittrack.shared.domain.SensorSample
import com.example.fittrack.shared.logError
import com.example.fittrack.shared.platform.formatIsoTimestamp
import com.example.fittrack.shared.platform.randomUuid
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class SensorRepository(
    private val auth: AuthManager,
) {
    private val memory = mutableListOf<SensorSample>()

    suspend fun listRecent(limit: Int = 20): List<SensorSample> {
        val userId = auth.getCurrentUser()?.id ?: return memory.take(limit)
        return try {
            val remote = SupabaseConfig.client.from(DbTables.SENSOR_DATA)
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                    order("fecha", Order.DESCENDING)
                    limit(limit.toLong())
                }
                .decodeList<SensorDataDto>()
                .map { it.toDomain() }
            memory.clear()
            memory.addAll(remote)
            remote
        } catch (e: Exception) {
            logError(TAG, "Error al listar sensores; se usa cache local", e)
            memory.take(limit)
        }
    }

    suspend fun record(kind: String, value: Double, unit: String?): SensorSample {
        val userId = requireUserId()
        val local = SensorSample(
            id = randomUuid(),
            kind = kind,
            value = value,
            unit = unit,
            isoTimestamp = "",
            dateLabel = "Ahora",
        )
        return try {
            val dto = SensorDataDto(
                userId = userId,
                tipoSensor = kind,
                valor = value,
                unidad = unit,
            )
            val created = SupabaseConfig.client.from(DbTables.SENSOR_DATA)
                .insert(dto) {
                    select()
                }
                .decodeList<SensorDataDto>()
                .firstOrNull()
                ?.toDomain()
                ?: local
            memory.add(0, created)
            created
        } catch (e: Exception) {
            logError(TAG, "Error al guardar sensor; queda en cache local", e)
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

    private fun SensorDataDto.toDomain(): SensorSample {
        val stamp = fecha.orEmpty()
        return SensorSample(
            id = id ?: randomUuid(),
            kind = tipoSensor,
            value = valor,
            unit = unidad,
            isoTimestamp = stamp,
            dateLabel = if (stamp.isBlank()) "Ahora" else formatIsoTimestamp(stamp),
        )
    }

    private companion object {
        const val TAG = "SensorRepository"
    }
}
