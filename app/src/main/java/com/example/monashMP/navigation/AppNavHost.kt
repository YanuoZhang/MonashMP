package com.example.monashMP.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.monashMP.viewmodel.AuthViewModel
import com.example.monashMP.viewmodel.ProductViewModel

/**
 * Main navigation host for the application.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    factory: ViewModelProvider.Factory
) {
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "Splash") {

        composable("Splash") {
            SplashScreen(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo("Splash") { inclusive = true }
                }
            })
        }

        composable("Login") {
            val authViewModel = viewModel<AuthViewModel>(factory = factory)

            LoginScreen(
                viewModel = authViewModel,
                onRegisterClick = { email -> navController.navigate("Register/${email}") },
                onLoginSuccess = {
                    Log.d("LoginScreen", "onLoginSuccess() triggered!")
                    Toast.makeText(context, "Login succeeded!", Toast.LENGTH_SHORT).show()
                    navController.navigate("Home")
                }
            )
        }

        composable("Register/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val authViewModel = viewModel<AuthViewModel>(factory = factory)
            RegisterScreen(
                viewModel = authViewModel,
                onBackClick = { navController.popBackStack() },
                email = email,
                onRegisterSuccess = { navController.navigate("Home") }
            )
        }

        composable("Home") {
            val productViewModel = viewModel<ProductViewModel>(factory = factory)
            MonashMPScreen(
                navController = navController,
                viewModel = productViewModel
            )
        }

        composable("Post") {
            val productViewModel = viewModel<ProductViewModel>(factory = factory)
            PostScreen(
                viewModel = productViewModel,
                navController = navController,
                onPostResult = { success ->
                    if (success) {
                        Toast.makeText(context, "Post created successfully!", Toast.LENGTH_SHORT).show()
                        navController.navigate("Home") {
                            popUpTo("post") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Failed to post item", Toast.LENGTH_SHORT).show()
                    }
                }
            )
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
