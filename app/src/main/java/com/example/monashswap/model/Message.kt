package com.example.monashswap.model

data class Message(
    val senderName: String,
    val senderEmail: String,
    val time: String,
    val title: String,
    val content: String
)
