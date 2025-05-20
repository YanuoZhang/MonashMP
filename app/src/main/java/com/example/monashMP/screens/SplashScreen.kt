package com.example.monashMP.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigate: (String) -> Unit) {
    val isLoggedIn = true
    val isExpired = false
    LaunchedEffect(isLoggedIn) {
        delay(1000L)
        onNavigate(if (isLoggedIn && !isExpired) "Home" else "Login")
    }
}

