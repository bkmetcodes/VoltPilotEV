package com.bekacodes.voltpilotev.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekacodes.voltpilotev.ui.ChargerStopRecommendation
import com.bekacodes.voltpilotev.ui.theme.VpBorder
import com.bekacodes.voltpilotev.ui.theme.VpCyan
import com.bekacodes.voltpilotev.ui.theme.VpEmerald
import com.bekacodes.voltpilotev.ui.theme.VpInk
import com.bekacodes.voltpilotev.ui.theme.VpMuted
import com.bekacodes.voltpilotev.ui.theme.VpSecondary

/**
 * Custom Canvas recreating the Kettleman City I-5 N Intelligent Route Preview Map from Figma.
 */
@Composable
fun IntelligentRouteMapCanvas(
    stop: ChargerStopRecommendation,
    isNavigationActive: Boolean,
    modifier: Modifier = Modifier
) {
    val gridColor = Color(0xFF152033)
    val altRoadColor = Color(0xFF283548)
    val originBgColor = Color(0xFF0D2235)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF080D16))
            .border(1.dp, VpBorder, RoundedCornerShape(12.dp))
            .semantics {
                contentDescription = "Intelligent route map to ${stop.stationName} via ${stop.highwayRoute}, ${stop.distanceMiles} miles ahead"
            }
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val w = size.width
                    val h = size.height

                    // Grid & road parameters
                    val horizontalGridLines = (1..4).map { i ->
                        val y = h * (i / 5f)
                        Offset(w * 0.45f, y) to Offset(w * 0.92f, y)
                    }
                    val verticalGridLines = (1..3).map { i ->
                        val x = w * (0.48f + i * 0.11f)
                        Offset(x - 18.dp.toPx(), h * 0.22f) to Offset(x + 18.dp.toPx(), h * 0.85f)
                    }

                    // Secondary gray road path
                    val altPath = Path().apply {
                        moveTo(w * 0.28f, h * 0.78f)
                        lineTo(w * 0.36f, h * 0.78f)
                        lineTo(w * 0.48f, h * 0.54f)
                        lineTo(w * 0.80f, h * 0.32f)
                    }
                    val altStroke = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )

                    // Primary glowing cyan route path
                    val routePath = Path().apply {
                        moveTo(w * 0.28f, h * 0.78f)
                        lineTo(w * 0.35f, h * 0.78f)
                        lineTo(w * 0.46f, h * 0.52f)
                        lineTo(w * 0.65f, h * 0.42f)
                        lineTo(w * 0.80f, h * 0.30f)
                    }
                    val glowStroke = Stroke(
                        width = 9.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                    val coreStroke = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )

                    // Origin & Destination Nodes
                    val origin = Offset(w * 0.28f, h * 0.78f)
                    val dest = Offset(w * 0.80f, h * 0.30f)
                    val destTopLeft = Offset(dest.x - 12.dp.toPx(), dest.y - 12.dp.toPx())
                    val destSize = Size(24.dp.toPx(), 24.dp.toPx())
                    val destCorner = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    val destBorderStroke = Stroke(1.5.dp.toPx())
                    val originRingStroke = Stroke(1.5.dp.toPx())

                    val gridStrokeWidth = 1.dp.toPx()

                    onDrawWithContent {
                        // Subtle dark terrain polygons & road grid
                        horizontalGridLines.forEach { (start, end) ->
                            drawLine(gridColor, start, end, strokeWidth = gridStrokeWidth)
                        }
                        verticalGridLines.forEach { (start, end) ->
                            drawLine(gridColor, start, end, strokeWidth = gridStrokeWidth)
                        }

                        // Secondary gray road path
                        drawPath(path = altPath, color = altRoadColor, style = altStroke)

                        // Outer cyan glow
                        drawPath(
                            path = routePath,
                            color = VpCyan.copy(alpha = if (isNavigationActive) 0.40f else 0.22f),
                            style = glowStroke
                        )
                        // Core cyan line
                        drawPath(path = routePath, color = VpCyan, style = coreStroke)

                        // Origin Vehicle Node Circle
                        drawCircle(originBgColor, radius = 10.dp.toPx(), center = origin)
                        drawCircle(
                            VpCyan,
                            radius = 10.dp.toPx(),
                            center = origin,
                            style = originRingStroke
                        )
                        drawCircle(VpCyan, radius = 3.5.dp.toPx(), center = origin)

                        // Destination Emerald Charger Pin Box
                        drawRoundRect(
                            color = VpEmerald,
                            topLeft = destTopLeft,
                            size = destSize,
                            cornerRadius = destCorner
                        )
                        drawRoundRect(
                            color = Color.White,
                            topLeft = destTopLeft,
                            size = destSize,
                            cornerRadius = destCorner,
                            style = destBorderStroke
                        )
                    }
                }
        )

        // Map Overlays & Labels matching Figma screenshot
        Text(
            text = "● INTELLIGENT ROUTE PREVIEW",
            color = VpCyan.copy(alpha = 0.8f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(start = 12.dp, top = 10.dp)
        )

        Text(
            text = "KETTLEMAN CITY",
            color = VpMuted.copy(alpha = 0.55f),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 24.dp, top = 26.dp)
        )

        // Highway Shield Badge [I-5 N]
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 24.dp, y = (-8).dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, VpBorder, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(stop.highwayRoute, color = VpInk, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }

        // Destination Station Label & Stalls Count
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 18.dp, end = 24.dp)
        ) {
            Text(
                text = "HyperVolt",
                color = VpInk,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${stop.availableStalls} stalls available",
                color = VpEmerald,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "☕ ${stop.nearbyAmenity}",
                color = VpSecondary,
                fontSize = 9.sp
            )
        }

        // Bottom-Left Distance Label
        Text(
            text = "${stop.distanceMiles} mi · via ${stop.highwayRoute}",
            color = VpSecondary,
            fontSize = 9.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 8.dp)
        )

        // Bottom-Right Compass Badge [N ↑]
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 8.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, VpBorder, RoundedCornerShape(5.dp))
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Text("N ↑", color = VpInk, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
