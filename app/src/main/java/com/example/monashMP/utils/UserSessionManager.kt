package com.example.monashMP.utils

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.userSession by preferencesDataStore(name = "user_session")

object UserSessionManager {

    private val KEY_USER_UID = stringPreferencesKey("user_uid")

    suspend fun getUserUid(context: Context): String? {
        return context.userSession.data.map { it[KEY_USER_UID] }.first()
    }

}