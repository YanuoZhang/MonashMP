package com.example.monashMP.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.monashMP.data.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // --- Insert ---

    /** Inserts a product into the database. Replaces if conflict occurs. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    /** Inserts a list of products into the database. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    // --- Update ---

    /** Updates a product. Returns number of rows affected. */
    @Update
    suspend fun updateProduct(product: ProductEntity): Int

    /** Increments the view count of a product by 1. */
    @Query("UPDATE products SET viewCount = viewCount + 1 WHERE productId = :productId")
    suspend fun incrementViewCount(productId: Long)

    // --- Delete ---

    /** Deletes a specific product. */
    @Delete
    suspend fun deleteProduct(product: ProductEntity): Int

    /** Deletes a product by its ID. */
    @Query("DELETE FROM products WHERE productId = :productId")
    suspend fun deleteProductById(productId: Long): Int

    /** Deletes all products from the table. */
    @Query("DELETE FROM products")
    suspend fun deleteAllProducts(): Int

    // --- Query (Read) ---


    /** Returns all products as a Flow stream, sorted by creation time. */
    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllProductsFlow(): Flow<List<ProductEntity>>

    /** Returns a product by ID. */
    @Query("SELECT * FROM products WHERE productId = :productId")
    suspend fun getProductById(productId: Long): ProductEntity?

    /** Returns all products not yet synced with the cloud. */
    @Query("SELECT * FROM products WHERE isSynced = 0")
    suspend fun getUnsyncedProducts(): List<ProductEntity>

    /** Returns all products posted by a specific user. */
    @Query("SELECT * FROM products WHERE sellerUid = :sellerUid")
    suspend fun getUserProducts(sellerUid: String): List<ProductEntity>

    /** Returns the total number of products. */
    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    @Query("UPDATE products SET isSynced = 1 WHERE productId = :id")
    suspend fun markAsSynced(id: Long)

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE isDraft = 1 AND sellerUid = :sellerUid")
    suspend fun getDraftsForUser(sellerUid: String): List<ProductEntity>

    @Query("DELETE FROM products WHERE productId = :id")
    suspend fun deleteDraftById(id: Long)
}