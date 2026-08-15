package com.example.truelineapp.network.user

import com.example.truelineapp.network.ApiResponse
import retrofit2.Response

class UserRepository(private val apiService: UserApiService) {

    suspend fun getUserProfile(): Result<UserProfileData> {
        return try {
            val response = apiService.getUserProfile()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.error?.message ?: "Unknown error"))
                }
            } else {
                Result.failure(Exception("HTTP_${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLanguagePreference(languageCode: String): Result<String> {
        return try {
            val response = apiService.updateLanguagePreference(mapOf("language" to languageCode))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body.data?.get("message") ?: "Success")
                } else {
                    Result.failure(Exception(body?.error?.message ?: "Unknown error"))
                }
            } else {
                Result.failure(Exception("HTTP_${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
