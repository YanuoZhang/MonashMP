package com.example.monashswap.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ContactMethodPreference() {
    val options = listOf("Email", "Phone", "Both")
    var selectedOption by remember { mutableStateOf("Email") } // 默认选中 Email
    val selectedColor = Color(0xFF3167B2)

    Column {

        Label("Preferred Contact Method")
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            options.forEach { option ->
                val isSelected = selectedOption == option
                OutlinedButton(
                    onClick = { selectedOption = option },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = if (isSelected) selectedColor else Color.Black
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) selectedColor else Color.LightGray
                    )
                ) {
                    Text(option)
                }
            }
        }
    }
}
