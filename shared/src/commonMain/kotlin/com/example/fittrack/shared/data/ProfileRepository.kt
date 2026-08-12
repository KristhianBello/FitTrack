package com.example.fittrack.shared.data

import com.example.fittrack.shared.auth.AuthManager
import com.example.fittrack.shared.config.SupabaseConfig
import com.example.fittrack.shared.data.dto.ProfileDto
import com.example.fittrack.shared.data.schema.DbTables
import com.example.fittrack.shared.domain.UserProfile
import com.example.fittrack.shared.logError
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class ProfileRepository(
    private val auth: AuthManager,
) {
    suspend fun ensureCurrentProfile(displayName: String? = null): UserProfile? {
        val user = auth.getCurrentUser() ?: return null
        val name = displayName
            ?: user.email?.substringBefore("@")
            ?: "Atleta"
        val email = user.email.orEmpty()
        return try {
            val existing = SupabaseConfig.client.from(DbTables.PROFILES)
                .select(Columns.ALL) {
                    filter {
                        eq("id", user.id)
                    }
                }
                .decodeList<ProfileDto>()
                .firstOrNull()
            if (existing != null) {
                existing.toDomain()
            } else {
                val created = ProfileDto(id = user.id, name = name, email = email)
                SupabaseConfig.client.from(DbTables.PROFILES).upsert(created, onConflict = "id")
                created.toDomain()
            }
        } catch (e: Exception) {
            logError(TAG, "No se pudo sincronizar el perfil; se usa cache local", e)
            UserProfile(id = user.id, name = name, email = user.email)
        }
    }

    suspend fun loadCurrent(): UserProfile? {
        val user = auth.getCurrentUser() ?: return null
        return try {
            SupabaseConfig.client.from(DbTables.PROFILES)
                .select {
                    filter {
                        eq("id", user.id)
                    }
                }
                .decodeList<ProfileDto>()
                .firstOrNull()
                ?.toDomain()
                ?: UserProfile(
                    id = user.id,
                    name = user.email?.substringBefore("@") ?: "Atleta",
                    email = user.email,
                )
        } catch (e: Exception) {
            logError(TAG, "Error al cargar perfil", e)
            UserProfile(
                id = user.id,
                name = user.email?.substringBefore("@") ?: "Atleta",
                email = user.email,
            )
        }
    }

    private fun ProfileDto.toDomain() = UserProfile(
        id = id,
        name = name,
        email = email,
        goalKg = pesoMeta?.toFloat(),
    )

    private companion object {
        const val TAG = "ProfileRepository"
    }
}
