package com.example.monashMP.data.entity

import androidx.room.*
import com.example.monashMP.utils.StringListConverter

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val productId: Long = 0,
    val sellerUid: String,
    @TypeConverters(StringListConverter::class) val photos: List<String>,
    val title: String,
    val desc: String,
    val price: Float,
    val category: String,
    val condition: String,
    val location: String,
    val meetupPoint: String,
    val dayPreferenceWeekdays: Boolean,
    val dayPreferenceWeekends: Boolean,
    val paymentMethodPreference: String,
    val additionalNotes: String,
    val email: String,
    val phoneNum: String,
    val viewCount: Int,
    val preferredContactMethod: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val isSynced: Boolean = false
)
