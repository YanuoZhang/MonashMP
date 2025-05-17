package com.example.monashMP.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashMP.data.entity.ProductEntity
import com.example.monashMP.model.UserModel
import com.example.monashMP.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _userInfo = MutableStateFlow<UserModel?>(null)
    val userInfo: StateFlow<UserModel?> = _userInfo

    private val _userProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    val userProducts: StateFlow<List<ProductEntity>> = _userProducts

    private val _userFavoriteProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    val userFavoriteProducts: StateFlow<List<ProductEntity>> = _userFavoriteProducts

    fun refreshAllData() {
        fetchUserInfo()
        getUserPostProducts()
//        getUserFavoriteProducts()
    }

    fun fetchUserInfo() {
        viewModelScope.launch {
            _userInfo.value = repository.fetchUserInfoFromFirebase()
        }
    }

    fun getUserPostProducts() {
        viewModelScope.launch {
            _userProducts.value = repository.getUserPostedProducts()
        }
    }

//    fun getUserFavoriteProducts() {
//        viewModelScope.launch {
//            _userFavoriteProducts.value = repository.getUserFavoriteProducts()
//        }
//    }

//    fun removeFavorite(productId: Long) {
//        viewModelScope.launch {
//            repository.removeFavoriteProduct(productId)
//            _userFavoriteProducts.value = _userFavoriteProducts.value.filterNot { it.productId == productId }
//        }
//    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            _userProducts.value = _userProducts.value.filterNot { it.productId == productId }
        }
    }

//    fun updateUserInfo(updatedUser: UserEntity) {
//        viewModelScope.launch {
//            repository.updateUserInfo(updatedUser)
//            _userInfo.value = updatedUser
//        }
//    }
}
