package com.example.monashMP.workManager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.monashMP.data.database.AppDatabase
import com.example.monashMP.model.toModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class SyncProductsWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val productDao = AppDatabase.getDatabase(context).productDao()
    private val firebaseRef = Firebase.database.reference.child("drafts")

    override suspend fun doWork(): Result {
        return try {
            val drafts = productDao.getUnsyncedProducts()
            drafts.forEach { draft ->
                val model = draft.toModel() // transfer to model
                val firebaseId = model.productId.toString()
                firebaseRef.child(firebaseId).setValue(model).await()
                productDao.markAsSynced(draft.productId)
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
