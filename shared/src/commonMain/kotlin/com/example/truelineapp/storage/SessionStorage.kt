package com.example.truelineapp.storage

interface SessionStorage {
    fun saveAuthToken(token: String)
    fun getAuthToken(): String?
    fun savePhone(phone: String)
    fun getPhone(): String?
    fun saveLanguage(language: String)
    fun getLanguage(): String?
    fun saveUserName(name: String)
    fun getUserName(): String?
    fun saveUserPhoto(path: String)
    fun getUserPhoto(): String?
    fun saveWalletBalance(balance: Double)
    fun getWalletBalance(): Double?
    fun saveNameChangedBefore(changed: Boolean)
    fun isNameChangedBefore(): Boolean
    fun clearSession()
}

expect fun getSessionStorage(): SessionStorage
