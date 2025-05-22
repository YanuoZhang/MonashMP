package com.example.monashMP.data.model

import com.example.monashMP.data.entity.ProductEntity
import com.example.monashMP.data.model.ProductModel

fun ProductModel.toEntity(isDraft: Boolean): ProductEntity {
    return ProductEntity(
        productId = this.productId,
        sellerUid = this.sellerUid,
        photos = this.photos,
        title = this.title,
        desc = this.desc,
        price = this.price,
        category = this.category,
        condition = this.condition,
        location = this.location,
        meetupPoint = this.meetupPoint,
        dayPreferenceWeekdays = this.dayPreferenceWeekdays,
        dayPreferenceWeekends = this.dayPreferenceWeekends,
        paymentMethodPreference = this.paymentMethodPreference,
        additionalNotes = this.additionalNotes,
        email = this.email,
        phoneNum = this.phoneNum,
        viewCount = this.viewCount,
        preferredContactMethod = this.preferredContactMethod,
        isActive = false,
        isSynced = false,
        isDraft = isDraft
    )
}


fun ProductEntity.toModel(): ProductModel {
    return ProductModel(
        productId = this.productId,
        sellerUid = this.sellerUid,
        title = this.title,
        desc = this.desc,
        price = this.price,
        photos = this.photos,
        category = this.category,
        condition = this.condition,
        location = this.location,
        email = this.email,
        phoneNum = this.phoneNum,
        viewCount = this.viewCount,
        preferredContactMethod = this.preferredContactMethod
    )
}