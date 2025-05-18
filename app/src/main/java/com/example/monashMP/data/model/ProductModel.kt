package com.example.monashMP.data.model

data class ProductModel(
    val productId: Long = 0,
    val sellerUid: String = "",
    val photos: List<String> = emptyList(),
    val title: String = "",
    val desc: String = "",
    val price: Float = 0f,
    val category: String = "",
    val condition: String = "",
    val location: String = "",
    val meetupPoint: String = "",
    val dayPreferenceWeekdays: Boolean = false,
    val dayPreferenceWeekends: Boolean = false,
    val paymentMethodPreference: String = "",
    val additionalNotes: String = "",
    val email: String = "",
    val phoneNum: String = "",
    val viewCount: Int = 0,
    val preferredContactMethod: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
