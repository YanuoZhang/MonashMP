package com.example.monashMP.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.monashMP.screens.LoginScreen
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
            onRegisterClick = { email -> navController.navigate("register/${email}") },
            onLoginSuccess = {
                navController.navigate("main") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("register/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            RegisterScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                email = email,
                onRegisterSuccess = { navController.navigate("home") }
            )
        }
        composable("main") { MonashMPScreen(navController) }
    }
}
