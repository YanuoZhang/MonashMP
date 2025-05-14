package com.example.monashMP.utils

fun String.isValidEmail(): Boolean =
    "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex().matches(this)

fun String.isValidPassword(): Boolean =
    Regex("^(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])(.{8,})$").matches(this)
