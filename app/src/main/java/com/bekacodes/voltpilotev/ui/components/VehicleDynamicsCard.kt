package com.bekacodes.voltpilotev.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekacodes.voltpilotev.ui.DriveMode
import com.bekacodes.voltpilotev.ui.GearState
import com.bekacodes.voltpilotev.ui.VoltPilotUiState
import com.bekacodes.voltpilotev.ui.theme.VpBorder
import com.bekacodes.voltpilotev.ui.theme.VpCard
import com.bekacodes.voltpilotev.ui.theme.VpCyan
import com.bekacodes.voltpilotev.ui.theme.VpInk
import com.bekacodes.voltpilotev.ui.theme.VpMuted
import com.bekacodes.voltpilotev.ui.theme.VpSecondary

@Composable
fun VehicleDynamicsCard(
    uiState: VoltPilotUiState,
    onSelectDriveMode: (DriveMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = VpCard,
        border = BorderStroke(1.dp, VpBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VoltPilot EV",
                    color = VpInk,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(VpCyan)
                    )
                    Text(
                        text = if (uiState.activeGear == GearState.DRIVE) "ACTIVE DRIVE" else "READY TO DRIVE",
                        color = VpCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Speedometer + 240-Degree Power/Regen Arc Gauge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = uiState.speedMph.toString(),
                            color = VpInk,
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Light,
                            lineHeight = 54.sp
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "MPH",
                            color = VpMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Text(
                        text = if (uiState.speedMph == 0) "Vehicle stationary" else "Cruising · Adaptive Regen",
                        color = VpMuted,
                        fontSize = 11.sp
                    )
                }

                PowerRegenArcGauge(
                    powerKw = uiState.powerKw,
                    modifier = Modifier.size(116.dp)
                )
            }

            // Silver EV Coupe Silhouette + Security Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EvSideProfileCanvas(
                    isDriving = uiState.speedMph > 0,
                    modifier = Modifier
                        .width(142.dp)
                        .height(44.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = if (uiState.isPreconditioningActive) "🔥 Thermal Preconditioning" else "🔒 Secured & ready",
                        color = if (uiState.isPreconditioningActive) VpCyan else VpInk,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = uiState.statusSubtitle,
                        color = VpMuted,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Segmented Drive Mode Selector (Range | Comfort | Sport)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color(0xFF080C14))
                    .border(1.dp, VpBorder, RoundedCornerShape(9.dp))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DriveMode.entries.forEach { mode ->
                    val selected = uiState.driveMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(7.dp))
                            .background(if (selected) Color(0xFF0E2336) else Color.Transparent)
                            .border(
                                width = if (selected) 1.dp else 0.dp,
                                color = if (selected) VpCyan else Color.Transparent,
                                shape = RoundedCornerShape(7.dp)
                            )
                            .clickable { onSelectDriveMode(mode) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.label,
                            color = if (selected) VpCyan else VpSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}