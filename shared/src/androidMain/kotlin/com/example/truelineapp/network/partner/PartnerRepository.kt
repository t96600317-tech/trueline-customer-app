package com.example.truelineapp.network.partner

import com.example.truelineapp.network.ApiResponse
import retrofit2.Response

class PartnerRepository(private val apiService: PartnerApiService) {

    suspend fun getPartners(language: String? = null, search: String? = null): Result<List<PartnerData>> {
        return try {
            val response = apiService.getPartners(
                language = if (language == "All") null else language,
                search = if (search.isNullOrBlank()) null else search
            )
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
}
