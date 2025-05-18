package com.example.monashMP.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.data.repository.UserRepository
import com.example.monashMP.model.ProfileItem
import com.example.monashMP.model.ProfileItemType
import com.example.monashMP.model.UserModel
import com.example.monashMP.utils.UserSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val favoriteRepository: UserFavoriteRepository,
    private val userUid: String
) : ViewModel() {

    private val _userInfo = MutableStateFlow<UserModel?>(null)
    val userInfo: StateFlow<UserModel?> = _userInfo

    private val _userProducts = MutableStateFlow<List<ProfileItem>>(emptyList())
    val userProducts: StateFlow<List<ProfileItem>> = _userProducts

    private val _userFavoriteProducts = MutableStateFlow<List<ProfileItem>>(emptyList())
    val userFavoriteProducts: StateFlow<List<ProfileItem>> = _userFavoriteProducts


    fun refreshAllData() {
        fetchUserInfo()
        loadUserPostedProducts()
        loadUserFavoriteProducts()
    }

    fun fetchUserInfo() {
        viewModelScope.launch {
            _userInfo.value = userRepository.getUserByUid(userUid)
        }
    }

    fun loadUserPostedProducts() {
        viewModelScope.launch {
            val posted = productRepository.getUserProducts(userUid)
            _userProducts.value = posted.map {
                ProfileItem(
                    id = it.productId,
                    title = it.title,
                    price = "$${it.price}",
                    cover = it.photos.firstOrNull() ?: "",
                    type = ProfileItemType.Posted
                )
            }
        }
    }

    fun loadUserFavoriteProducts() {
        viewModelScope.launch {
            val favoriteIds = favoriteRepository.getFavoritesByUser(userUid).map { it.productId }
            val favoriteProducts = favoriteIds.mapNotNull { productRepository.getProductById(it) }
            _userFavoriteProducts.value = favoriteProducts.map {
                ProfileItem(
                    id = it.productId,
                    title = it.title,
                    price = "$${it.price}",
                    cover = it.photos.firstOrNull() ?: "",
                    type = ProfileItemType.Saved
                )
            }
        }
    }

    fun deleteProduct(item: ProfileItem) {
        viewModelScope.launch {
            productRepository.deleteProduct(item.id)
            _userProducts.value = _userProducts.value.filterNot { it.id == item.id }
        }
    }

    fun logout(context: Context, navController: NavHostController) {
        viewModelScope.launch {
            UserSessionManager.clearSession(context)
            navController.navigate("Login") {
                popUpTo("Profile") { inclusive = true }
            }
        }
    }

}


