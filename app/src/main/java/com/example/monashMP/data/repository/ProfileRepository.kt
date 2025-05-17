package com.example.monashMP.repository

import UserFavoriteDao
import android.content.Context
import com.example.monashMP.data.dao.ProductDao
import com.example.monashMP.data.entity.ProductEntity
import com.example.monashMP.model.UserModel
import com.example.monashMP.utils.UserSessionManager
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val context: Context,
    private val productDao: ProductDao,
    private val userFavoriteDao: UserFavoriteDao,
    private val firebaseDatabase: FirebaseDatabase
) {
    private suspend fun getUserUid(): String {
        return UserSessionManager.getUserUid(context) ?: throw IllegalStateException("User UID not found")
    }

    suspend fun fetchUserInfoFromFirebase(): UserModel? {
        val uid = getUserUid()
        val snapshot = firebaseDatabase.reference.child("users").child(uid).get().await()
        return snapshot.getValue(UserModel::class.java)
    }

    suspend fun updateUserInfoToFirebase(user: UserModel) {
        val uid = getUserUid()
        firebaseDatabase.reference.child("users").child(uid).setValue(user).await()
    }

    suspend fun getUserPostedProducts(): List<ProductEntity> {
        val uid = getUserUid()
        return productDao.getUserProducts(uid)
    }

//    suspend fun getUserFavoriteProducts(): List<ProductEntity> {
//        val uid = getUserUid()
//        return productDao.getUserFavoriteProducts(uid)
//    }

//    suspend fun removeFavorite(productId: Long) {
//        val uid = getUserUid()
//        userFavoriteDao.removeFavorite(uid, productId)
//    }

    suspend fun deleteProduct(productId: Long) {
        productDao.deleteProductById(productId)
    }
}
