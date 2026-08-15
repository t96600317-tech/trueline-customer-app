package com.example.truelineapp.network.auth

import com.example.truelineapp.network.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/otp/request")
    suspend fun requestOtp(@Body request: OtpRequest): Response<ApiResponse<OtpRequestData>>

    @POST("auth/otp/verify")
    suspend fun verifyOtp(@Body request: OtpVerifyRequest): Response<ApiResponse<OtpVerifyData>>
}
