package com.example.monashMP.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.data.repository.UserFavoriteRepository

class HomeViewModelFactory(
    private val productRepository: ProductRepository,
    private val userFavoriteRepository: UserFavoriteRepository,
    private val userUid: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(productRepository, userFavoriteRepository, userUid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
