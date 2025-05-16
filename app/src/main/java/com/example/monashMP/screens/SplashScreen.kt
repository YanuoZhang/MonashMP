package com.example.monashMP.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.monashMP.utils.UserSessionManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val sessionMaxAge = remember { 7 * 24 * 60 * 60 * 1000L }
    val isLoggedIn by UserSessionManager.isLoggedInFlow(context).collectAsState(initial = false)
    val isExpired by UserSessionManager.isSessionExpired(context, sessionMaxAge).collectAsState(initial = false)

    LaunchedEffect(isLoggedIn) {
        delay(1000L)
        onNavigate(if (isLoggedIn && !isExpired) "home" else "login")
    }
}

