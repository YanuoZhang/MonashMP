package com.example.monashMP.model

import com.example.monashMP.data.entity.ProductEntity

data class ProductWithFavoriteStatus(
    val product: ProductEntity,
    val isFavorite: Boolean
)
