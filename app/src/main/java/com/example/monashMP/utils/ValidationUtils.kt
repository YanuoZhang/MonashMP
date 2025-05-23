package com.example.monashMP.utils

fun String.isValidPassword(): Boolean =
    Regex("^(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])(.{8,})$").matches(this)