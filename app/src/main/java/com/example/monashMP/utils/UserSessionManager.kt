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

    /** Flow 形式获取 uid，可配合 Compose 使用 **/
    fun getUserUidFlow(context: Context): Flow<String?> =
        context.userSession.data.map { it[KEY_USER_UID] }

    /** 立即获取当前 uid，用于非 UI 协程环境 **/
    suspend fun getUserUid(context: Context): String? {
        context.userSession.edit { it.remove(KEY_USER_UID) }
        return context.userSession.data.map { it[KEY_USER_UID] }.first()
    }

    /** 登录成功后保存 uid（Firebase 提供的） **/
    suspend fun saveUserUid(context: Context, uid: String) {
        context.userSession.edit { it[KEY_USER_UID] = uid }
    }

    /** 判断是否已登录（Flow 形式，用于 UI 显示跳转） **/
    fun isLoggedInFlow(context: Context): Flow<Boolean> =
        getUserUidFlow(context).map { !it.isNullOrBlank() }

    suspend fun saveLoginTimestamp(context: Context) {
        context.userSession.edit {
            it[KEY_LOGIN_TIMESTAMP] = System.currentTimeMillis()
        }
    }

    fun isSessionExpired(context: Context, maxAgeMillis: Long): Flow<Boolean> =
        context.userSession.data.map {
            val ts = it[KEY_LOGIN_TIMESTAMP] ?: 0L
            System.currentTimeMillis() - ts > maxAgeMillis
        }
}