package com.example.monashMP.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.monashMP.data.dao.ProductDao

class ProductDetailViewModelFactory(
    private val productDao: ProductDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductDetailViewModel(productDao) as T
    }
}