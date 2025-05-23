package com.example.monashMP.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.monashMP.data.dao.ProductDao
import com.example.monashMP.data.entity.ProductEntity
import com.example.monashMP.utils.StringListConverter

@Database(
    entities = [ProductEntity::class],
    version = 2,
    exportSchema = false
)

@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns a singleton instance of AppDatabase
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}