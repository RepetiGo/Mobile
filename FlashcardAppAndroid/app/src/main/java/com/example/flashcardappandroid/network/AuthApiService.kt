package com.example.flashcardappandroid.network

import com.example.flashcardappandroid.data.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/users/log-in")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("api/users/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("api/users/log-out")
    suspend fun logOut(
        @Body body: RequestBody,
        @Header("Authorization") token: String
    ): retrofit2.Response<Any>
}
