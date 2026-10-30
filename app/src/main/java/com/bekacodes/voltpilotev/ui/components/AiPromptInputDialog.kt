package com.bekacodes.voltpilotev.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekacodes.voltpilotev.ui.theme.VpBorder
import com.bekacodes.voltpilotev.ui.theme.VpCard
import com.bekacodes.voltpilotev.ui.theme.VpCardElevated
import com.bekacodes.voltpilotev.ui.theme.VpCyan
import com.bekacodes.voltpilotev.ui.theme.VpIndigo
import com.bekacodes.voltpilotev.ui.theme.VpInk
import com.bekacodes.voltpilotev.ui.theme.VpMuted
import com.bekacodes.voltpilotev.ui.theme.VpSecondary

@Composable
fun AiPromptInputDialog(
    initialText: String,
    presets: List<String>,
    onTextChanged: (String) -> Unit,
    onSelectPreset: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VpCardElevated,
        titleContentColor = VpInk,
        textContentColor = VpSecondary,
        title = {
            Text("✦ AI Route & Charger Co-Pilot", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = initialText,
                    onValueChange = onTextChanged,
                    placeholder = {
                        Text(
                            "e.g. Find a 350kW charger within 50 miles",
                            color = VpMuted
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = VpInk,
                        unfocusedTextColor = VpInk,
                        focusedBorderColor = VpCyan,
                        unfocusedBorderColor = VpIndigo
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Or tap a voice command preset:", color = VpMuted, fontSize = 11.sp)
                presets.forEach { preset ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VpCard)
                            .border(1.dp, VpBorder, RoundedCornerShape(8.dp))
                            .clickable { onSelectPreset(preset) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("✦ $preset", color = VpInk, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = VpCyan)
            ) {
                Text("Search Route", color = Color(0xFF041019), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = VpSecondary)
            }
        }
    )
}