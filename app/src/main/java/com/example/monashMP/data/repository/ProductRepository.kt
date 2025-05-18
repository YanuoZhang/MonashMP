package com.example.monashMP.data.repository

import com.example.monashMP.data.dao.ProductDao
import com.example.monashMP.data.entity.ProductEntity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

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

    suspend fun getUserProducts(sellerUid: String): List<ProductEntity> {
        return productDao.getUserProducts(sellerUid)
    }

    suspend fun getProductById(productId: Long): ProductEntity? {
        return productDao.getProductById(productId)
    }

    suspend fun incrementAndGetViewCount(productId: Long): Int {
        productDao.incrementViewCount(productId)
        return productDao.getProductById(productId)?.viewCount ?: 0
    }

    suspend fun deleteProduct(productId: Long) {
        productDao.deleteProductById(productId)
    }

    suspend fun getLocalProductCount(): Int {
        return productDao.getProductCount()
    }

    suspend fun fetchAllFromFirebase(): List<ProductEntity> {
        val snapshot = FirebaseDatabase.getInstance().reference.child("products").get().await()
        return snapshot.children.mapNotNull { it.getValue(ProductEntity::class.java) }
    }

    suspend fun insertAllIntoRoom(products: List<ProductEntity>) {
        productDao.insertAll(products)
    }
}