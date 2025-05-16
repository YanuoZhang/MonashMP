package com.example.monashMP.data.repository

import com.example.monashMP.data.dao.ProductDao
import com.example.monashMP.data.entity.ProductEntity
import com.example.monashMP.data.repository.query.buildProductFilterQuery
import kotlinx.coroutines.flow.Flow

class ProductRepositoryImpl(
    private val productDao: ProductDao
) : ProductRepository {

    override fun getFilteredProducts(
        title: String,
        category: String,
        minPrice: Float,
        maxPrice: Float,
        condition: String,
        locations: List<String>,
        sortBy: String
    ): Flow<List<ProductEntity>> {
        val query = buildProductFilterQuery(title, category, minPrice, maxPrice, condition, locations, sortBy)
        return productDao.getFilteredProductsRaw(query)
    }

    override suspend fun insertProduct(product: ProductEntity) {
        productDao.insertProduct(product)
    }

    override suspend fun getAllProducts(): List<ProductEntity> {
        return productDao.getAllProducts()
    }
}
