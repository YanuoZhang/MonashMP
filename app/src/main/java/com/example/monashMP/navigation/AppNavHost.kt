package com.example.monashMP.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.monashMP.screens.LoginScreen
import com.example.monashMP.screens.MapScreen
import com.example.monashMP.screens.MonashMPScreen
import com.example.monashMP.screens.PostScreen
import com.example.monashMP.screens.ProductDetailScreen
import com.example.monashMP.screens.ProfileScreen
import com.example.monashMP.screens.RegisterScreen
import com.example.monashMP.screens.SplashScreen

/**
 * Main navigation host for the application.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
) {
    NavHost(navController = navController, startDestination = "Splash") {
        composable("Splash") {
            SplashScreen(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo("Splash") { inclusive = true }
                }
            })
        }
        composable("Login") {
            LoginScreen()
        }
        composable("Register") {
            RegisterScreen()
        }
        composable("Home") {
            MonashMPScreen(navController)
        }
        composable("Post") {
            PostScreen(navController)
        }
        composable("ProductDetail") {
            ProductDetailScreen()
        }
        composable("MapView") { backStackEntry ->
            MapScreen()
        }
        composable("Profile") {
            ProfileScreen(navController)
        }
    }
}
