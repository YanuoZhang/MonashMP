package com.example.monashMP.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashMP.data.dao.ProductDao
import com.example.monashMP.data.entity.ProductEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productDao: ProductDao
) : ViewModel() {

    private val _product = MutableStateFlow<ProductEntity?>(null)
    val product: StateFlow<ProductEntity?> = _product


    fun fetchProduct(productId: Long) {
        viewModelScope.launch {
            _product.value = productDao.getProductById(productId)
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
