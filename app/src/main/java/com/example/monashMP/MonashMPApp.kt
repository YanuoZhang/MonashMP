package com.example.monashMP

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.monashMP.workManager.SyncProductsWorker
import java.util.concurrent.TimeUnit

class MonashMPApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Upload draft every 15 minutes
        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncProductsWorker>(
            15, TimeUnit.MINUTES
        ).build()

        // Avoid repeating missions
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sync_draft_products",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
    }
}
