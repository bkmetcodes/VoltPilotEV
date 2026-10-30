package com.bekacodes.voltpilotev.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekacodes.voltpilotev.ui.theme.VpCyan
import com.bekacodes.voltpilotev.ui.theme.VpEmerald
import com.bekacodes.voltpilotev.ui.theme.VpInk
import com.bekacodes.voltpilotev.ui.theme.VpMuted
import com.bekacodes.voltpilotev.ui.theme.VpSecondary

@Composable
fun PowerRegenArcGauge(
    powerKw: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 7.dp.toPx()
            val arcPadding = 10.dp.toPx()
            val arcSize = Size(size.width - arcPadding * 2, size.height - arcPadding * 2)
            val topLeft = Offset(arcPadding, arcPadding)

            // Background 240-degree track (from 150 deg to 390 deg)
            drawArc(
                color = Color(0xFF1A2436),
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Active Power (Cyan clockwise from top) or Regen (Emerald counter-clockwise from top)
            val clamped = powerKw.coerceIn(-100, 250)
            if (clamped != 0) {
                val sweep = if (clamped > 0) {
                    (clamped / 250f) * 120f
                } else {
                    (clamped / 100f) * 120f
                }
                drawArc(
                    color = if (clamped > 0) VpCyan else VpEmerald,
                    startAngle = 270f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }

            // Top-center zero reference tick (Cyan)
            drawLine(
                color = VpCyan,
                start = Offset(size.width / 2f, arcPadding - 2.dp.toPx()),
                end = Offset(size.width / 2f, arcPadding + 6.dp.toPx()),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = powerKw.toString(),
                    color = VpInk,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text = "kW",
                    color = VpSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = VpSecondary)) { append("Power / ") }
                    withStyle(SpanStyle(color = if (powerKw < 0) VpEmerald else VpCyan)) {
                        append(if (powerKw < 0) "Regen" else if (powerKw > 0) "Drive" else "Ready")
                    }
                },
                fontSize = 9.sp
            )
        }

        Text(
            text = "REGEN",
            color = VpEmerald,
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 6.dp)
        )
        Text(
            text = "POWER",
            color = VpMuted,
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 6.dp)
        )
    }
}