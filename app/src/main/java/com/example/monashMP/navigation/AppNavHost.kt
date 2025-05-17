package com.example.monashMP.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.monashMP.data.database.AppDatabase
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.screens.LoginScreen
import com.example.monashMP.screens.MonashMPScreen
import com.example.monashMP.screens.PostScreen
import com.example.monashMP.screens.ProductDetailScreen
import com.example.monashMP.screens.RegisterScreen
import com.example.monashMP.screens.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val productDao = AppDatabase.getDatabase(context).productDao()
    val repository = ProductRepository(productDao)

    NavHost(navController = navController, startDestination = "Splash") {
        composable("Splash") { SplashScreen(onNavigate = { route ->
            navController.navigate(route) {
                popUpTo("Splash") { inclusive = true }
            }
        }) }
        composable("Login") { LoginScreen(
            onRegisterClick = { email -> navController.navigate("Register/${email}") },
            onLoginSuccess = {
                navController.navigate("Home")
            })
        }
        composable("Register/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            RegisterScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                email = email,
                onRegisterSuccess = { navController.navigate("Home") }
            )
        }
        composable("Home") {
            MonashMPScreen(navController, repository)
        }
        composable("Post") {
            PostScreen(
                navController = navController,
                repository = repository,
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
        composable("ProductDetail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()

            if (productId != null) {
                ProductDetailScreen(
                    productId = productId,
                    navController = navController
                )
            }
        }


    }
}
