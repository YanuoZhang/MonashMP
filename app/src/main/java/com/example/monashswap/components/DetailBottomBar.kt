package com.example.monashswap.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DetailBottomBar(
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onContactClick: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)) // gray-200
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onSaveClick,
                border = BorderStroke(1.dp, if (isSaved) Color.Red else Color(0xFF006DAE)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isSaved) Color.Red else Color(0xFF006DAE)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Save",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Save")
            }

            Button(
                onClick = onContactClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006DAE)),
                modifier = Modifier
                    .weight(2f)
                    .height(48.dp)
            ) {
                Text(text = "Contact Seller")
            }
        }
    }
}
