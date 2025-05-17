package com.example.monashMP.data.repository

import com.example.monashMP.data.dao.ProductDao
import com.example.monashMP.data.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepository(private val productDao: ProductDao) {

    fun getFilteredProducts(
        title: String,
        category: String,
        minPrice: Float,
        maxPrice: Float,
        condition: String,
        locations: List<String>,
        sortBy: String
    ): Flow<List<ProductEntity>> {
        return productDao.getAllProductsFlow().map { list ->
            val filtered = list.filter { product ->
                (title.isBlank() || product.title.contains(title, ignoreCase = true)) &&
                        (category == "All" || product.category == category) &&
                        product.price in minPrice..maxPrice &&
                        (condition == "All" || product.condition == condition) &&
                        (locations.isEmpty() || product.location in locations)
            }

            when (sortBy) {
                "newest" -> filtered.sortedByDescending { it.createdAt }
                "lowest" -> filtered.sortedBy { it.price }
                "highest" -> filtered.sortedByDescending { it.price }
                else -> filtered.sortedByDescending { it.createdAt }
            }
        }
    }

    suspend fun insertProduct(product: ProductEntity): Long {
        return productDao.insertProduct(product)
    }

//    suspend fun getAllProducts(): List<ProductEntity> {
//        return productDao.getAllProducts()
//    }

    suspend fun getProductById(productId: Long): ProductEntity? {
        return productDao.getProductById(productId)
    }

    suspend fun incrementAndGetViewCount(productId: Long): Int {
        productDao.incrementViewCount(productId)
        return productDao.getProductById(productId)?.viewCount ?: 0
    }

}