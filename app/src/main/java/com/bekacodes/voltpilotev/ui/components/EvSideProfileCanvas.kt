package com.bekacodes.voltpilotev.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.bekacodes.voltpilotev.ui.theme.VpCyan

/**
 * Draws the sleek silver metallic EV sports sedan side profile from the Figma design.
 */
@Composable
fun EvSideProfileCanvas(
    isDriving: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Ground shadow
        drawOval(
            color = Color.Black.copy(alpha = 0.55f),
            topLeft = Offset(w * 0.06f, h * 0.78f),
            size = Size(w * 0.88f, h * 0.16f)
        )

        // Sleek Fastback Body Path
        val bodyPath = Path().apply {
            moveTo(w * 0.08f, h * 0.68f)
            lineTo(w * 0.05f, h * 0.50f)
            quadraticTo(w * 0.12f, h * 0.34f, w * 0.28f, h * 0.32f)
            // Roofline arch
            quadraticTo(w * 0.50f, h * 0.10f, w * 0.74f, h * 0.34f)
            // Front hood
            quadraticTo(w * 0.90f, h * 0.38f, w * 0.95f, h * 0.54f)
            lineTo(w * 0.93f, h * 0.68f)
            close()
        }

        drawPath(
            path = bodyPath,
            brush = Brush.verticalGradient(
                listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8), Color(0xFF475569))
            )
        )

        // Greenhouse / Tinted Glass Window
        val glassPath = Path().apply {
            moveTo(w * 0.30f, h * 0.34f)
            quadraticTo(w * 0.50f, h * 0.16f, w * 0.70f, h * 0.35f)
            lineTo(w * 0.30f, h * 0.35f)
            close()
        }
        drawPath(glassPath, Color(0xFF0F172A))

        // Cyan LED Daytime Running Headlight
        drawLine(
            color = if (isDriving) VpCyan else Color(0xFFBAE6FD),
            start = Offset(w * 0.88f, h * 0.46f),
            end = Offset(w * 0.94f, h * 0.50f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Rear & Front Alloy Wheels
        val wheelRadius = h * 0.20f
        listOf(w * 0.24f, w * 0.77f).forEach { wheelX ->
            val center = Offset(wheelX, h * 0.68f)
            drawCircle(Color(0xFF090D16), radius = wheelRadius, center = center)
            drawCircle(
                Color(0xFF64748B),
                radius = wheelRadius * 0.7f,
                center = center,
                style = Stroke(1.5.dp.toPx())
            )
            drawCircle(VpCyan.copy(alpha = 0.6f), radius = wheelRadius * 0.22f, center = center)
        }
    }
}