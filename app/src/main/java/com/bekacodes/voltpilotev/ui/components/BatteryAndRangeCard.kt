package com.bekacodes.voltpilotev.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekacodes.voltpilotev.ui.VoltPilotUiState
import com.bekacodes.voltpilotev.ui.theme.VpBorder
import com.bekacodes.voltpilotev.ui.theme.VpCard
import com.bekacodes.voltpilotev.ui.theme.VpEmerald
import com.bekacodes.voltpilotev.ui.theme.VpInk
import com.bekacodes.voltpilotev.ui.theme.VpMuted
import com.bekacodes.voltpilotev.ui.theme.VpSecondary

@Composable
fun BatteryAndRangeCard(
    uiState: VoltPilotUiState,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BATTERY & RANGE",
                    color = VpSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
                // Battery Icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(width = 18.dp, height = 10.dp)
                            .border(1.2.dp, VpEmerald, RoundedCornerShape(2.dp))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth((uiState.socPercent / 100f).coerceIn(0.1f, 1f))
                                .background(VpEmerald)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(width = 2.dp, height = 5.dp)
                            .background(VpEmerald)
                    )
                }
            }

            // 78% SoC · 242 mi Range
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = VpEmerald,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("${uiState.socPercent}% ")
                    }
                    withStyle(
                        SpanStyle(
                            color = VpInk,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Normal
                        )
                    ) {
                        append("SoC · ")
                    }
                    withStyle(
                        SpanStyle(
                            color = VpInk,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("${uiState.rangeMiles} mi ")
                    }
                    withStyle(
                        SpanStyle(
                            color = VpMuted,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    ) {
                        append("Range")
                    }
                }
            )

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1D283A))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth((uiState.socPercent / 100f).coerceIn(0.05f, 1f))
                        .clip(CircleShape)
                        .background(VpEmerald)
                )
            }

            // Architecture & Tire Pressure Footer
            val psiText = uiState.tirePressuresPsi.joinToString(" | ")
            Text(
                text = "${uiState.architectureVolts}V Architecture · Batt Temp ${uiState.batteryTempF}°F · 4-Tire PSI: $psiText",
                color = VpMuted,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}