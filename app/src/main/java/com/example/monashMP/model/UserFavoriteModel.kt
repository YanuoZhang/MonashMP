package com.example.monashMP.model

/**
 * Data model representing a user's favorite product,
 * used for Firebase or general-purpose storage (not Room).
 */
data class UserFavoriteModel(
    val userUid: String = "",
    val productId: Long = 0L,
    val favoriteDate: Long = System.currentTimeMillis()
)
