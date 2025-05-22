package com.example.monashMP.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DescriptionSection(
    intro: String,
    extraNotes: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Section Title
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937) // gray-800
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Intro paragraph
        Text(
            text = intro,
            fontSize = 14.sp,
            color = Color(0xFF4B5563), // gray-600
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Extra notes
        Text(
            text = extraNotes,
            fontSize = 14.sp,
            color = Color(0xFF4B5563),
            lineHeight = 20.sp
        )
    }
}
