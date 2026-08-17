package com.example.fittrack.shared.session

import com.example.fittrack.shared.data.ExerciseRepository
import com.example.fittrack.shared.data.ProfileRepository
import com.example.fittrack.shared.data.RoutineExerciseRepository
import com.example.fittrack.shared.data.RoutineRepository
import com.example.fittrack.shared.data.WeightRepository
import com.example.fittrack.shared.data.WorkoutHistoryRepository
import com.example.fittrack.shared.domain.Exercise
import com.example.fittrack.shared.domain.Routine
import com.example.fittrack.shared.domain.RoutineExercise
import com.example.fittrack.shared.domain.UserProfile
import com.example.fittrack.shared.domain.WeightRecord
import com.example.fittrack.shared.domain.WorkoutSession
import com.example.fittrack.shared.domain.WorkoutStats
import com.example.fittrack.shared.platform.isoDateDaysAgo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FitTrackSession(
    private val profiles: ProfileRepository,
    private val weights: WeightRepository,
    private val routineRepository: RoutineRepository,
    private val workoutHistory: WorkoutHistoryRepository,
    private val exerciseRepository: ExerciseRepository,
    private val routineExerciseRepository: RoutineExerciseRepository,
) {
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    private val _weightRecords = MutableStateFlow<List<WeightRecord>>(emptyList())
    val weightRecords: StateFlow<List<WeightRecord>> = _weightRecords.asStateFlow()

    private val _routines = MutableStateFlow<List<Routine>>(emptyList())
    val routines: StateFlow<List<Routine>> = _routines.asStateFlow()

    private val _workouts = MutableStateFlow<List<WorkoutSession>>(emptyList())
    val workouts: StateFlow<List<WorkoutSession>> = _workouts.asStateFlow()

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status.asStateFlow()

    val nextRoutine: Routine?
        get() = _routines.value.firstOrNull()

    val weeklyCompleted: Int
        get() = WorkoutStats.completedSince(_workouts.value, isoDateDaysAgo(6))

    val weeklyGoal: Int
        get() = WorkoutStats.weeklyGoal(_routines.value)

    val weeklyPercent: Int
        get() = WorkoutStats.percent(weeklyCompleted, weeklyGoal)

    suspend fun start(displayName: String? = null) {
        _profile.value = profiles.ensureCurrentProfile(displayName)
        refreshWeights()
        refreshRoutines()
        refreshWorkouts()
        refreshExercises()
    }

    suspend fun refreshWeights() {
        _weightRecords.value = weights.list()
    }

    suspend fun refreshRoutines() {
        _routines.value = routineRepository.list()
    }

    suspend fun refreshWorkouts() {
        _workouts.value = workoutHistory.list(_routines.value)
    }

    suspend fun refreshExercises() {
        _exercises.value = exerciseRepository.list()
    }

    suspend fun routineExercises(routineId: String): List<RoutineExercise> {
        return routineExerciseRepository.listForRoutine(routineId, _exercises.value)
    }

    suspend fun savePersonalData(
        heightCm: Float,
        currentWeightKg: Float,
        goalWeightKg: Float,
        birthIsoDate: String,
        gender: String,
    ): Result<UserProfile?> {
        return runCatching {
            profiles.savePersonalData(heightCm, goalWeightKg, birthIsoDate, gender)
        }.onSuccess { updated ->
            if (updated != null) _profile.value = updated
            addWeight(currentWeightKg)
        }.onFailure {
            _status.value = it.message
        }
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
        weeklyFrequency: Int,
        category: String,
        exerciseIds: List<String>,
    ): Result<Routine> {
        return runCatching {
            val created = routineRepository.add(name, durationMinutes, exerciseIds.size, weeklyFrequency, category)
            routineExerciseRepository.insertAll(created.id, exerciseIds, _exercises.value)
            created
        }.onSuccess {
            refreshRoutines()
        }.onFailure {
            _status.value = it.message
        }
    }

    suspend fun completeWorkout(routine: Routine? = nextRoutine): Result<WorkoutSession> {
        val selected = routine ?: return Result.failure(IllegalStateException("No hay rutina para entrenar"))
        return runCatching { workoutHistory.add(selected) }
            .onSuccess { refreshWorkouts() }
            .onFailure { _status.value = it.message }
    }

    fun clear() {
        _profile.value = null
        _weightRecords.value = emptyList()
        _routines.value = emptyList()
        _workouts.value = emptyList()
        weights.clear()
        routineRepository.clear()
        workoutHistory.clear()
    }
}
