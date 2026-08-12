package com.example.fittrack.shared.session

import com.example.fittrack.shared.data.ProfileRepository
import com.example.fittrack.shared.data.RoutineRepository
import com.example.fittrack.shared.data.WeightRepository
import com.example.fittrack.shared.domain.Routine
import com.example.fittrack.shared.domain.UserProfile
import com.example.fittrack.shared.domain.WeightRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FitTrackSession(
    private val profiles: ProfileRepository,
    private val weights: WeightRepository,
    private val routineRepository: RoutineRepository,
) {
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    private val _weightRecords = MutableStateFlow<List<WeightRecord>>(emptyList())
    val weightRecords: StateFlow<List<WeightRecord>> = _weightRecords.asStateFlow()

    private val _routines = MutableStateFlow<List<Routine>>(emptyList())
    val routines: StateFlow<List<Routine>> = _routines.asStateFlow()

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status.asStateFlow()

    suspend fun start(displayName: String? = null) {
        _profile.value = profiles.ensureCurrentProfile(displayName)
        refreshWeights()
        refreshRoutines()
    }

    suspend fun refreshWeights() {
        _weightRecords.value = weights.list()
    }

    suspend fun refreshRoutines() {
        _routines.value = routineRepository.list()
    }

    suspend fun addWeight(kilograms: Float): Result<WeightRecord> {
        return runCatching { weights.add(kilograms) }
            .onSuccess { refreshWeights() }
            .onFailure { _status.value = it.message }
    }

    suspend fun updateWeight(id: String, kilograms: Float): Result<WeightRecord?> {
        return runCatching { weights.update(id, kilograms) }
            .onSuccess { refreshWeights() }
            .onFailure { _status.value = it.message }
    }

    suspend fun addRoutine(
        name: String,
        durationMinutes: Int,
        exerciseCount: Int,
        weeklyFrequency: Int,
    ): Result<Routine> {
        return runCatching {
            routineRepository.add(name, durationMinutes, exerciseCount, weeklyFrequency)
        }.onSuccess {
            refreshRoutines()
        }.onFailure {
            _status.value = it.message
        }
    }

    fun clear() {
        _profile.value = null
        _weightRecords.value = emptyList()
        _routines.value = emptyList()
        weights.clear()
        routineRepository.clear()
    }
}
