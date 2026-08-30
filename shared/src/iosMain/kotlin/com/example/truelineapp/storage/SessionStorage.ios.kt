@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.example.truelineapp.storage

import platform.Foundation.NSUserDefaults

private object IOSCustomerSessionStorage : SessionStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun saveAuthToken(token: String) = defaults.setObject(token, forKey = AUTH_TOKEN)
    override fun getAuthToken(): String? = defaults.stringForKey(AUTH_TOKEN)

    override fun savePhone(phone: String) = defaults.setObject(phone, forKey = PHONE)
    override fun getPhone(): String? = defaults.stringForKey(PHONE)

    override fun saveLanguage(language: String) = defaults.setObject(language, forKey = LANGUAGE)
    override fun getLanguage(): String? = defaults.stringForKey(LANGUAGE)

    override fun saveUserName(name: String) = defaults.setObject(name, forKey = USER_NAME)
    override fun getUserName(): String? = defaults.stringForKey(USER_NAME)

    override fun saveUserPhoto(path: String) = defaults.setObject(path, forKey = USER_PHOTO)
    override fun getUserPhoto(): String? = defaults.stringForKey(USER_PHOTO)

    override fun saveWalletBalance(balance: Double) = defaults.setObject(balance.toString(), forKey = WALLET_BALANCE)
    override fun getWalletBalance(): Double? = defaults.stringForKey(WALLET_BALANCE)?.toDoubleOrNull()

    override fun saveNameChangedBefore(changed: Boolean) = defaults.setBool(changed, forKey = NAME_CHANGED)
    override fun isNameChangedBefore(): Boolean = defaults.boolForKey(NAME_CHANGED)

    override fun clearSession() {
        customerSessionKeys.forEach(defaults::removeObjectForKey)
    }
}

actual fun getSessionStorage(): SessionStorage = IOSCustomerSessionStorage

private const val AUTH_TOKEN = "auth_token"
private const val PHONE = "saved_phone"
private const val LANGUAGE = "selected_lang"
private const val USER_NAME = "user_name"
private const val USER_PHOTO = "user_photo"
private const val WALLET_BALANCE = "user_wallet_balance"
private const val NAME_CHANGED = "name_changed_before"

private val customerSessionKeys = listOf(
    AUTH_TOKEN,
    PHONE,
    LANGUAGE,
    USER_NAME,
    USER_PHOTO,
    WALLET_BALANCE,
    NAME_CHANGED
)
