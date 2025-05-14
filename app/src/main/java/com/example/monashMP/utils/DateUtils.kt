package com.example.monashMP.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

fun Long.toReadableDate(): String = dateFormat.format(Date(this))
