package com.example.monashMP.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun RegisterPrompt(onRegisterClick: () -> Unit) {
    val annotatedText = buildAnnotatedString {
        append("Don't have an account? ")

        pushStringAnnotation(
            tag = "REGISTER",
            annotation = "register"
        )
        withStyle(
            style = SpanStyle(
                color = Color(0xFF2563EB), // 蓝色
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.None
            )
        ) {
            append("Register")
        }
        pop()
    }

    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations("REGISTER", offset, offset)
                .firstOrNull()?.let {
                    onRegisterClick()
                }
        },
        style = TextStyle(
            color = Color(0xFF4B5563), // 默认文字颜色灰色
            fontSize = 14.sp
        ),
        modifier = Modifier
    )
}
