package com.example.monashMP.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashMP.data.entity.ProductEntity
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.data.repository.UserFavoriteRepository
import com.example.monashMP.data.repository.UserRepository
import com.example.monashMP.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val favoriteRepository: UserFavoriteRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _product = MutableStateFlow<ProductEntity?>(null)
    val product: StateFlow<ProductEntity?> = _product

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _sellerInfo = MutableStateFlow<UserModel?>(null)
    val sellerInfo: StateFlow<UserModel?> = _sellerInfo

    fun fetchProduct(productId: Long) {
        Log.d("product", _product.value.toString())
        viewModelScope.launch {
            productRepository.incrementAndGetViewCount(productId)
            _product.value = productRepository.getProductById(productId)
            _sellerInfo.value = userRepository.getUserByUid(_product.value?.sellerUid.orEmpty())
        }
    }

    fun checkFavoriteStatus(userUid: String, productId: Long) {
        viewModelScope.launch {
            _isFavorite.value = favoriteRepository.isFavorite(userUid, productId)
        }
    }

    fun toggleFavorite(userUid: String, productId: Long) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                favoriteRepository.removeFavorite(userUid, productId)
            } else {
                favoriteRepository.addFavorite(userUid, productId)
            }
            _isFavorite.value = !_isFavorite.value
        }
    }

    fun buildDayPreference(product: ProductEntity?): String {
        return when {
            product == null -> "--"
            product.dayPreferenceWeekdays && product.dayPreferenceWeekends -> "Weekdays & Weekends"
            product.dayPreferenceWeekdays -> "Weekdays"
            product.dayPreferenceWeekends -> "Weekends"
            else -> "--"
        }
    }
}

