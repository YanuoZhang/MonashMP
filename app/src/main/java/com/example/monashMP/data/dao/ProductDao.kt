package com.example.monashMP.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.monashMP.data.entity.ProductEntity

@Dao
interface ProductDao {

    // --- Insert ---

    /** Inserts a product into the database. Replaces if conflict occurs. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    // --- Update ---
    /** Updates a product. Returns number of rows affected. */
    @Update
    suspend fun updateDraftProduct(product: ProductEntity): Int

    // --- Delete ---
    /** Deletes a product by its ID. */
    @Query("DELETE FROM products WHERE productId = :productId")
    suspend fun deleteDraftById(productId: Long)

    // --- Query (Read) ---
    /** Returns a product by ID. */
    @Query("SELECT * FROM products WHERE productId = :productId")
    suspend fun getDraftByProductId(productId: Long): ProductEntity?

    /** Returns all products not yet synced with the cloud. */
    @Query("SELECT * FROM products WHERE isSynced = 0")
    suspend fun getUnsyncedProducts(): List<ProductEntity>

    /** Returns all products posted by a specific user. */
    @Query("SELECT * FROM products WHERE isDraft = 1 AND sellerUid = :sellerUid")
    suspend fun getDraftsForUser(sellerUid: String): List<ProductEntity>

    @Query("UPDATE products SET isSynced = 1 WHERE productId = :id")
    suspend fun markAsSynced(id: Long)



}