package com.example.monashMP.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.monashMP.data.database.AppDatabase
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class SyncProductsWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    private val productDao = AppDatabase.getDatabase(context).productDao()

    override suspend fun doWork(): Result {
        return try {
            val unsyncedProducts = productDao.getUnsyncedProducts()

            val firebaseRef = Firebase.database.reference.child("products")

            unsyncedProducts.forEach { product ->
                val firebaseId = product.productId.toString()
                firebaseRef.child(firebaseId).setValue(product).await()

                productDao.updateProduct(product.copy(isSynced = true))
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
