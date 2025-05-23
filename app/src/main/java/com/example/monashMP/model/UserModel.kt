package com.example.monashMP.model

data class UserModel(
    val uid: String = "",
    val email: String = "",
    val password: String = "",
    val avatarUrl: String = "",
    val nickname: String = "",
    val birthday: String = "",
    val primaryCampus: String = "",
    val createdAt: Long = 0L
)


fun UserModel.toMap(): Map<String, Any> = mapOf(
    "uid" to uid,
    "email" to email,
    "password" to password,
    "avatarUrl" to avatarUrl,
    "nickname" to nickname,
    "birthday" to birthday,
    "primaryCampus" to primaryCampus,
    "createdAt" to createdAt
)