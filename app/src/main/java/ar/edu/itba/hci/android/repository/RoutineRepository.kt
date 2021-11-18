package ar.edu.itba.hci.android.repository

import ar.edu.itba.hci.android.MainApplication
import ar.edu.itba.hci.android.api.service.RoutineService
import ar.edu.itba.hci.android.ui.routine.Cycle
import ar.edu.itba.hci.android.ui.routine.Exercise
import ar.edu.itba.hci.android.ui.routine.Routine
import kotlin.math.round

class RoutineRepository(app: MainApplication) :
    BaseRepository<RoutineService>(app, RoutineService::class.java) {

    suspend fun getRoutine(routineID:Int) : Routine {
        val apiRoutine = unwrapResponse(apiService.getRoutine(routineID))
        val apiCycles = unwrapResponse(apiService.getRoutineCycles(routineID))

        val cycles = apiCycles.content.sortedBy{ it.order }.map { cycle ->
            val apiExercises = unwrapResponse(apiService.getCycleExercises(cycle.id))
            val exercises = apiExercises.content.sortedBy{ it.order }.map { cycleEx ->
                val repetitionType:Exercise.RepetitionType
                val repetitionValue:Int

                when {
                    cycleEx.duration >= 60 -> {
                        repetitionType = Exercise.RepetitionType.MINUTES
                        repetitionValue = round(cycleEx.duration / 60f).toInt()
                    }
                    cycleEx.duration > 0 -> {
                        repetitionType = Exercise.RepetitionType.SECONDS
                        repetitionValue = cycleEx.duration
                    }
                    else -> {
                        repetitionType = Exercise.RepetitionType.TIMES
                        repetitionValue = cycleEx.repetitions
                    }
                }

                Exercise(
                    cycleEx.exercise.name,
                    repetitionType,
                    repetitionValue,
                    cycleEx.exercise.detail,
                    cycleEx.exercise.metadata.grupo,
                    cycleEx.exercise.metadata.dif
                )
            }

            Cycle(
                cycle.id,
                cycle.name,
                cycle.repetitions,
                exercises
            )
        }

        return Routine(
            apiRoutine.id,
            apiRoutine.name,
            apiRoutine.detail,
            apiRoutine.metadata.duration,
            apiRoutine.difficulty,
            cycles
        )
    }
}