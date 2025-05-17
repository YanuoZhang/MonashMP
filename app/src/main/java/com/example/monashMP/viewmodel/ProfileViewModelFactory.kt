package com.example.monashMP.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.data.repository.UserFavoriteRepository
import com.example.monashMP.data.repository.UserRepository

class ProfileViewModelFactory(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val userFavoriteRepository: UserFavoriteRepository,
    private val userUid: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(userRepository, productRepository, userFavoriteRepository, userUid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
