package com.example.monashMP.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.monashMP.data.dao.ProductDao
import com.example.monashMP.data.database.AppDatabase
import com.example.monashMP.data.entity.ProductEntity
import com.example.monashMP.data.model.ProductModel
import com.example.monashMP.data.model.UserFavoriteModel
import com.example.monashMP.data.model.UserModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProductRepository(private val productDao: ProductDao) {

    private val db = FirebaseDatabase.getInstance().reference

    /**
     * Fetch all products from Firebase and filter/sort in-memory.
     */
    suspend fun getFilteredProducts(
        title: String,
        category: String,
        minPrice: Float,
        maxPrice: Float,
        condition: String,
        locations: List<String>,
        sortBy: String
    ): List<ProductModel> {
        return safeFetchList(db.child("products"), ProductModel::class.java).filter { product ->
            (title.isBlank() || product.title.contains(title, ignoreCase = true)) &&
                    (category == "All" || product.category == category) &&
                    product.price in minPrice..maxPrice &&
                    (condition == "All" || product.condition == condition) &&
                    (locations.isEmpty() || product.location in locations)
        }.let { filtered ->
            when (sortBy) {
                "Newest" -> filtered.sortedByDescending { it.createdAt }
                "Price: Low to High" -> filtered.sortedBy { it.price }
                "Price: High to Low" -> filtered.sortedByDescending { it.price }
                else -> filtered
            }
        }
    }

    suspend fun insertProduct(product: ProductModel): Boolean = safeCall {
        val id = product.productId.toString()
        db.child("products").child(id).setValue(product).await()
        true
    } ?: false

    suspend fun insertDraftProduct(product: ProductEntity): Long {
        return productDao.insertProduct(product)
    }

    suspend fun updateLocalDraft(product: ProductEntity): Int {
        return productDao.updateDraftProduct(product)
    }

    suspend fun deleteLocalDraftIfExists(productId: Long) {
        productDao.deleteDraftById(productId)
    }

    suspend fun getUserProducts(sellerUid: String): List<ProductModel> = safeCall {
        val snapshot = db.child("products").orderByChild("sellerUid").equalTo(sellerUid).get().await()
        if (snapshot.exists()) snapshot.children.mapNotNull { it.getValue(ProductModel::class.java) } else emptyList()
    } ?: emptyList()

    suspend fun getProductById(productId: Long): ProductModel? = safeCall {
        val snapshot = db.child("products").child(productId.toString()).get().await()
        snapshot.getValue(ProductModel::class.java)
    }

    suspend fun getUserDraftProducts(sellerUid: String): List<ProductEntity> {
        return productDao.getDraftsForUser(sellerUid)
    }

    suspend fun getDraftByProductId(productId: Long): ProductEntity {
        return productDao.getDraftByProductId(productId)
            ?: throw IllegalStateException("Draft with ID $productId not found")
    }


    suspend fun deleteProduct(productId: Long) = safeCall {
        db.child("products").child(productId.toString()).removeValue().await()
    }

    suspend fun deleteDraftProductById(productId: Long) {
        productDao.deleteDraftById(productId)
    }


    suspend fun addFavorite(userUid: String, productId: Long) = safeCall {
        db.child("favorites").child(userUid).child(productId.toString())
            .setValue(UserFavoriteModel(userUid, productId)).await()
    }

    suspend fun removeFavorite(userUid: String, productId: Long) = safeCall {
        db.child("favorites").child(userUid).child(productId.toString()).removeValue().await()
    }

    suspend fun isFavorite(userUid: String, productId: Long): Boolean = safeCall {
        val snapshot = db.child("favorites").child(userUid).child(productId.toString()).get().await()
        snapshot.exists()
    } ?: false

    suspend fun getFavoriteProductIds(userUid: String): List<Long> = safeCall {
        val snapshot = db.child("favorites").child(userUid).get().await()
        snapshot.children.mapNotNull { it.key?.toLongOrNull() }
    } ?: emptyList()

    suspend fun getFavoritesByUser(userUid: String): List<UserFavoriteModel> = safeCall {
        val snapshot = db.child("favorites").child(userUid).get().await()
        snapshot.children.mapNotNull { it.getValue(UserFavoriteModel::class.java) }
    } ?: emptyList()

    suspend fun getSellerInfo(uid: String): UserModel? = safeCall {
        val snapshot = db.child("users").child(uid).get().await()
        snapshot.getValue(UserModel::class.java)
    }

    suspend fun uploadProductImage(productId: Long, index: Int, bitmap: Bitmap): String = suspendCoroutine { cont ->
        val storageRef = FirebaseStorage.getInstance()
            .reference.child("products/$productId/photo_$index.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
        val data = baos.toByteArray()

        storageRef.putBytes(data)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception ?: Exception("Upload failed")
                storageRef.downloadUrl
            }
            .addOnSuccessListener { uri -> cont.resume(uri.toString()) }
            .addOnFailureListener { e -> cont.resumeWithException(e) }
    }

    suspend fun incrementAndGetViewCount(productId: Long): Int = safeCall {
        val ref = db.child("products").child(productId.toString())
        val snapshot = ref.get().await()
        val current = snapshot.getValue(ProductModel::class.java)
        val newCount = (current?.viewCount ?: 0) + 1
        ref.child("viewCount").setValue(newCount).await()
        newCount
    } ?: 0

    suspend fun deleteImageFromStorage(url: String) {
        try {
            FirebaseStorage.getInstance().getReferenceFromUrl(url).delete().await()
        } catch (e: Exception) {
            Log.e("ProductRepository", "Image deletion failed", e)
        }
    }

    private suspend fun <T> safeCall(block: suspend () -> T): T? {
        return try {
            block()
        } catch (e: Exception) {
            Log.e("FirebaseSafeCall", "Error: ${e.message}", e)
            null
        }
    }

    suspend fun deleteProductImageFolder(productId: Long) {
        val storageRef = FirebaseStorage.getInstance().reference.child("products/$productId")
        try {
            val items = storageRef.listAll().await()
            for (item in items.items) {
                item.delete().await()
            }
        } catch (e: Exception) {
            Log.e("Cleanup", "Failed to delete folder for productId=$productId", e)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ProductRepository? = null

        fun getInstance(context: Context): ProductRepository {
            return INSTANCE ?: synchronized(this) {
                val dao = AppDatabase.getDatabase(context).productDao()
                ProductRepository(dao).also { INSTANCE = it }
            }
        }
    }

    private suspend fun <T> safeFetchList(ref: DatabaseReference, clazz: Class<T>): List<T> {
        return try {
            val snapshot = ref.get().await()
            if (snapshot.exists()) {
                snapshot.children.mapNotNull { it.getValue(clazz) }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("FirebaseFetchList", "Error fetching ${clazz.simpleName}", e)
            emptyList()
        }
    }
}