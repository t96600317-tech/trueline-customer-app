package com.example.truelineapp.storage

import android.content.Context
import android.content.SharedPreferences

class AndroidCustomerSessionStorage(private val context: Context) : SessionStorage {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("trueline_customer_session", Context.MODE_PRIVATE)
    }

    override fun saveAuthToken(token: String) {
        prefs.edit().putString("auth_token", token).commit()
    }

    override fun getAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    override fun savePhone(phone: String) {
        prefs.edit().putString("saved_phone", phone).commit()
    }

    override fun getPhone(): String? {
        return prefs.getString("saved_phone", null)
    }

    override fun saveLanguage(language: String) {
        prefs.edit().putString("selected_lang", language).commit()
    }

    override fun getLanguage(): String? {
        return prefs.getString("selected_lang", null)
    }

    override fun saveUserName(name: String) {
        prefs.edit().putString("user_name", name).commit()
    }

    override fun getUserName(): String? {
        return prefs.getString("user_name", null)
    }

    override fun saveUserPhoto(path: String) {
        prefs.edit().putString("user_photo", path).commit()
    }

    override fun getUserPhoto(): String? {
        return prefs.getString("user_photo", null)
    }

    override fun saveWalletBalance(balance: Double) {
        prefs.edit().putString("user_wallet_balance", balance.toString()).commit()
    }

    override fun getWalletBalance(): Double? {
        return prefs.getString("user_wallet_balance", null)?.toDoubleOrNull()
    }

    override fun saveNameChangedBefore(changed: Boolean) {
        prefs.edit().putBoolean("name_changed_before", changed).commit()
    }

    override fun isNameChangedBefore(): Boolean {
        return prefs.getBoolean("name_changed_before", false)
    }

    override fun clearSession() {
        prefs.edit().clear().commit()
    }
}

private var globalCustomerSessionStorage: SessionStorage? = null

fun initCustomerSessionStorage(context: Context) {
    globalCustomerSessionStorage = AndroidCustomerSessionStorage(context.applicationContext)
}

actual fun getSessionStorage(): SessionStorage {
    return globalCustomerSessionStorage ?: object : SessionStorage {
        private val inMemory = mutableMapOf<String, String>()
        private var nameChanged = false
        override fun saveAuthToken(token: String) { inMemory["auth_token"] = token }
        override fun getAuthToken(): String? = inMemory["auth_token"]
        override fun savePhone(phone: String) { inMemory["saved_phone"] = phone }
        override fun getPhone(): String? = inMemory["saved_phone"]
        override fun saveLanguage(language: String) { inMemory["selected_lang"] = language }
        override fun getLanguage(): String? = inMemory["selected_lang"]
        override fun saveUserName(name: String) { inMemory["user_name"] = name }
        override fun getUserName(): String? = inMemory["user_name"]
        override fun saveUserPhoto(path: String) { inMemory["user_photo"] = path }
        override fun getUserPhoto(): String? = inMemory["user_photo"]
        override fun saveWalletBalance(balance: Double) { inMemory["user_wallet_balance"] = balance.toString() }
        override fun getWalletBalance(): Double? = inMemory["user_wallet_balance"]?.toDoubleOrNull()
        override fun saveNameChangedBefore(changed: Boolean) { nameChanged = changed }
        override fun isNameChangedBefore(): Boolean = nameChanged
        override fun clearSession() { inMemory.clear(); nameChanged = false }
    }
}
