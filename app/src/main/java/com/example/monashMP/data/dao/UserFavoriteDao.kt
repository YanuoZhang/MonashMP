package com.example.monashMP.data.dao

import UserFavoriteEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserFavoriteDao {

    /** Adds a product to the user's favorites. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: UserFavoriteEntity)

    /** Removes a product from the user's favorites. */
    @Delete
    suspend fun deleteFavorite(favorite: UserFavoriteEntity)

    /** Returns all favorites of a user. */
    @Query("SELECT * FROM user_favorites WHERE userUid = :userUid")
    suspend fun getFavoritesByUser(userUid: String): List<UserFavoriteEntity>

    /** Returns a Flow of product IDs favorited by the user. */
    @Query("SELECT productId FROM user_favorites WHERE userUid = :userUid")
    fun getFavoriteProductIdsFlow(userUid: String): Flow<List<Long>>

    /** Checks whether a product is in the user's favorites. */
    @Query("SELECT EXISTS(SELECT 1 FROM user_favorites WHERE userUid = :userUid AND productId = :productId)")
    suspend fun isFavorite(userUid: String, productId: Long): Boolean
}
