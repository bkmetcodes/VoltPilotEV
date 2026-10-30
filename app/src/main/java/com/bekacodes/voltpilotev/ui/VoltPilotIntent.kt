package com.bekacodes.voltpilotev.ui

sealed interface VoltPilotIntent {
    data class SelectGear(val gear: GearState) : VoltPilotIntent
    data class SelectDriveMode(val mode: DriveMode) : VoltPilotIntent
    data object CycleAiVoiceCommand : VoltPilotIntent
    data class TogglePromptDialog(val open: Boolean) : VoltPilotIntent
    data class UpdatePromptBuffer(val text: String) : VoltPilotIntent
    data object SubmitCustomAiCommand : VoltPilotIntent
    data object ToggleRouteAndPrecondition : VoltPilotIntent
    data class AdjustDriverTemp(val delta: Int) : VoltPilotIntent
    data class AdjustPassengerTemp(val delta: Int) : VoltPilotIntent
    data object CycleSeatHeater : VoltPilotIntent
    data object ToggleDefrost : VoltPilotIntent
    data object CycleFanSpeed : VoltPilotIntent
    data object ToggleFrunk : VoltPilotIntent
    data object ToggleChargePort : VoltPilotIntent
}