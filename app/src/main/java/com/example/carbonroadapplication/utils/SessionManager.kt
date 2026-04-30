package com.example.carbonroadapplication.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_USERNAME = "username"
        // 【新增】通知开关 Key
        const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
    }

    fun saveLoginSession(username: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USERNAME, username)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun logout() {
        prefs.edit().remove(KEY_IS_LOGGED_IN).remove(KEY_USERNAME).apply()
    }

    // 【新增】设置通知开关
    fun setNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply()
    }

    // 【新增】获取通知开关状态 (默认开启)
    fun isNotificationEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATION_ENABLED, true)
    }
}