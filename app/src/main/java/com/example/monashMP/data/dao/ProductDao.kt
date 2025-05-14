package com.example.monashMP.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.monashMP.entity.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>): List<Long>

    @Update
    suspend fun updateProduct(product: Product): Int

    @Delete
    suspend fun deleteProduct(product: Product): Int

    @Query("DELETE FROM products WHERE productId = :productId")
    suspend fun deleteProductById(productId: Long): Int

    @Query("DELETE FROM products")
    suspend fun deleteAllCommodities()

    @Query("SELECT * FROM products WHERE productId = :productId")
    suspend fun getProductById(productId: Long): Product?

    @Query("SELECT * FROM products WHERE category = :category ORDER BY createdAt DESC")
    fun getProductsByCategory(category: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE title LIKE '%' || :title || '%' ORDER BY createdAt DESC")
    fun getProductsByTitle(title: String): Flow<List<Product>>

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllProductsFlow(): Flow<List<Product>>

    @Query("SELECT * FROM products ORDER BY createdAt ASC")
    suspend fun getProductsSortASCByCreateDate(): List<Product>

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    suspend fun getProductsSortDESCByCreateDate(): List<Product>

    @Query("SELECT * FROM products WHERE sellerId = :userId ORDER BY createdAt DESC")
    suspend fun getUserProducts(userId: Long): List<Product>

    @Transaction
    @Query(
        """
        SELECT p.* FROM products p
        INNER JOIN user_favorites f ON p.productId = f.productId
        WHERE f.userId = :userId
        ORDER BY f.favoriteDate DESC
    """
    )
    suspend fun getUserFavoriteProducts(userId: Long): List<Product>

    @RawQuery(observedEntities = [Product::class])
    fun getFilteredProductsRaw(query: SupportSQLiteQuery): Flow<List<Product>>

    fun getFilteredProducts(
        title: String = "",
        category: String = "All",
        minPrice: Float = 0f,
        maxPrice: Float = Float.MAX_VALUE,
        condition: String = "All",
        locations: List<String> = emptyList(),
        sortBy: String = "newest"
    ): Flow<List<Product>> {
        val queryBuilder = StringBuilder("SELECT * FROM products WHERE 1=1")
        val args = ArrayList<Any>()

        if (title.isNotEmpty()) {
            queryBuilder.append(" AND title LIKE ?")
            args.add("%$title%")
        }

        if (category != "All") {
            queryBuilder.append(" AND category = ?")
            args.add(category)
        }

        queryBuilder.append(" AND price BETWEEN ? AND ?")
        args.add(minPrice)
        args.add(maxPrice)

        if (condition != "All") {
            queryBuilder.append(" AND condition = ?")
            args.add(condition)
        }

        if (locations.isNotEmpty()) {
            queryBuilder.append(" AND location IN (")
            locations.forEachIndexed { index, _ ->
                queryBuilder.append("?")
                if (index < locations.size - 1) queryBuilder.append(", ")
            }
            queryBuilder.append(")")
            args.addAll(locations)
        }

        when (sortBy) {
            "newest" -> queryBuilder.append(" ORDER BY createdAt DESC")
            "oldest" -> queryBuilder.append(" ORDER BY createdAt ASC")
            "price_high_to_low" -> queryBuilder.append(" ORDER BY price DESC")
            "price_low_to_high" -> queryBuilder.append(" ORDER BY price ASC")
            else -> queryBuilder.append(" ORDER BY createdAt DESC")
        }

        val query = SimpleSQLiteQuery(queryBuilder.toString(), args.toArray())
        return getFilteredProductsRaw(query)
    }
}
