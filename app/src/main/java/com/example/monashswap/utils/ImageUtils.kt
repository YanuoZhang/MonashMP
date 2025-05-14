package com.example.monashswap.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream

object ImageUtils {
    fun bitmapToBase64(bitmap: Bitmap): String {
        val resizedBitmap = if (bitmap.width > 1000 || bitmap.height > 1000) {
            val ratio = minOf(1000f / bitmap.width, 1000f / bitmap.height)
            bitmap.scale((bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt())
        } else bitmap

        val output = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, output)
        return Base64.encodeToString(output.toByteArray(), Base64.DEFAULT)
    }

    fun base64ToBitmap(base64: String): Bitmap? = try {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (e: Exception) {
        e.printStackTrace(); null
    }
}
