package com.bekacodes.voltpilotev.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekacodes.voltpilotev.ui.VoltPilotUiState
import com.bekacodes.voltpilotev.ui.theme.VpBorder
import com.bekacodes.voltpilotev.ui.theme.VpCard
import com.bekacodes.voltpilotev.ui.theme.VpCyan
import com.bekacodes.voltpilotev.ui.theme.VpEmerald
import com.bekacodes.voltpilotev.ui.theme.VpIndigo
import com.bekacodes.voltpilotev.ui.theme.VpInk
import com.bekacodes.voltpilotev.ui.theme.VpMuted
import com.bekacodes.voltpilotev.ui.theme.VpSecondary

@Composable
fun AiRecommendedStopCard(
    uiState: VoltPilotUiState,
    onRouteAndPrecondition: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stop = uiState.recommendedStop

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
            // Top Header & Title
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("✦", color = VpIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "AI Recommended Stop · ${stop.distanceMiles} mi ahead",
                            color = VpSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(VpEmerald)
                        )
                        Text(
                            text = "LIVE AVAILABILITY",
                            color = VpEmerald,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = VpInk,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("${stop.stationName} ")
                        }
                        withStyle(
                            SpanStyle(
                                color = VpSecondary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append("(${stop.maxPowerKw}kW)")
                        }
                    }
                )
            }

            // Interactive Route Preview Vector Map (Canvas)
            IntelligentRouteMapCanvas(
                stop = stop,
                isNavigationActive = uiState.isNavigationActive,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 6.dp)
            )

            // 3 Telemetry Step Pills (Arrive at 66% SoC -> Charge to 80% in 11 min -> Blue Bottle 120ft)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RouteTelemetryPill(
                    text = "Arrive at ${stop.arrivalSocPercent}% SoC",
                    isEmerald = true,
                    modifier = Modifier.weight(1f)
                )
                Text("→", color = VpMuted, fontSize = 12.sp)
                RouteTelemetryPill(
                    text = "Charge to ${stop.targetSocPercent}% in ${stop.chargeDurationMinutes} min",
                    isEmerald = true,
                    modifier = Modifier.weight(1.1f)
                )
                Text("→", color = VpMuted, fontSize = 12.sp)
                RouteTelemetryPill(
                    text = "☕ ${stop.amenityDistanceLabel}",
                    isEmerald = false,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(6.dp))

            // Large Cyan Primary CTA Button ("▶ Route & Precondition Battery")
            val ctaColor by animateColorAsState(
                targetValue = if (uiState.isNavigationActive) VpEmerald else VpCyan,
                label = "CtaBgColor"
            )
            val ctaLabel = if (uiState.isNavigationActive) {
                "✓ Routing Active · Preconditioning Battery (${uiState.batteryTempF}°F)"
            } else {
                "▶  Route & Precondition Battery"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ctaColor)
                    .semantics {
                        contentDescription = ctaLabel
                        role = Role.Button
                    }
                    .clickable(onClick = onRouteAndPrecondition),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ctaLabel,
                    color = Color(0xFF041019),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
