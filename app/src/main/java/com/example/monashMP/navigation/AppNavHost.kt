package com.example.monashMP.navigation

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
                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
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
                onRegisterSuccess = {
                    navController.navigate("Home")
                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                }
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

        composable("Post/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            if (productId != null) {
                val productViewModel = viewModel<ProductViewModel>(factory = factory)
                PostScreen(
                    productId = productId,
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

        }
        composable("ProductDetail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            if (productId != null) {
                val productViewModel = viewModel<ProductViewModel>(factory = factory)
                ProductDetailScreen(
                    productId = productId,
                    viewModel = productViewModel,
                    navController = navController
                )
            }
        }

        composable("MapView/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            val apiKey = context.getString(com.example.monashMP.R.string.openweather_api_key)
            if (productId != null) {
                val mapViewModel = viewModel<ProductViewModel>(factory = factory)
                MapScreen(
                    productId = productId,
                    viewModel = mapViewModel,
                    navController = navController,
                    apiKey = apiKey
                )
            }
        }


        composable("Profile") {
            val productViewModel = viewModel<ProductViewModel>(factory = factory)
            val authViewModel = viewModel<AuthViewModel>(factory = factory)
            ProfileScreen(
                onLogoutClick = {
                    authViewModel.logout(context)
                    navController.navigate("Login") {
                        popUpTo("Profile") { inclusive = true }
                    }
                },
                onProductCardClick = { productId, isDraft ->
                    if (isDraft)
                    {
                        navController.navigate("Post/${productId}")
                    } else {
                        navController.navigate("ProductDetail/${productId}")
                    }
                },
                viewModel = productViewModel,
                navController = navController
            )
        }
    }
}
