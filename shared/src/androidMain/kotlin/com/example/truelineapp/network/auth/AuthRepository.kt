package com.example.truelineapp.network.auth

import com.example.truelineapp.network.ApiResponse
import com.example.truelineapp.network.ApiError
import retrofit2.Response

class AuthRepository(private val apiService: AuthApiService) {

    suspend fun requestOtp(phone: String): Result<OtpRequestData> {
        return try {
            val response = apiService.requestOtp(OtpRequest(phone))
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(phone: String, otp: String): Result<OtpVerifyData> {
        return try {
            val response = apiService.verifyOtp(OtpVerifyRequest(phone, otp))
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun <T> handleResponse(response: Response<ApiResponse<T>>): Result<T> {
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.success && body.data != null) {
                return Result.success(body.data)
            }
            val errorMessage = body?.error?.message ?: "Unknown error occurred"
            return Result.failure(Exception(errorMessage))
        } else {
            // Include status code in exception message for identification
            return Result.failure(Exception("HTTP_${response.code()}: ${response.message()}"))
        }
    }
}
