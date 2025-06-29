package com.example.flashcardappandroid.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveTokens(accessToken: String, refreshToken: String, userId: String) {
        prefs.edit() {
            putString("access_token", accessToken)
                .putString("refresh_token", refreshToken)
                .putString("user_id", userId)
        }
    }

    fun getAccessToken(): String? = prefs.getString("access_token", null)

    fun getRefreshToken(): String? = prefs.getString("refresh_token", null)

    fun clearTokens() {
        prefs.edit() { clear() }
    }

    fun getUserId(): String? = prefs.getString("user_id", null)
}
