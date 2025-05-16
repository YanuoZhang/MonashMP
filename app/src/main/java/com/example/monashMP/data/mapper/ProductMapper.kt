package com.example.monashMP.data.mapper

import com.example.monashMP.data.entity.ProductEntity
import com.example.monashMP.model.ProductModel

fun ProductEntity.toModel(): ProductModel = ProductModel(
    productId, sellerUid, photos, title, desc, price, category, condition,
    location, meetupPoint, dayPreferenceWeekdays, dayPreferenceWeekends,
    paymentMethodPreference, additionalNotes, email, phoneNum, preferredContactMethod,
    createdAt, isActive
)

fun ProductModel.toEntity(): ProductEntity = ProductEntity(
    productId, sellerUid, photos, title, desc, price, category, condition,
    location, meetupPoint, dayPreferenceWeekdays, dayPreferenceWeekends,
    paymentMethodPreference, additionalNotes, email, phoneNum, preferredContactMethod,
    createdAt, isActive, isSynced = false
)
