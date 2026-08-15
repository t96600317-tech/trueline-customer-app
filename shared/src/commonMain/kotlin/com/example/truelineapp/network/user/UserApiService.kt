package com.example.truelineapp.network.user

import com.example.truelineapp.network.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Body

interface UserApiService {
    @GET("user/me")
    suspend fun getUserProfile(): Response<ApiResponse<UserProfileData>>

    @PATCH("user/language")
    suspend fun updateLanguagePreference(
        @Body body: Map<String, String>
    ): Response<ApiResponse<Map<String, String>>>
}
