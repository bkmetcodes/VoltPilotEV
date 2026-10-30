package com.bekacodes.voltpilotev.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekacodes.voltpilotev.ui.GearState
import com.bekacodes.voltpilotev.ui.VoltPilotUiState
import com.bekacodes.voltpilotev.ui.theme.VpAmber
import com.bekacodes.voltpilotev.ui.theme.VpBorder
import com.bekacodes.voltpilotev.ui.theme.VpCyan
import com.bekacodes.voltpilotev.ui.theme.VpEmerald
import com.bekacodes.voltpilotev.ui.theme.VpEmeraldSurface
import com.bekacodes.voltpilotev.ui.theme.VpIndigo
import com.bekacodes.voltpilotev.ui.theme.VpInk
import com.bekacodes.voltpilotev.ui.theme.VpSecondary

@Composable
fun TopSystemStatusBar(
    uiState: VoltPilotUiState,
    onSelectGear: (GearState) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFF080C14))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Gear Selector (P R N D) + Safety Mode Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                GearState.entries.forEach { gear ->
                    val selected = uiState.activeGear == gear
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(if (selected) Color(0xFF0C2538) else Color(0xFF0E1420))
                            .border(
                                width = if (selected) 1.5.dp else 1.dp,
                                color = if (selected) VpCyan else VpBorder,
                                shape = RoundedCornerShape(7.dp)
                            )
                            .clickable { onSelectGear(gear) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = gear.label,
                            color = if (selected) VpCyan else VpSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                // PARKED vs DRIVE Distraction Safety Badge
                val isParked = !uiState.isDriveDistractionRestricted
                val badgeBg = if (isParked) VpEmeraldSurface else Color(0xFF2B1D0B)
                val badgeBorder =
                    if (isParked) VpEmerald.copy(alpha = 0.45f) else VpAmber.copy(alpha = 0.55f)
                val badgeTextColor = if (isParked) VpEmerald else VpAmber
                val badgeLabel = if (isParked) {
                    "🛡 PARKED · FULL UI UNLOCKED"
                } else {
                    "🔒 DRIVE · LOW-DISTRACTION LOCKOUT"
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg)
                        .border(1.dp, badgeBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = badgeLabel,
                        color = badgeTextColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp
                    )
                }
            }

            // Center: Ambient Temp & 5G VHAL Telemetry Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "☀  ${uiState.ambientTempF}°F · Dual HVAC Auto",
                    color = VpInk,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(VpEmerald)
                    )
                    Text(
                        text = "5G · VHAL Connected",
                        color = VpSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Right: VoltPilot OS + Clock
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "VoltPilot OS ",
                        color = VpInk,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "✦",
                        color = VpIndigo,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = uiState.systemTime,
                    color = VpInk,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(VpBorder)
        )
    }
}