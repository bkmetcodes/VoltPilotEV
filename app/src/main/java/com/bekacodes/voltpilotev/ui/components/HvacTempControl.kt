package com.bekacodes.voltpilotev.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekacodes.voltpilotev.ui.theme.VpBorder
import com.bekacodes.voltpilotev.ui.theme.VpCard
import com.bekacodes.voltpilotev.ui.theme.VpInk
import com.bekacodes.voltpilotev.ui.theme.VpMuted
import com.bekacodes.voltpilotev.ui.theme.VpSecondary

@Composable
fun HvacTempControl(
    roleLabel: String,
    tempF: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(VpCard)
                .border(1.dp, VpBorder, RoundedCornerShape(8.dp))
                .clickable(onClick = onDecrement),
            contentAlignment = Alignment.Center
        ) {
            Text("−", color = VpSecondary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = roleLabel,
                color = VpMuted,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
            Text(
                text = "${tempF}°F",
                color = VpInk,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(VpCard)
                .border(1.dp, VpBorder, RoundedCornerShape(8.dp))
                .clickable(onClick = onIncrement),
            contentAlignment = Alignment.Center
        ) {
            Text("+", color = VpSecondary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}