package com.example.monashMP

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.navigation.AppNavHost
import com.example.monashMP.ui.theme.MonashMPTheme
import com.example.monashMP.utils.UserSessionManager
import com.example.monashMP.viewmodel.AppViewModelFactory
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        var isLoadingData = true
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { isLoadingData }
        isLoadingData = false

        enableEdgeToEdge()
        setContent {
            MonashMPTheme {
                val context = this
                val navController = rememberNavController()

                val productRepository = ProductRepository()
                val userUid = runBlocking { UserSessionManager.getUserUid(context) ?: "" }

                val factory = AppViewModelFactory(
                    productRepository = productRepository,
                    userUid = userUid
                )

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(navController = navController, factory = factory)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MonashMPTheme {
        Surface {
            val navController = rememberNavController()
            // Preview does not pass factory
            AppNavHost(navController = navController, factory = ViewModelProvider.NewInstanceFactory())
        }
    }
}