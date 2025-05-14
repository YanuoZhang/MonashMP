package com.example.monashMP

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.monashMP.ui.theme.MonashMPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonashMPApp()
        }
    }
}

@Composable
fun MonashMPApp() {
    MonashMPTheme {
        Surface {
            val navController = rememberNavController()
            AppNavHost(navController = navController)
        }
    }
}
