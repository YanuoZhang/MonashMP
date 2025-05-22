package com.example.monashMP.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun NoteCard(email: String)
{
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
//            Text("Note: The seller cannot reply in this app. Please check your email for any response.", fontSize = 14.sp)
            Text("Contact the seller directly via : ", fontSize = 14.sp)
            Text(email, fontWeight = FontWeight.Medium, color = Color(0xFF3B82F6))
        }
    }
}
