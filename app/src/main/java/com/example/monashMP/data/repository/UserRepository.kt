package com.example.monashMP.data.repository

import android.content.Context
import com.example.monashMP.data.database.AppDatabase
import com.example.monashMP.utils.UserSessionManager
import com.example.monashMP.utils.md5

class UserRepository(private val context: Context) {

    suspend fun login(email: String, password: String): Boolean {
        val user = AppDatabase.getDatabase(context).userDao().getUserByEmail(email)
        return if (user != null && user.password == password.md5()) {
            UserSessionManager.saveUserId(context, user.userId)
            true
        } else {
            false
        }
    }
}
