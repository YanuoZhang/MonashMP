package com.example.monashswap.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.monashswap.utils.UserSessionManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val isLoggedIn by UserSessionManager.isLoggedInFlow(context).collectAsState(false)

    LaunchedEffect(isLoggedIn) {
        delay(1000L) // 可选延迟用于视觉闪屏
        onNavigate(if (isLoggedIn) "main" else "login")
    }
}

