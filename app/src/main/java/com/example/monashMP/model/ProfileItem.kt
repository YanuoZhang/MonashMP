package com.example.monashMP.model

data class ProfileItem(
    val id: Long,
    val title: String,
    val price: String,
    val cover: String,
    val type: ProfileItemType
)
