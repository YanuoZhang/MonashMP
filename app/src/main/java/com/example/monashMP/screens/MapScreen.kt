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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monashMP.components.CommonTopBar
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.viewmodel.MapViewModel
import com.example.monashMP.viewmodel.MapViewModelFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(productId: Long, productRepository: ProductRepository) {
    val viewModel: MapViewModel = viewModel(
        factory = MapViewModelFactory(productRepository)
    )

    LaunchedEffect(Unit) {
        viewModel.fetchProduct(productId)
    }

    val product by viewModel.product.collectAsState()

    Scaffold(
        topBar = { CommonTopBar(onBackClick = { }, "Map View") }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            product?.let { prod ->
                val latLng = viewModel.getLatLng(prod.location, prod.meetupPoint)

                if (latLng != null) {
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(latLng, 16f)
                    }

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        Marker(state = MarkerState(position = latLng))
                    }
                } else {
                    Text("Location not found for ${prod.location} - ${prod.meetupPoint}", modifier = Modifier.padding(16.dp))
                }
            } ?: run {
                Text("Loading product...", modifier = Modifier.padding(16.dp))
            }
        }
//            Image(
//                painter = painterResource(id = R.drawable.map_placeholder),
//                contentDescription = "Map Placeholder",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.fillMaxSize()
//            )
//
//            FloatingActionButton(
//                onClick = { /* 定位等操作 */ },
//                containerColor = Color(0xFF3167B2),
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .padding(16.dp)
//            ) {
//                Icon(Icons.Default.MyLocation, contentDescription = "Locate")
//            }
//            Box(modifier = Modifier.fillMaxSize()) {
//                FloatingWeatherInfo(
//                    condition = "Clear",
//                    temperature = "22℃",
//                    modifier = Modifier
//                        .align(Alignment.TopEnd)
//                        .padding(top = 24.dp, end = 16.dp)
//                )
//            }
        }
    }
