package com.example.flashcardappandroid.network

import com.example.flashcardappandroid.data.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthApiService {
    @POST("api/users/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("api/users/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("api/users/logout")
    suspend fun logOut(
        @Body body: LogOutRequest,
        @Header("Authorization") token: String
    ): Response<Any>

    @POST("api/users/refresh/{userId}")
    suspend fun refreshToken(
        @retrofit2.http.Path("userId") userId: String,
        @Body request: RefreshTokenRequest
    ): Response<RefreshResponse>

    @GET("api/users/resend")
    suspend fun resendConfirmationEmail(
        @Query("email") email: String
    ): Response<Unit>

    @POST("api/users/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Any>

    @POST("api/users/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Any>

    @GET("api/decks")
    suspend fun getDecks(
        @Header("Authorization") token: String
    ): Response<ServiceResult<List<DeckResponse>>>

    @POST("api/decks")
    suspend fun createDeck(
        @Header("Authorization") token: String,
        @Body request: CreateDeckRequest
    ): Response<ServiceResult<DeckResponse>>
}
