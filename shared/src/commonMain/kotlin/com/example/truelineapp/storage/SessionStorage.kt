package com.example.truelineapp.storage

interface SessionStorage {
    fun saveAuthToken(token: String)
    fun getAuthToken(): String?
    fun savePhone(phone: String)
    fun getPhone(): String?
    fun saveLanguage(language: String)
    fun getLanguage(): String?
    fun clearSession()
}

expect fun getSessionStorage(): SessionStorage
