package com.example.monashMP.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// 全局扩展属性，用于访问 DataStore 文件
val Context.userSession by preferencesDataStore(name = "user_session")

object UserSessionManager {

    private val KEY_USER_ID = longPreferencesKey("user_id")
    private const val DEFAULT_USER_ID = -1L

    /** Flow 形式获取 userId，可配合 Compose 使用 **/
    fun getUserIdFlow(context: Context): Flow<Long> =
        context.userSession.data.map { it[KEY_USER_ID] ?: DEFAULT_USER_ID }

    /** 立即获取当前 userId，用于非 UI 协程环境 **/
    suspend fun getUserId(context: Context): Long {
        return context.userSession.data.map { it[KEY_USER_ID] ?: DEFAULT_USER_ID }.first()
    }

    /** 登录成功后保存 userId **/
    suspend fun saveUserId(context: Context, id: Long) {
        context.userSession.edit { it[KEY_USER_ID] = id }
    }

    /** 注销时清除 userId **/
    suspend fun clearSession(context: Context) {
        context.userSession.edit { it.remove(KEY_USER_ID) }
    }

    /** 判断是否已登录（用于导航跳转） **/
    fun isLoggedInFlow(context: Context): Flow<Boolean> =
        getUserIdFlow(context).map { it != DEFAULT_USER_ID }

    /** 非 Flow 场景判断登录（如直接调用在 ViewModel 中） **/
    suspend fun isLoggedIn(context: Context): Boolean =
        getUserId(context) != DEFAULT_USER_ID
}
