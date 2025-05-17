package com.example.monashMP.model

data class UserModel(
    val uid: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val nickName: String = "",
    val birthday: String = "",
    val primaryCampus: String = ""
)

fun UserModel.toMap(): Map<String, Any> = mapOf(
    "uid" to uid,
    "email" to email,
    "avatarUrl" to avatarUrl,
    "nickName" to nickName,
    "birthday" to birthday,
    "primaryCampus" to primaryCampus,
)