package com.example.truelineapp.network.chat

import com.example.truelineapp.network.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiService {
    @GET("chats")
    suspend fun getConversations(): Response<ApiResponse<List<ChatConversationData>>>

    @GET("chats/{partner_id}/messages")
    suspend fun getMessages(
        @Path("partner_id") partnerId: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<ApiResponse<List<ChatMessageData>>>

    @retrofit2.http.POST("chats/{partner_id}/messages")
    suspend fun sendMessage(
        @Path("partner_id") partnerId: String,
        @retrofit2.http.Body body: Map<String, String>
    ): Response<ApiResponse<ChatMessageData>>

    @retrofit2.http.POST("chats/{partner_id}/read")
    suspend fun markAsRead(
        @Path("partner_id") partnerId: String
    ): Response<ApiResponse<Map<String, String>>>
}
