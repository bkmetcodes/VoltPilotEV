package com.bekacodes.voltpilotev.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bekacodes.voltpilotev.ui.VoltPilotIntent
import com.bekacodes.voltpilotev.ui.VoltPilotUiState
import com.bekacodes.voltpilotev.ui.theme.VpBorder
import com.bekacodes.voltpilotev.ui.theme.VpCard

@Composable
fun BottomHvacDock(
    uiState: VoltPilotUiState,
    onIntent: (VoltPilotIntent) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(VpBorder)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .background(Color(0xFF080C14))
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Driver Temp Stepper
            HvacTempControl(
                roleLabel = "DRIVER",
                tempF = uiState.driverTempF,
                onDecrement = { onIntent(VoltPilotIntent.AdjustDriverTemp(-1)) },
                onIncrement = { onIntent(VoltPilotIntent.AdjustDriverTemp(1)) }
            )

            // 2. Center Climate Controls (Seat Heater, Defrost, AUTO · FAN)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(26.dp)
            ) {
                ClimateIconAction(
                    icon = "♨",
                    label = if (uiState.seatHeaterLevel == 0) "Seat Heater" else "Heater (${uiState.seatHeaterLevel})",
                    active = uiState.seatHeaterLevel > 0,
                    onClick = { onIntent(VoltPilotIntent.CycleSeatHeater) }
                )
                ClimateIconAction(
                    icon = "ttt",
                    label = "Defrost",
                    active = uiState.isDefrostOn,
                    onClick = { onIntent(VoltPilotIntent.ToggleDefrost) }
                )
                ClimateIconAction(
                    icon = "❂",
                    label = "AUTO · FAN ${uiState.fanSpeed}",
                    active = true,
                    onClick = { onIntent(VoltPilotIntent.CycleFanSpeed) }
                )
            }

            // 3. Frunk & Charge Port Segmented Pill
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(VpCard)
                    .border(1.dp, VpBorder, RoundedCornerShape(10.dp))
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickAccessToggleButton(
                    label = if (uiState.isFrunkOpen) "🚗 Frunk Open" else "🚗 Frunk",
                    active = uiState.isFrunkOpen,
                    onClick = { onIntent(VoltPilotIntent.ToggleFrunk) }
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(22.dp)
                        .background(VpBorder)
                )
                QuickAccessToggleButton(
                    label = if (uiState.isChargePortOpen) "⚡ Port Open" else "🔌 Charge Port",
                    active = uiState.isChargePortOpen,
                    onClick = { onIntent(VoltPilotIntent.ToggleChargePort) }
                )
            }

            // 4. Passenger Temp Stepper
            HvacTempControl(
                roleLabel = "PASSENGER",
                tempF = uiState.passengerTempF,
                onDecrement = { onIntent(VoltPilotIntent.AdjustPassengerTemp(-1)) },
                onIncrement = { onIntent(VoltPilotIntent.AdjustPassengerTemp(1)) }
            )
        }
    }
}