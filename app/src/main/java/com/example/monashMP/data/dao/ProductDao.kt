package com.example.monashMP.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.monashMP.data.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity): Int

    @Delete
    suspend fun deleteProduct(product: ProductEntity): Int

    @Query("DELETE FROM products WHERE productId = :productId")
    suspend fun deleteProductById(productId: Long): Int

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts(): Int

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE productId = :productId")
    suspend fun getProductById(productId: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE isSynced = 0")
    suspend fun getUnsyncedProducts(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE sellerUid = :sellerUid")
    suspend fun getUserProducts(sellerUid: String): List<ProductEntity>

    @RawQuery(observedEntities = [ProductEntity::class])
    fun getFilteredProductsRaw(query: SupportSQLiteQuery): Flow<List<ProductEntity>>

    @Query("UPDATE products SET viewCount = viewCount + 1 WHERE productId = :productId")
    suspend fun incrementViewCount(productId: Long)

}