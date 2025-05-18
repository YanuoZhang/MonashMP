package com.example.monashMP

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.monashMP.workmanager.SyncProductsWorker
import java.util.concurrent.TimeUnit

class MonashMPApp : Application() {

    companion object {
        lateinit var INSTANCE: MonashMPApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        val workRequest = PeriodicWorkRequestBuilder<SyncProductsWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sync_products_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
