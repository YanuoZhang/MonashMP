package com.example.monashMP.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.monashMP.entity.UserFavorite
import kotlinx.coroutines.flow.Flow

@Dao
interface UserFavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: UserFavorite)

    @Query("DELETE FROM user_favorites WHERE userId = :userId AND productId = :productId")
    suspend fun removeFavorite(userId: Long, productId: Long)

    @Query("SELECT COUNT(*) FROM user_favorites WHERE productId = :productId")
    suspend fun getFavoriteCount(productId: Long): Int

    @Query("SELECT productId FROM user_favorites WHERE productId = :userId")
    suspend fun getUserFavoriteProductIds(userId: Long): List<Long>

    @Query("SELECT productId FROM user_favorites WHERE userId = :userId")
    fun getUserFavoriteProductIdsFlow(userId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(favorites: List<UserFavorite>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(userFavorite: UserFavorite): Long

    @Query("DELETE FROM user_favorites WHERE userId = :userId AND productId IN (:productIds)")
    suspend fun deleteFavorites(userId: Long, productIds: List<Long>)

    @Query("SELECT EXISTS(SELECT 1 FROM user_favorites WHERE userId = :userId AND productId = :productId LIMIT 1)")
    suspend fun isProductFavorite(userId: Long, productId: Long): Boolean

    @Query("DELETE FROM user_favorites WHERE userId = :userId AND productId = :productId")
    suspend fun deleteFavorite(userId: Long, productId: Long): Int

}
