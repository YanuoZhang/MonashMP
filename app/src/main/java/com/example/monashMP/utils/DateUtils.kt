package com.example.monashMP.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun Long.formatTimestamp(): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}