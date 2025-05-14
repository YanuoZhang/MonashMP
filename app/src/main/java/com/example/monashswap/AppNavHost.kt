package com.example.monashswap

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.monashswap.screens.LoginScreen
import com.example.monashswap.screens.MonashMPScreen
import com.example.monashswap.screens.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(onNavigate = { route ->
            navController.navigate(route) {
                popUpTo("splash") { inclusive = true }
            }
        }) }
        composable("login") { LoginScreen(navController) }
        composable("main") { MonashMPScreen(navController) }
    }
}
