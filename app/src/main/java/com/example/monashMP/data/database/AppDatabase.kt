package com.example.monashMP.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.monashMP.dao.ProductDao
import com.example.monashMP.dao.UserDao
import com.example.monashMP.dao.UserFavoriteDao
import com.example.monashMP.entity.Product
import com.example.monashMP.entity.User
import com.example.monashMP.entity.UserFavorite
import com.example.monashMP.utils.StringListConverter

@Database(
    entities = [
        User::class,
        Product::class,
        UserFavorite::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun userFavoriteDao(): UserFavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
