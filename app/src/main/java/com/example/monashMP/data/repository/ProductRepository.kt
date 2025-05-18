package com.example.monashMP.data.repository

import UserFavoriteEntity
import com.example.monashMP.data.dao.ProductDao
import com.example.monashMP.data.dao.UserFavoriteDao
import com.example.monashMP.data.entity.ProductEntity
import com.example.monashMP.model.UserModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ProductRepository(
    private val productDao: ProductDao,
    private val favoriteDao: UserFavoriteDao
) {

    /**
     * Returns a filtered and sorted list of products based on user criteria.
     * The filtering is done in-memory after fetching all products from Room.
     */
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
                else -> filtered
            }
        }
    }

    /** Inserts a product into the local Room database. */
    suspend fun insertProduct(product: ProductEntity): Long {
        return productDao.insertProduct(product)
    }

    /** Returns all products created by a specific user. */
    suspend fun getUserProducts(sellerUid: String): List<ProductEntity> {
        return productDao.getUserProducts(sellerUid)
    }

    /** Fetches a single product by its ID. */
    suspend fun getProductById(productId: Long): ProductEntity? {
        return productDao.getProductById(productId)
    }

    /** Increments the view count and returns the updated value. */
    suspend fun incrementAndGetViewCount(productId: Long): Int {
        productDao.incrementViewCount(productId)
        return productDao.getProductById(productId)?.viewCount ?: 0
    }

    /** Deletes a product by ID. */
    suspend fun deleteProduct(productId: Long) {
        productDao.deleteProductById(productId)

        FirebaseDatabase.getInstance()
            .reference
            .child("products")
            .child(productId.toString())
            .removeValue()
            .await()
    }


//    /** Fetches all products from Firebase Realtime Database. */
//    suspend fun fetchAllFromFirebase(): List<ProductEntity> {
//        val snapshot = FirebaseDatabase.getInstance().reference.child("products").get().await()
//        return snapshot.children.mapNotNull { it.getValue(ProductEntity::class.java) }
//    }

//    /** Inserts a list of products into the local Room database. */
//    suspend fun insertAllIntoRoom(products: List<ProductEntity>) {
//        productDao.insertAll(products)
//    }

//    suspend fun syncWithFirebase(): Boolean {
//        return try {
//            val firebaseProducts = fetchAllFromFirebase()
//            val localProductIds = productDao.getAllProducts().map { it.productId }.toSet()
//            val newProducts = firebaseProducts.filterNot { it.productId in localProductIds }
//            if (newProducts.isNotEmpty()) insertAllIntoRoom(newProducts)
//
//            val unsyncedProducts = productDao.getUnsyncedProducts()
//            if (unsyncedProducts.isNotEmpty()) {
//                val ref = FirebaseDatabase.getInstance().reference.child("products")
//                for (product in unsyncedProducts) {
//                    ref.child(product.productId.toString()).setValue(product).await()
//                    productDao.markAsSynced(product.productId)
//                }
//            }
//            true
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
//    }



    // -------------- Favorite operations --------------

    /** Adds a product to the user's favorites. */
    suspend fun addFavorite(userUid: String, productId: Long) {
        favoriteDao.insertFavorite(UserFavoriteEntity(userUid, productId))
    }

    /** Removes a product from the user's favorites. */
    suspend fun removeFavorite(userUid: String, productId: Long) {
        favoriteDao.deleteFavorite(UserFavoriteEntity(userUid, productId))
    }

    /** Checks if a product is in the user's favorites. */
    suspend fun isFavorite(userUid: String, productId: Long): Boolean {
        return favoriteDao.isFavorite(userUid, productId)
    }

    /** Gets a live flow of product IDs that the user has favorited. */
    fun getFavoriteProductIdsFlow(userUid: String): Flow<List<Long>> {
        return favoriteDao.getFavoriteProductIdsFlow(userUid)
    }

    /** Returns all favorite entities for a user. */
    suspend fun getFavoritesByUser(userUid: String): List<UserFavoriteEntity> {
        return favoriteDao.getFavoritesByUser(userUid)
    }
    /** Fetches seller information by their user UID from Firebase Realtime Database. */
    suspend fun getSellerInfo(uid: String): UserModel? {
        val snapshot = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(uid)
            .get()
            .await()

        return snapshot.getValue(UserModel::class.java)
    }

}
