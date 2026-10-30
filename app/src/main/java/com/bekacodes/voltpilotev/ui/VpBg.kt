package com.bekacodes.voltpilotev.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bekacodes.voltpilotev.ui.components.AiCoPilotBar
import com.bekacodes.voltpilotev.ui.components.AiPromptInputDialog
import com.bekacodes.voltpilotev.ui.components.AiRecommendedStopCard
import com.bekacodes.voltpilotev.ui.components.BatteryAndRangeCard
import com.bekacodes.voltpilotev.ui.components.BottomHvacDock
import com.bekacodes.voltpilotev.ui.components.TopSystemStatusBar
import com.bekacodes.voltpilotev.ui.components.VehicleDynamicsCard
import com.bekacodes.voltpilotev.ui.theme.VpBg

@Composable
fun VoltPilotCockpitScreen(
    uiState: VoltPilotUiState,
    onIntent: (VoltPilotIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = VpBg
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. TOP SYSTEM & GEAR STATUS BAR
            TopSystemStatusBar(
                uiState = uiState,
                onSelectGear = { onIntent(VoltPilotIntent.SelectGear(it)) }
            )

            // 2. MAIN CENTER COCKPIT (Left Telemetry Column + Right AI Co-Pilot & Map Stage)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // LEFT COLUMN (37% Width): Speed/Power Card + Battery/Range Card
                Column(
                    modifier = Modifier
                        .weight(0.37f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VehicleDynamicsCard(
                        uiState = uiState,
                        onSelectDriveMode = { onIntent(VoltPilotIntent.SelectDriveMode(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.62f)
                    )

                    BatteryAndRangeCard(
                        uiState = uiState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.38f)
                    )
                }

                // RIGHT COLUMN (63% Width): AI Co-Pilot Voice Bar + Route & Supercharger Card
                Column(
                    modifier = Modifier
                        .weight(0.63f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AiCoPilotBar(
                        promptText = uiState.aiPromptText,
                        isDriveRestricted = uiState.isDriveDistractionRestricted,
                        onMicClick = { onIntent(VoltPilotIntent.CycleAiVoiceCommand) },
                        onBarClick = {
                            if (uiState.isDriveDistractionRestricted) {
                                onIntent(VoltPilotIntent.CycleAiVoiceCommand)
                            } else {
                                onIntent(VoltPilotIntent.TogglePromptDialog(true))
                            }
                        }
                    )

                    AiRecommendedStopCard(
                        uiState = uiState,
                        onRouteAndPrecondition = { onIntent(VoltPilotIntent.ToggleRouteAndPrecondition) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            // 3. BOTTOM DUAL-ZONE HVAC & VEHICLE CONTROLS DOCK
            BottomHvacDock(
                uiState = uiState,
                onIntent = onIntent
            )
        }

        // Custom AI Prompt Input Dialog (Only accessible when PARKED per AAOS safety rules)
        if (uiState.isPromptDialogOpen && !uiState.isDriveDistractionRestricted) {
            AiPromptInputDialog(
                initialText = uiState.promptInputBuffer,
                presets = uiState.presetVoiceCommands,
                onTextChanged = { onIntent(VoltPilotIntent.UpdatePromptBuffer(it)) },
                onSelectPreset = { preset ->
                    onIntent(VoltPilotIntent.UpdatePromptBuffer(preset))
                    onIntent(VoltPilotIntent.SubmitCustomAiCommand)
                },
                onSubmit = { onIntent(VoltPilotIntent.SubmitCustomAiCommand) },
                onDismiss = { onIntent(VoltPilotIntent.TogglePromptDialog(false)) }
            )
        }
    }
}

@Preview(
    name = "VoltPilot EV Widescreen Cockpit",
    widthDp = 1024,
    heightDp = 576,
    showBackground = true
)
@Composable
private fun VoltPilotCockpitScreenPreview() {
    MaterialTheme {
        VoltPilotCockpitScreen(
            uiState = VoltPilotUiState(),
            onIntent = {}
        )
    }
}