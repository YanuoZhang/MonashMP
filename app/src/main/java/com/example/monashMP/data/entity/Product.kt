package com.example.monashMP.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.monashMP.utils.StringListConverter

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["sellerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sellerId")]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val productId: Long = 0,
    val sellerId: Long,
    @TypeConverters(StringListConverter::class)
    val photos: List<String>,
    val title: String,
    val desc: String,
    val price: Float,
    val category: String,
    val condition: String,
    val location: String,
    @ColumnInfo(defaultValue = "")
    val meetupPoint: String,
    val dayPreferenceWeekdays: Boolean,
    val dayPreferenceWeekends: Boolean,
    val paymentMethodPreference: String,
    @ColumnInfo(defaultValue = "")
    val additionalNotes: String,
    @ColumnInfo(defaultValue = "")
    val email: String,
    @ColumnInfo(defaultValue = "")
    val phoneNum: String,
    @ColumnInfo(defaultValue = "Email")
    val preferredContactMethod: String,
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "isActive")
    val isActive: Boolean = true
)
