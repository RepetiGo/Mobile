package com.example.flashcardappandroid.network

import com.example.flashcardappandroid.data.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
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

    @PUT("/api/decks/{deckId}")
    suspend fun updateDeck(
        @Header("Authorization") token: String,
        @Path("deckId") deckId: Int,
        @Body request: CreateDeckRequest
    ): Response<ServiceResult<DeckResponse>>

    @DELETE("/api/decks/{deckId}")
    suspend fun deleteDeck(
        @Header("Authorization") token: String,
        @Path("deckId") deckId: Int
    ): Response<DeleteResponse>

    @GET("/api/cards/decks/{deckId}/cards")
    suspend fun getCardsByDeckId(
        @Header("Authorization") token: String,
        @Path("deckId") deckId: Int
    ): Response<BaseResponse<List<CardResponse>>>

    @GET("/api/cards/decks/{deckId}/due")
    suspend fun getDueCards(
        @Header("Authorization") token: String,
        @Path("deckId") deckId: Int
    ): Response<BaseResponse<List<ReviewResponse>>>

    @PUT("/api/cards/decks/{deckId}/cards/{cardId}/review")
    suspend fun reviewCard(
        @Header("Authorization") token: String,
        @Path("deckId") deckId: Int,
        @Path("cardId") cardId: Int,
        @Body request: ReviewReQuest
    ): Response<BaseResponse<Unit>>


}
