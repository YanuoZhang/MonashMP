package com.example.monashMP.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.example.monashMP.data.model.ProductModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProductRepository {

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
                "newest" -> filtered.sortedByDescending { it.createdAt }
                "lowest" -> filtered.sortedBy { it.price }
                "highest" -> filtered.sortedByDescending { it.price }
                else -> filtered
            }
        }
    }

    suspend fun insertProduct(product: ProductModel): Boolean = safeCall {
        val id = product.productId.toString()
        db.child("products").child(id).setValue(product).await()
        true
    } ?: false

    suspend fun getFavoriteProductIds(userUid: String): List<Long> = safeCall {
        val snapshot = db.child("favorites").child(userUid).get().await()
        snapshot.children.mapNotNull { it.key?.toLongOrNull() }
    } ?: emptyList()

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