package com.bekacodes.voltpilotev.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekacodes.voltpilotev.ui.theme.VpIndigo
import com.bekacodes.voltpilotev.ui.theme.VpInk
import com.bekacodes.voltpilotev.ui.theme.VpMuted

@Composable
fun AiCoPilotBar(
    promptText: String,
    isDriveRestricted: Boolean,
    onMicClick: () -> Unit,
    onBarClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .semantics {
                contentDescription = if (isDriveRestricted) {
                    "AI Co-Pilot bar in voice-only mode. Current prompt: $promptText. Tap to cycle preset voice commands."
                } else {
                    "AI Co-Pilot prompt bar. Current prompt: $promptText. Tap to open custom input dialog."
                }
                role = Role.Button
            }
            .clickable(onClick = onBarClick),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0E1324),
        border = BorderStroke(1.2.dp, VpIndigo.copy(alpha = 0.75f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left Indigo Mic Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(VpIndigo)
                    .semantics {
                        contentDescription = "Cycle AI voice command presets"
                        role = Role.Button
                    }
                    .clickable(onClick = onMicClick),
                contentAlignment = Alignment.Center
            ) {
                Text("🎙", fontSize = 16.sp)
            }

            // Center AI Co-Pilot Prompt
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = if (isDriveRestricted) "AI CO-PILOT (VOICE ONLY IN DRIVE)" else "AI CO-PILOT",
                    color = VpMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("✦", color = Color(0xFFA5B4FC), fontSize = 13.sp)
                    Text(
                        text = promptText,
                        color = VpInk,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right Audio Waveform Bars
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.5.dp),
                modifier = Modifier.padding(end = 4.dp)
            ) {
                listOf(8.dp, 16.dp, 22.dp, 12.dp, 18.dp, 9.dp).forEach { barHeight ->
                    Box(
                        modifier = Modifier
                            .width(2.5.dp)
                            .height(barHeight)
                            .clip(CircleShape)
                            .background(VpIndigo)
                    )
                }
            }
        }
    }
}
