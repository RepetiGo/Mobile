package com.example.flashcardappandroid.network

import com.example.flashcardappandroid.data.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/users/log-in")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("api/users/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>
}
