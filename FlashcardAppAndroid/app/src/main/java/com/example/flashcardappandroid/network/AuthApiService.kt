package com.example.flashcardappandroid.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.flashcardappandroid.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate

interface AuthApiService {
    @POST("api/users/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("api/users/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("api/users/logout")
    suspend fun logOut(
        @Body body: RequestBody,
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
    ): Response<ServiceResult<List<DeckResponse>>>

    @POST("api/decks")
    suspend fun createDeck(
        @Body request: CreateDeckRequest
    ): Response<ServiceResult<DeckResponse>>

    @PUT("/api/decks/{deckId}")
    suspend fun updateDeck(
        @Path("deckId") deckId: Int,
        @Body request: CreateDeckRequest
    ): Response<ServiceResult<DeckResponse>>

    @DELETE("/api/decks/{deckId}")
    suspend fun deleteDeck(
        @Path("deckId") deckId: Int
    ): Response<DeleteResponse>

    @GET("/api/cards/decks/{deckId}/cards")
    suspend fun getCardsByDeckId(
        @Path("deckId") deckId: Int
    ): Response<BaseResponse<List<CardResponse>>>

    @GET("/api/cards/decks/{deckId}/due")
    suspend fun getDueCards(
        @Path("deckId") deckId: Int
    ): Response<BaseResponse<List<ReviewResponse>>>

    @PUT("/api/cards/decks/{deckId}/cards/{cardId}/review")
    suspend fun reviewCard(
        @Path("deckId") deckId: Int,
        @Path("cardId") cardId: Int,
        @Body request: ReviewReQuest
    ): Response<BaseResponse<Unit>>

    @Multipart
    @POST("/api/cards/decks/{deckId}/cards")
    suspend fun addCard(
        @Path("deckId") deckId: Int,
        @Part("frontText") frontText: RequestBody,
        @Part("backText") backText: RequestBody,
        @Part imageFile: MultipartBody.Part? = null
    ): Response<BaseResponse<Unit>>

    @Multipart
    @PUT("/api/cards/decks/{deckId}/cards/{cardId}")
    suspend fun updateCard(
        @Path("deckId") deckId: Int,
        @Path("cardId") cardId: Int,
        @Part("FrontText") frontText: RequestBody,
        @Part("BackText") backText: RequestBody,
        @Part imageFile: MultipartBody.Part? = null
    ): Response<BaseResponse<CardResponse>>

    @DELETE("/api/cards/decks/{deckId}/cards/{cardId}")
    suspend fun deleteCard(
        @Path("deckId") deckId: Int,
        @Path("cardId") cardId: Int
    ): Response<BaseResponse<Unit>>

    @GET("/api/profiles/profile")
    suspend fun getProfile(
    ): Response<BaseResponse<ProfileResponse>>

    @GET("api/decks/shared")
    suspend fun getsharedDecks(
    ): Response<ServiceResult<List<DeckResponse>>>

    @POST("api/decks/shared/{deckId}/clone")
    suspend fun cloneDeck(
        @Path("deckId") deckId: Int,
        @Body request: CreateDeckRequest
    ): Response<ServiceResult<DeckResponse>>

    @POST("api/cards/generate")
    suspend fun generateCard(
        @Body request: GenerateRequest
    ): Response<BaseResponse<CardGenResponse>>

    @Multipart
    @POST("/api/cards/decks/{deckId}/cards")
    suspend fun addGenCard(
        @Path("deckId") deckId: Int,
        @Part("frontText") frontText: RequestBody,
        @Part("backText") backText: RequestBody,
        @Part("imageUrl") imageUrl: RequestBody?,
    ): Response<BaseResponse<Unit>>

    @PUT("api/profiles/settings")
    suspend fun updateSettings(
        @Body settings: Map<String, @JvmSuppressWildcards Any?>
    ): retrofit2.Response<Unit>

    @Multipart
    @POST("api/profiles/avatar")
    suspend fun uploadAvatar(
        @Part file: MultipartBody.Part
    ): Response<Unit>

    @POST("api/profiles/username")
    suspend fun updateUsername(
        @Body body: RequestBody
    ): Response<Unit>

    @RequiresApi(Build.VERSION_CODES.O)
    @GET("api/stats")
    suspend fun getStats(
        @Query("year") year: Int = LocalDate.now().year
    ): Response<ServiceResult<ActivityStatsResponse>>
}
