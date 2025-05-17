package com.example.monashMP.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithProducts(
    @Embedded val userEntity: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "sellerId"
    )
    val products: List<ProductEntity>
)
