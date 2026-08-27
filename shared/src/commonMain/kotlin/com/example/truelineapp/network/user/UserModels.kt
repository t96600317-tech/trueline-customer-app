package com.example.truelineapp.network.user

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileData(
    val user: UserInfo,
    val balance: Double
)

@Serializable
data class UserInfo(
    val id: String,
    val name: String = "",
    val phone: String,
    val language_pref: String,
    val status: String
)
