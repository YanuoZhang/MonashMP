package com.example.monashswap.utils

import java.math.BigInteger
import java.security.MessageDigest

fun String.md5(): String {
    val digest = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return BigInteger(1, digest).toString(16).padStart(32, '0')
}
