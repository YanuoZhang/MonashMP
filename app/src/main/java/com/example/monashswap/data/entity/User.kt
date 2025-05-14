package com.example.monashswap.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "avatar")
    val avatar: String,
    @ColumnInfo(name = "nickName")
    val nickName: String,
    @ColumnInfo(name = "birthday")
    val birthday: String,
    @ColumnInfo(name = "primaryCampus")
    val primaryCampus: String,
    @ColumnInfo(name = "password")
    val password: String,
    @ColumnInfo(name = "isActive")
    val isActive: Boolean = true
)
