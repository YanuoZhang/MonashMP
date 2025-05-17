package com.example.monashMP.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.monashMP.data.database.AppDatabase
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.data.repository.UserFavoriteRepository
import com.example.monashMP.data.repository.UserRepository
import com.example.monashMP.screens.LoginScreen
import com.example.monashMP.screens.MapScreen
import com.example.monashMP.screens.MonashMPScreen
import com.example.monashMP.screens.PostScreen
import com.example.monashMP.screens.ProductDetailScreen
import com.example.monashMP.screens.ProfileScreen
import com.example.monashMP.screens.RegisterScreen
import com.example.monashMP.screens.SplashScreen
import com.example.monashMP.utils.UserSessionManager
import com.example.monashMP.viewmodel.ProfileViewModel
import com.example.monashMP.viewmodel.ProfileViewModelFactory
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.runBlocking

@Composable
fun AppNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val productDao = database.productDao()
    val userFavoriteDao = AppDatabase.getDatabase(context).userFavoriteDao()
    val userRepository = UserRepository(context)
    val productRepository = ProductRepository(productDao)
    val userFavoriteRepository = UserFavoriteRepository(userFavoriteDao)
    val firebaseDatabase = FirebaseDatabase.getInstance()


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

            val userUid = runBlocking { UserSessionManager.getUserUid(context) ?: "" }
            MonashMPScreen(navController, productRepository, userFavoriteRepository, userUid)
        }
        composable("Post") {
            PostScreen(
                navController = navController,
                repository = productRepository,
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
                    productRepository = productRepository,
                    favoriteRepository = userFavoriteRepository,
                    userRepository = userRepository,
                    navController = navController
                )
            }
        }
        composable("MapView/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            if (productId != null) {
                MapScreen(
                    productId,
                    productRepository = productRepository,
                )
            }

        }
        composable("Profile") {
            val userUid = runBlocking { UserSessionManager.getUserUid(context) ?: "" }
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(
                    userRepository,
                    productRepository ,
                    userFavoriteRepository,
                    userUid
                )
            )

            ProfileScreen(
                onLogoutClick = {
                    profileViewModel.logout(context = context, navController = navController)
                },
                onProductCardClick = { productId ->
                    navController.navigate("ProductDetail/$productId")
                },
                viewModel = profileViewModel,
                navController = navController
            )
        }

    }
}
