package com.example.monashMP.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ErrorToast(
    message: String,
    onDismiss: () -> Unit,
    durationMillis: Long = 3000
) {
    if (message.isNotEmpty()) {
        LaunchedEffect(message) {
            delay(durationMillis)
            onDismiss()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (message.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Red, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

    }
}