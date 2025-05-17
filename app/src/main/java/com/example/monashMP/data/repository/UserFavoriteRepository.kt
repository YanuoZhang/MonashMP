package com.example.monashMP.data.repository

import UserFavoriteDao
import UserFavoriteEntity
import kotlinx.coroutines.flow.Flow

class UserFavoriteRepository(private val userFavoriteDao: UserFavoriteDao) {

    suspend fun addFavorite(userUid: String, productId: Long) {
        userFavoriteDao.insertFavorite(UserFavoriteEntity(userUid, productId))
    }

    suspend fun removeFavorite(userUid: String, productId: Long) {
        userFavoriteDao.deleteFavorite(UserFavoriteEntity(userUid, productId))
    }

    suspend fun isFavorite(userUid: String, productId: Long): Boolean {
        return userFavoriteDao.isFavorite(userUid, productId)
    }

    fun getFavoriteProductIdsFlow(userUid: String): Flow<List<Long>> {
        return userFavoriteDao.getFavoriteProductIdsFlow(userUid)
    }

    suspend fun getFavoritesByUser(userUid: String): List<UserFavoriteEntity> {
        return userFavoriteDao.getFavoritesByUser(userUid)
    }
}