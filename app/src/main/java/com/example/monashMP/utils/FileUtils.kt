package com.example.monashMP.utils

import android.content.Context

object FileUtils {
    fun saveBase64(context: Context, base64: String, fileName: String): Boolean = try {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(base64.toByteArray())
        }
        true
    } catch (e: Exception) {
        e.printStackTrace(); false
    }

    fun readBase64(context: Context, fileName: String): String? = try {
        context.openFileInput(fileName).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        e.printStackTrace(); null
    }
}
