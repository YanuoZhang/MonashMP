package com.example.monashswap.model

data class ProfileItem(
    val title: String,
    val price: String,
    val location: String,
    val type: ProfileItemType
)

enum class ProfileItemType {
    Saved, Posted
}