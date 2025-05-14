package com.example.monashMP.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Label(title: String)
{
    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(8.dp))
}