package com.example.monashswap.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithProducts(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "sellerId"
    )
    val products: List<Product>
)
