package com.bodeapp.util

import android.content.Context
import android.content.SharedPreferences

class UserSessionManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, 
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val PREFS_NAME = "BodeAppSession"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USER_EMAIL = "userEmail"
        private const val KEY_USER_NAME = "userName"
        private const val KEY_STORE_NAME = "storeName"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        
        @Volatile
        private var INSTANCE: UserSessionManager? = null
        
        fun getInstance(context: Context): UserSessionManager {
            return INSTANCE ?: synchronized(this) {
                val instance = UserSessionManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
    
    fun saveUserSession(userId: Int, email: String, userName: String, storeName: String) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, userName)
            putString(KEY_STORE_NAME, storeName)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }
    
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }
    
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }
    
    fun getStoreName(): String? {
        return prefs.getString(KEY_STORE_NAME, null)
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
