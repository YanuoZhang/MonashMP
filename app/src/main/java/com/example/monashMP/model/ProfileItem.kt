package com.example.monashMP.model

data class ProfileItemModel(
    val id: Long,
    val title: String,
    val price: String,
    val cover: String,
    val type: ProfileItemType,
    val isDraft: Boolean = false
)

enum class ProfileItemType {
    Saved, Posted
}