package com.example.fittrack.shared.data

import com.example.fittrack.shared.auth.AuthManager
import com.example.fittrack.shared.config.SupabaseConfig
import com.example.fittrack.shared.data.dto.WeightPatchDto
import com.example.fittrack.shared.data.dto.WeightRecordDto
import com.example.fittrack.shared.data.schema.DbTables
import com.example.fittrack.shared.domain.WeightRecord
import com.example.fittrack.shared.logError
import com.example.fittrack.shared.platform.formatIsoDate
import com.example.fittrack.shared.platform.randomUuid
import com.example.fittrack.shared.platform.todayIsoDate
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class WeightRepository(
    private val auth: AuthManager,
) {
    private val memory = mutableListOf<WeightRecord>()

    suspend fun list(): List<WeightRecord> {
        val userId = auth.getCurrentUser()?.id ?: return memory.toList()
        return try {
            val remote = SupabaseConfig.client.from(DbTables.WEIGHT_RECORDS)
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                    order("fecha", Order.DESCENDING)
                }
                .decodeList<WeightRecordDto>()
                .map { it.toDomain() }
            memory.clear()
            memory.addAll(remote)
            remote
        } catch (e: Exception) {
            logError(TAG, "Error al listar pesos; se usa cache local", e)
            memory.toList()
        }
    }

    suspend fun add(kilograms: Float): WeightRecord {
        val userId = requireUserId()
        val isoDate = todayIsoDate()
        val local = WeightRecord(
            id = randomUuid(),
            isoDate = isoDate,
            kilograms = kilograms,
            dateLabel = formatIsoDate(isoDate),
        )
        return try {
            val dto = WeightRecordDto(
                userId = userId,
                peso = kilograms.toDouble(),
                fecha = isoDate,
            )
            val created = SupabaseConfig.client.from(DbTables.WEIGHT_RECORDS)
                .insert(dto) {
                    select()
                }
                .decodeList<WeightRecordDto>()
                .firstOrNull()
                ?.toDomain()
                ?: local
            memory.add(0, created)
            created
        } catch (e: Exception) {
            logError(TAG, "Error al guardar peso; queda en cache local", e)
            memory.add(0, local)
            local
        }
    }

    suspend fun update(id: String, kilograms: Float): WeightRecord? {
        return try {
            SupabaseConfig.client.from(DbTables.WEIGHT_RECORDS)
                .update(WeightPatchDto(peso = kilograms.toDouble())) {
                    filter {
                        eq("id", id)
                    }
                    select()
                }
                .decodeList<WeightRecordDto>()
                .firstOrNull()
                ?.toDomain()
                ?.also { updated ->
                    val index = memory.indexOfFirst { it.id == id }
                    if (index >= 0) memory[index] = updated
                }
        } catch (e: Exception) {
            logError(TAG, "Error al actualizar peso; se actualiza cache local", e)
            val index = memory.indexOfFirst { it.id == id }
            if (index < 0) return null
            val updated = memory[index].copy(kilograms = kilograms)
            memory[index] = updated
            updated
        }
    }

    fun clear() {
        memory.clear()
    }

    private fun requireUserId(): String {
        return auth.getCurrentUser()?.id
            ?: error("No hay sesión activa")
    }

    private fun WeightRecordDto.toDomain(): WeightRecord {
        val iso = fecha
        return WeightRecord(
            id = id ?: randomUuid(),
            isoDate = iso,
            kilograms = peso.toFloat(),
            dateLabel = formatIsoDate(iso),
        )
    }

    private companion object {
        const val TAG = "WeightRepository"
    }
}
