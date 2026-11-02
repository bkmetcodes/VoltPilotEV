package com.bekacodes.voltpilotev.data.vhal

import com.bekacodes.voltpilotev.ui.DriveMode
import com.bekacodes.voltpilotev.ui.GearState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

class VehicleHalEngine {

    /**
     * Streams continuous 500ms VHAL telemetry ticks reacting to active gear, drive mode,
     * and thermal battery preconditioning state.
     */
    fun observeTelemetry(
        gearProvider: () -> GearState,
        driveModeProvider: () -> DriveMode,
        preconditioningProvider: () -> Boolean
    ): Flow<VhalTelemetryTick> = flow {
        var tickCount = 0
        var currentSoc = 78.0
        var currentBattTemp = 74.0

        while (true) {
            val gear = gearProvider()
            val mode = driveModeProvider()
            val isPreconditioning = preconditioningProvider()

            val modePowerMultiplier = when (mode) {
                DriveMode.RANGE -> 0.78
                DriveMode.COMFORT -> 1.00
                DriveMode.SPORT -> 1.35
            }

            val modeRangeFactor = when (mode) {
                DriveMode.RANGE -> 3.22
                DriveMode.COMFORT -> 3.10
                DriveMode.SPORT -> 2.88
            }

            val (speed, power) = when (gear) {
                GearState.PARK, GearState.NEUTRAL -> {
                    val hvacAuxKw = if (isPreconditioning) 4 else 0
                    0 to hvacAuxKw
                }

                GearState.REVERSE -> {
                    5 to 8
                }

                GearState.DRIVE -> {
                    val wave = sin(tickCount * 0.45)
                    val secondaryWave = sin(tickCount * 0.18)
                    val targetSpeed =
                        (66 + (wave * 5) + (secondaryWave * 3)).roundToInt().coerceIn(55, 76)

                    // Periodically enter regenerative braking on downhill/deceleration phases
                    val rawKw = (28 * wave + 18 * secondaryWave) * modePowerMultiplier
                    val targetKw = rawKw.roundToInt().coerceIn(-45, 110)

                    // Gradual SoC consumption (or slight recovery during regen)
                    currentSoc = (currentSoc - (targetKw * 0.00035)).coerceIn(12.0, 100.0)
                    targetSpeed to targetKw
                }
            }

            // Thermal battery preconditioning warms pack to optimal 82°F for 350kW DC fast charging
            currentBattTemp = if (isPreconditioning) {
                (currentBattTemp + 0.25).coerceAtMost(82.0)
            } else if (gear == GearState.DRIVE) {
                (currentBattTemp + 0.05).coerceAtMost(78.0)
            } else {
                (currentBattTemp - 0.10).coerceAtLeast(74.0)
            }

            // Tire pressure warms +1 PSI when cruising at highway speeds
            val psiBoost = if (speed > 50) 1 else 0
            val tires = listOf(42 + psiBoost, 42 + psiBoost, 41 + psiBoost, 42 + psiBoost)

            val socInt = currentSoc.roundToInt().coerceIn(1, 100)
            val estimatedRange = (socInt * modeRangeFactor).roundToInt()

            emit(
                VhalTelemetryTick(
                    speedMph = speed,
                    powerKw = power,
                    socPercent = socInt,
                    rangeMiles = estimatedRange,
                    batteryTempF = currentBattTemp.roundToInt(),
                    tirePressuresPsi = tires
                )
            )

            tickCount++
            delay(500L.milliseconds)
        }
    }
}