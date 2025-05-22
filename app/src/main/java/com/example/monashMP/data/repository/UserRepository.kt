package com.example.monashMP.data.repository

import android.content.Context
import android.util.Log
import com.example.monashMP.utils.md5
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class UserRepository(private val context: Context) {

    private val db = FirebaseDatabase.getInstance().reference

    suspend fun login(email: String, password: String): String? = safeCall {
        val hashedPassword = password.md5()
        val snapshot = db.child("users").orderByChild("email").equalTo(email).get().await()
        if (snapshot.exists()) {
            for (child in snapshot.children) {
                val userPassword = child.child("password").getValue(String::class.java)
                val uid = child.key
                if (userPassword == hashedPassword && uid != null) {
                    return@safeCall uid
                }
            }
        }
        null
    }


    suspend fun getUserByEmail(email: String): Boolean = safeCall {
        val snapshot = db.child("users").orderByChild("email").equalTo(email).get().await()
        snapshot.exists()
    } ?: false


    private suspend fun <T> safeCall(block: suspend () -> T): T? {
        return try {
            block()
        } catch (e: Exception) {
            Log.e("FirebaseSafeCall", "Error: ${e.message}", e)
            null
        }
    }
}
