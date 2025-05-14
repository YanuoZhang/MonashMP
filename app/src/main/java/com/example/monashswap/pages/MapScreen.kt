package com.example.monashswap.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.monashswap.R
import com.example.monashswap.components.CommonTopBar
import com.example.monashswap.components.FloatingWeatherInfo

@Composable
fun MapScreen() {
    Scaffold(
        topBar = { CommonTopBar(onBackClick = { }, "Map View") }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.map_placeholder),
                contentDescription = "Map Placeholder",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            FloatingActionButton(
                onClick = { /* 定位等操作 */ },
                containerColor = Color(0xFF3167B2),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Locate")
            }
            Box(modifier = Modifier.fillMaxSize()) {
                FloatingWeatherInfo(
                    condition = "Clear",
                    temperature = "22℃",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 24.dp, end = 16.dp)
                )
            }
        }
    }
}
