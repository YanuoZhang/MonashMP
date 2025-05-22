package com.example.monashMP.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.monashMP.data.dao.ProductDao
import com.example.monashMP.data.database.AppDatabase
import com.example.monashMP.data.entity.ProductEntity
import com.example.monashMP.data.model.ProductModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProductRepository(private val productDao: ProductDao) {

    private val db = FirebaseDatabase.getInstance().reference

    suspend fun insertProduct(product: ProductModel): Boolean = safeCall {
        val id = product.productId.toString()
        db.child("products").child(id).setValue(product).await()
        true
    } ?: false

    suspend fun insertDraftProduct(product: ProductEntity): Long {
        return productDao.insertProduct(product)
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
}