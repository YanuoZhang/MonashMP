package com.example.monashMP.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashMP.data.entity.ProductEntity
import com.example.monashMP.data.repository.ProductRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _product = MutableStateFlow<ProductEntity?>(null)
    val product: StateFlow<ProductEntity?> = _product

    val campusLocationMap = mapOf(
        "Clayton" to mapOf(
            "Monash Sport" to LatLng(-37.9116, 145.1340),
            "SML Library" to LatLng(-37.9110, 145.1335),
            "LTB" to LatLng(-37.9102, 145.1347),
            "Monash CLUB" to LatLng(-37.9125, 145.1329),
            "Bus stop" to LatLng(-37.9120, 145.1310),
            "Learning Village" to LatLng(-37.9107, 145.1330)
        ),
        "Caulfield" to mapOf(
            "Building H" to LatLng(-37.8770, 145.0450),
            "Monash sport" to LatLng(-37.8765, 145.0462),
            "Library" to LatLng(-37.8768, 145.0455)
        )
    )

    fun getLatLng(location: String, meetupPoint: String): LatLng? {
        return campusLocationMap[location]?.get(meetupPoint)
    }

    fun fetchProduct(productId: Long) {
        viewModelScope.launch {
            _product.value = productRepository.getProductById(productId)
        }
    }
}
