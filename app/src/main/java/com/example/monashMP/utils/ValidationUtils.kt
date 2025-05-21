package com.example.monashMP.utils

fun String.isValidPassword(): Boolean =
    Regex("^(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])(.{8,})$").matches(this)

fun String.isValidAustralianPhone(): Boolean =
    Regex("^\\(?0[2-478]\\)? ?\\d{4} ?\\d{4}\$").matches(this)