package com.example.monashMP.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.monashMP.components.CommonTopBar
import com.example.monashMP.components.FloatingWeatherInfo
import com.example.monashMP.viewmodel.ProductViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    productId: Long,
    viewModel: ProductViewModel,
    navController: NavHostController,
    apiKey: String
) {
    // Collect product and weather state from ViewModel
    val product by viewModel.formState.collectAsState()
    val weather by viewModel.weather.collectAsState()

    // Fetch product detail by ID
    LaunchedEffect(Unit) {
        viewModel.loadProductForDetail(productId)
    }

    // Calculate LatLng from product info (if available)
    val latLng: LatLng? = remember(product) {
        product?.let {
            viewModel.getLatLng(it.location, it.meetupPoint)
        }
    }

    // Fetch weather data for the selected LatLng
    LaunchedEffect(latLng) {
        if (latLng != null) {
            viewModel.fetchWeather(latLng.latitude, latLng.longitude, apiKey)
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Map View",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                product == null -> {
                    Text("Loading product...", modifier = Modifier.padding(16.dp))
                }

                latLng == null -> {
                    Text(
                        "Location not found for ${product?.location} - ${product?.meetupPoint}",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(latLng, 16f)
                    }

                    // Display Google Map with Marker
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        Marker(state = MarkerState(position = latLng))
                    }

                    // Overlay weather information if available
                    if (weather != null) {
                        FloatingWeatherInfo(
                            condition = weather?.weather?.firstOrNull()?.main ?: "--",
                            temperature = "${weather?.main?.temp ?: "--"}℃",
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 24.dp, end = 16.dp)
                        )
                    }
                }
            }
        }
    }
}
