package com.example.monashMP.data.model

data class ProfileItem(
    val id: Long,
    val title: String,
    val price: String,
    val cover: String,
    val type: ProfileItemType,
    val isDraft: Boolean = false
    )
