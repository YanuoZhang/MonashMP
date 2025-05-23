package com.example.monashMP.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.userSession by preferencesDataStore(name = "user_session")

object UserSessionManager {

    private val KEY_USER_UID = stringPreferencesKey("user_uid")
    val KEY_LOGIN_TIMESTAMP = longPreferencesKey("login_timestamp")

    /** Returns a Flow of the current UID. Recommended for use in Compose or reactive UI. */
    fun getUserUidFlow(context: Context): Flow<String?> =
        context.userSession.data.map { it[KEY_USER_UID] }

    /** Retrieves the current UID immediately. Suitable for use in non-UI coroutine contexts. */
    suspend fun getUserUid(context: Context): String? {
        return context.userSession.data.map { it[KEY_USER_UID] }.first()
    }

    /** Saves the UID after successful login (typically provided by Firebase). */
    suspend fun saveUserUid(context: Context, uid: String) {
        context.userSession.edit { it[KEY_USER_UID] = uid }
    }

    /** Clears the UID from storage on logout. */
    suspend fun clearSession(context: Context) {
        context.userSession.edit { it.remove(KEY_USER_UID) }
    }

    /** Checks login status as a Flow. Useful for conditional UI navigation or display. */
    fun isLoggedInFlow(context: Context): Flow<Boolean> =
        getUserUidFlow(context).map { !it.isNullOrBlank() }

    /** Stores the login timestamp for session validation. */
    suspend fun saveLoginTimestamp(context: Context) {
        context.userSession.edit {
            it[KEY_LOGIN_TIMESTAMP] = System.currentTimeMillis()
        }
    }

    /** Checks if the session has expired based on the given maximum allowed duration. */
    fun isSessionExpired(context: Context, maxAgeMillis: Long): Flow<Boolean> =
        context.userSession.data.map {
            val ts = it[KEY_LOGIN_TIMESTAMP] ?: 0L
            System.currentTimeMillis() - ts > maxAgeMillis
        }
}
