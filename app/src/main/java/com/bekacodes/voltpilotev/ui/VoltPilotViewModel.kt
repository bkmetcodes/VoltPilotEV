package com.bekacodes.voltpilotev.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bekacodes.voltpilotev.data.local.CabinProfileEntity
import com.bekacodes.voltpilotev.data.local.VoltPilotDao
import com.bekacodes.voltpilotev.data.vhal.VehicleHalEngine
import com.bekacodes.voltpilotev.domain.AiChargerCoPilot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VoltPilotViewModel(
    private val dao: VoltPilotDao,
    private val vhalEngine: VehicleHalEngine = VehicleHalEngine(),
    private val aiCoPilot: AiChargerCoPilot = AiChargerCoPilot()
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoltPilotUiState())
    val uiState: StateFlow<VoltPilotUiState> = _uiState.asStateFlow()

    init {
        // 1. Restore saved driver cabin & drive mode profile from Room SQLite
        viewModelScope.launch {
            val saved =
                dao.getProfileSnapshot() ?: CabinProfileEntity().also { dao.saveProfile(it) }
            val restoredMode =
                runCatching { DriveMode.valueOf(saved.driveModeName) }.getOrDefault(DriveMode.COMFORT)
            val initialStop =
                aiCoPilot.resolveOptimalStop(saved.lastAiPrompt, _uiState.value.socPercent)

            _uiState.update {
                it.copy(
                    driveMode = restoredMode,
                    driverTempF = saved.driverTempF,
                    passengerTempF = saved.passengerTempF,
                    seatHeaterLevel = saved.seatHeaterLevel,
                    fanSpeed = saved.fanSpeed,
                    aiPromptText = saved.lastAiPrompt,
                    recommendedStop = initialStop
                )
            }
        }

        // 2. Collect continuous 500ms VHAL telemetry stream
        viewModelScope.launch {
            vhalEngine.observeTelemetry(
                gearProvider = { _uiState.value.activeGear },
                driveModeProvider = { _uiState.value.driveMode },
                preconditioningProvider = { _uiState.value.isPreconditioningActive }
            ).collect { tick ->
                _uiState.update { current ->
                    val updatedStop =
                        aiCoPilot.resolveOptimalStop(current.aiPromptText, tick.socPercent)
                    current.copy(
                        speedMph = tick.speedMph,
                        powerKw = tick.powerKw,
                        socPercent = tick.socPercent,
                        rangeMiles = tick.rangeMiles,
                        batteryTempF = tick.batteryTempF,
                        tirePressuresPsi = tick.tirePressuresPsi,
                        recommendedStop = updatedStop
                    )
                }
            }
        }
    }

    fun onIntent(intent: VoltPilotIntent) {
        when (intent) {
            is VoltPilotIntent.SelectGear -> {
                val shiftingIntoMotion = intent.gear != GearState.PARK
                _uiState.update { state ->
                    state.copy(
                        activeGear = intent.gear,
                        // AAOS Safety Interlock: Auto-close Frunk, Charge Port & Keyboard Dialog when shifting out of PARK
                        isFrunkOpen = if (shiftingIntoMotion) false else state.isFrunkOpen,
                        isChargePortOpen = if (shiftingIntoMotion) false else state.isChargePortOpen,
                        isPromptDialogOpen = if (shiftingIntoMotion) false else state.isPromptDialogOpen,
                        statusSubtitle = when (intent.gear) {
                            GearState.DRIVE -> "Active telemetry · Keyboard locked for safety"
                            GearState.REVERSE -> "Rear camera & proximity sensors active"
                            GearState.NEUTRAL -> "Drivetrain disengaged"
                            GearState.PARK -> "All systems nominal"
                        }
                    )
                }
            }

            is VoltPilotIntent.SelectDriveMode -> {
                _uiState.update { it.copy(driveMode = intent.mode) }
                persistCabinProfile()
            }

            is VoltPilotIntent.CycleAiVoiceCommand -> {
                val presets = _uiState.value.presetVoiceCommands
                val currentIdx = presets.indexOf(_uiState.value.aiPromptText)
                val nextPrompt = presets[(currentIdx + 1) % presets.size]
                applyAiChargerQuery(nextPrompt)
            }

            is VoltPilotIntent.TogglePromptDialog -> {
                if (_uiState.value.isDriveDistractionRestricted && intent.open) {
                    // In DRIVE mode, tapping the bar cycles hands-free voice presets instead of opening a keyboard
                    onIntent(VoltPilotIntent.CycleAiVoiceCommand)
                    return
                }
                _uiState.update {
                    it.copy(
                        isPromptDialogOpen = intent.open,
                        promptInputBuffer = if (intent.open) it.aiPromptText else it.promptInputBuffer
                    )
                }
            }

            is VoltPilotIntent.UpdatePromptBuffer -> {
                _uiState.update { it.copy(promptInputBuffer = intent.text) }
            }

            is VoltPilotIntent.SubmitCustomAiCommand -> {
                val query = _uiState.value.promptInputBuffer.trim()
                if (query.isNotEmpty()) {
                    applyAiChargerQuery(query)
                }
                _uiState.update { it.copy(isPromptDialogOpen = false) }
            }

            is VoltPilotIntent.ToggleRouteAndPrecondition -> {
                _uiState.update { state ->
                    val nextActive = !state.isNavigationActive
                    state.copy(
                        isNavigationActive = nextActive,
                        isPreconditioningActive = nextActive,
                        statusSubtitle = if (nextActive) {
                            "Heating pack to 82°F for ${state.recommendedStop.maxPowerKw}kW fast charge"
                        } else {
                            "All systems nominal"
                        }
                    )
                }
            }

            is VoltPilotIntent.AdjustDriverTemp -> {
                _uiState.update {
                    it.copy(driverTempF = (it.driverTempF + intent.delta).coerceIn(60, 84))
                }
                persistCabinProfile()
            }

            is VoltPilotIntent.AdjustPassengerTemp -> {
                _uiState.update {
                    it.copy(passengerTempF = (it.passengerTempF + intent.delta).coerceIn(60, 84))
                }
                persistCabinProfile()
            }

            is VoltPilotIntent.CycleSeatHeater -> {
                _uiState.update {
                    it.copy(seatHeaterLevel = (it.seatHeaterLevel + 1) % 4)
                }
                persistCabinProfile()
            }

            is VoltPilotIntent.ToggleDefrost -> {
                _uiState.update { it.copy(isDefrostOn = !it.isDefrostOn) }
            }

            is VoltPilotIntent.CycleFanSpeed -> {
                _uiState.update {
                    it.copy(fanSpeed = (it.fanSpeed % 5) + 1)
                }
                persistCabinProfile()
            }

            is VoltPilotIntent.ToggleFrunk -> {
                if (_uiState.value.activeGear != GearState.PARK) {
                    _uiState.update { it.copy(statusSubtitle = "⚠ Shift to PARK to open Frunk") }
                    return
                }
                _uiState.update {
                    val next = !it.isFrunkOpen
                    it.copy(
                        isFrunkOpen = next,
                        statusSubtitle = if (next) "Front trunk unlatched" else "All systems nominal"
                    )
                }
            }

            is VoltPilotIntent.ToggleChargePort -> {
                if (_uiState.value.activeGear != GearState.PARK) {
                    _uiState.update { it.copy(statusSubtitle = "⚠ Shift to PARK to open Charge Port") }
                    return
                }
                _uiState.update {
                    val next = !it.isChargePortOpen
                    it.copy(
                        isChargePortOpen = next,
                        statusSubtitle = if (next) "Charge port motorized door open" else "All systems nominal"
                    )
                }
            }
        }
    }

    private fun applyAiChargerQuery(query: String) {
        val stop = aiCoPilot.resolveOptimalStop(query, _uiState.value.socPercent)
        _uiState.update {
            it.copy(
                aiPromptText = query,
                recommendedStop = stop
            )
        }
        persistCabinProfile()
    }

    private fun persistCabinProfile() {
        val snapshot = _uiState.value
        viewModelScope.launch {
            dao.saveProfile(
                CabinProfileEntity(
                    profileId = 1,
                    driveModeName = snapshot.driveMode.name,
                    driverTempF = snapshot.driverTempF,
                    passengerTempF = snapshot.passengerTempF,
                    seatHeaterLevel = snapshot.seatHeaterLevel,
                    fanSpeed = snapshot.fanSpeed,
                    lastAiPrompt = snapshot.aiPromptText
                )
            )
        }
    }

    companion object {
        fun provideFactory(dao: VoltPilotDao): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return VoltPilotViewModel(dao) as T
                }
            }
    }
}