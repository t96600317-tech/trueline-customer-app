package com.example.truelineapp.storage

import android.content.Context
import android.content.SharedPreferences

class AndroidCustomerSessionStorage(private val context: Context) : SessionStorage {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("trueline_customer_session", Context.MODE_PRIVATE)
    }

    override fun saveAuthToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    override fun getAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    override fun savePhone(phone: String) {
        prefs.edit().putString("saved_phone", phone).apply()
    }

    override fun getPhone(): String? {
        return prefs.getString("saved_phone", null)
    }

    override fun saveLanguage(language: String) {
        prefs.edit().putString("selected_lang", language).apply()
    }

    override fun getLanguage(): String? {
        return prefs.getString("selected_lang", null)
    }

    override fun clearSession() {
        prefs.edit().clear().apply()
    }
}

private var globalCustomerSessionStorage: SessionStorage? = null

fun initCustomerSessionStorage(context: Context) {
    globalCustomerSessionStorage = AndroidCustomerSessionStorage(context.applicationContext)
}

actual fun getSessionStorage(): SessionStorage {
    return globalCustomerSessionStorage ?: object : SessionStorage {
        private val inMemory = mutableMapOf<String, String>()
        override fun saveAuthToken(token: String) { inMemory["auth_token"] = token }
        override fun getAuthToken(): String? = inMemory["auth_token"]
        override fun savePhone(phone: String) { inMemory["saved_phone"] = phone }
        override fun getPhone(): String? = inMemory["saved_phone"]
        override fun saveLanguage(language: String) { inMemory["selected_lang"] = language }
        override fun getLanguage(): String? = inMemory["selected_lang"]
        override fun clearSession() { inMemory.clear() }
    }
}
