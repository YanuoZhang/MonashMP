package com.example.monashMP

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.monashMP.pages.LoginScreen
import com.example.monashMP.screens.MonashMPScreen
import com.example.monashMP.screens.RegisterScreen
import com.example.monashMP.screens.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(onNavigate = { route ->
            navController.navigate(route) {
                popUpTo("splash") { inclusive = true }
            }
        }) }
        composable("login") { LoginScreen(
            onRegisterClick = {
                navController.navigate("register")
            },
            onLoginSuccess = {
                navController.navigate("main") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("register") {
            RegisterScreen()
        }
        composable("main") { MonashMPScreen(navController) }
    }
}
