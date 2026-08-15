package com.example.truelineapp.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.truelineapp.in/api/v1/"

    private var retrofit: Retrofit? = null

    fun getRetrofit(context: Context): Retrofit {
        return retrofit ?: synchronized(this) {
            val tokenManager = TokenManager(context)
            
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(tokenManager))
                .addInterceptor(loggingInterceptor)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .also { retrofit = it }
        }
    }
    
    fun <T> createService(context: Context, serviceClass: Class<T>): T {
        return getRetrofit(context).create(serviceClass)
    }
}
