package com.example.monashMP.mapper

import com.example.monashMP.data.entity.ProductEntity
import com.example.monashMP.model.ProductModel

fun ProductEntity.toModel(): ProductModel = ProductModel(
    productId, sellerId, photos, title, desc, price, category, condition,
    location, meetupPoint, dayPreferenceWeekdays, dayPreferenceWeekends,
    paymentMethodPreference, additionalNotes, email, phoneNum, preferredContactMethod,
    createdAt, isActive
)

fun ProductModel.toEntity(): ProductEntity = ProductEntity(
    productId, sellerId, photos, title, desc, price, category, condition,
    location, meetupPoint, dayPreferenceWeekdays, dayPreferenceWeekends,
    paymentMethodPreference, additionalNotes, email, phoneNum, preferredContactMethod,
    createdAt, isActive, isSynced = false
)
