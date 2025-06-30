package com.example.flashcardappandroid.network

import android.content.Context
import com.example.flashcardappandroid.data.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5147/"

    private lateinit var tokenManager: TokenManager
    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        tokenManager = TokenManager(context)

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor { chain ->
                var request = chain.request()
                val accessToken = tokenManager.getAccessToken()
                if (!accessToken.isNullOrEmpty()) {
                    request = request.newBuilder()
                        .addHeader("Authorization", "Bearer $accessToken")
                        .build()
                }

                var response = chain.proceed(request)

                // Nếu bị 401 thì thử gọi refresh token
                if (response.code == 401 ) {
                    val refreshToken = tokenManager.getRefreshToken()
                    val userId = tokenManager.getUserId()

                    if (!refreshToken.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                        val refreshResponse = runBlocking {
                            try {
                                val apiService = retrofit.create(AuthApiService::class.java)
                                val result = apiService.refreshToken(userId, RefreshTokenRequest(refreshToken))
                                if (result.isSuccessful && result.body()?.isSuccess == true) {
                                    val newToken = result.body()?.data
                                    tokenManager.saveTokens(
                                        newToken?.accessToken ?: "",
                                        newToken?.refreshToken ?: "",
                                        userId
                                    )
                                    // Gửi lại request gốc với token mới
                                    val newRequest = request.newBuilder()
                                        .removeHeader("Authorization")
                                        .addHeader("Authorization", "Bearer ${newToken?.accessToken}")
                                        .build()
                                    return@runBlocking chain.proceed(newRequest)
                                }
                            } catch (_: Exception) {}
                            null
                        }

                        if (refreshResponse != null) return@addInterceptor refreshResponse
                    }
                }

                return@addInterceptor response
            }
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val api: AuthApiService by lazy {
        if (!::retrofit.isInitialized) {
            throw IllegalStateException("Call RetrofitClient.init(context) before using the API.")
        }
        retrofit.create(AuthApiService::class.java)
    }
}
