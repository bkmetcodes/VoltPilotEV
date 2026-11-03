package com.bekacodes.voltpilotev.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
    val bodyBrush = Brush.verticalGradient(
        listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8), Color(0xFF475569))
    )
    val shadowColor = Color.Black.copy(alpha = 0.55f)
    val glassColor = Color(0xFF0F172A)
    val headlightActiveColor = VpCyan
    val headlightInactiveColor = Color(0xFFBAE6FD)
    val wheelOuterColor = Color(0xFF090D16)
    val wheelRimColor = Color(0xFF64748B)
    val wheelHubColor = VpCyan.copy(alpha = 0.6f)

    Spacer(
        modifier = modifier
            .semantics {
                contentDescription = if (isDriving) "EV sedan side profile in motion" else "EV sedan side profile stationary"
            }
            .drawWithCache {
                val w = size.width
                val h = size.height

                val shadowTopLeft = Offset(w * 0.06f, h * 0.78f)
                val shadowSize = Size(w * 0.88f, h * 0.16f)

                val bodyPath = Path().apply {
                    moveTo(w * 0.08f, h * 0.68f)
                    lineTo(w * 0.05f, h * 0.50f)
                    quadraticTo(w * 0.12f, h * 0.34f, w * 0.28f, h * 0.32f)
                    quadraticTo(w * 0.50f, h * 0.10f, w * 0.74f, h * 0.34f)
                    quadraticTo(w * 0.90f, h * 0.38f, w * 0.95f, h * 0.54f)
                    lineTo(w * 0.93f, h * 0.68f)
                    close()
                }

                val glassPath = Path().apply {
                    moveTo(w * 0.30f, h * 0.34f)
                    quadraticTo(w * 0.50f, h * 0.16f, w * 0.70f, h * 0.35f)
                    lineTo(w * 0.30f, h * 0.35f)
                    close()
                }

                val headlightStart = Offset(w * 0.88f, h * 0.46f)
                val headlightEnd = Offset(w * 0.94f, h * 0.50f)
                val headlightStrokeWidth = 2.5.dp.toPx()

                val wheelRadius = h * 0.20f
                val wheelXList = listOf(w * 0.24f, w * 0.77f)
                val wheelY = h * 0.68f
                val rimStroke = Stroke(1.5.dp.toPx())

                onDrawWithContent {
                    // Ground shadow
                    drawOval(
                        color = shadowColor,
                        topLeft = shadowTopLeft,
                        size = shadowSize
                    )

                    // Sleek Fastback Body Path
                    drawPath(
                        path = bodyPath,
                        brush = bodyBrush
                    )

                    // Greenhouse / Tinted Glass Window
                    drawPath(glassPath, glassColor)

                    // Cyan LED Daytime Running Headlight
                    drawLine(
                        color = if (isDriving) headlightActiveColor else headlightInactiveColor,
                        start = headlightStart,
                        end = headlightEnd,
                        strokeWidth = headlightStrokeWidth,
                        cap = StrokeCap.Round
                    )

                    // Rear & Front Alloy Wheels
                    wheelXList.forEach { wheelX ->
                        val center = Offset(wheelX, wheelY)
                        drawCircle(wheelOuterColor, radius = wheelRadius, center = center)
                        drawCircle(
                            wheelRimColor,
                            radius = wheelRadius * 0.7f,
                            center = center,
                            style = rimStroke
                        )
                        drawCircle(wheelHubColor, radius = wheelRadius * 0.22f, center = center)
                    }
                }
            }
    )
}
