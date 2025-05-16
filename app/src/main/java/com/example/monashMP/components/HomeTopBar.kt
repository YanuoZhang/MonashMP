package com.example.monashMP.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(font: FontFamily) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Monash MP",
                style = TextStyle(
                    fontFamily = font,
                    fontSize = 24.sp,
                    color = Color(0xFF006DAE)
                )
            )
        }
    )
}
