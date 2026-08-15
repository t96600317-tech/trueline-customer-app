package com.example.truelineapp.network.partner

import com.example.truelineapp.network.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PartnerApiService {
    @GET("partners")
    suspend fun getPartners(
        @Query("language") language: String? = null,
        @Query("search") search: String? = null
    ): Response<ApiResponse<List<PartnerData>>>
}
