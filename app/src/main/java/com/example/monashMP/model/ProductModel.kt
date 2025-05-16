package com.example.monashMP.model

data class ProductModel(
    val productId: Long = 0,
    val sellerUid: String,
    val photos: List<String>,
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
    val preferredContactMethod: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)