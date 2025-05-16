package com.example.monashMP.data.repository

import com.example.monashMP.data.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getFilteredProducts(
        title: String,
        category: String,
        minPrice: Float,
        maxPrice: Float,
        condition: String,
        locations: List<String>,
        sortBy: String
    ): Flow<List<ProductEntity>>

    suspend fun insertProduct(product: ProductEntity)
    suspend fun getAllProducts(): List<ProductEntity>
}