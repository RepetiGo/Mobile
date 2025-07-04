package com.example.flashcardappandroid.data

//Login Dto
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val isSuccess: Boolean,
    val data: LoginData,
    val errorMessage: String,
    val statusCode: Int
)

data class LoginData(
    val id: String,
    val email: String,
    val accessToken: String,
    val refreshToken: String
)


data class LogOutRequest(
    val refreshToken: String
)
//Register Dto
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiration: String? = null
)

//Refresh Dto
data class RefreshTokenRequest(
    val refreshToken: String
)

data class RefreshResponse(
    val isSuccess: Boolean,
    val data: RefreshData,
    val errorMessage: String,
    val statusCode: Int
)

data class RefreshData(
    val id: String,
    val email: String,
    val accessToken: String,
    val refreshToken: String
)

//Forgot Dto
data class ForgotPasswordRequest(
    val email: String
)

//Reset Dto
data class ResetPasswordRequest(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val code: String
)

//Deck Dto
data class CreateDeckRequest(
    val name: String,
    val description: String? = null,
    val visibility: Int
)

data class ServiceResult<T>(
    val isSuccess: Boolean,
    val data: T?,
    val errorMessage: String?,
    val statusCode: Int
)

data class DeckResponse(
    val id: Int,
    val name: String,
    val description: String?,   // nullable
    val visibility: Int,
    val downloads: Int,
    val forkedFromUsername: String?,
    val createdAt: String,
    val updatedAt: String?,     // nullable
    val userId: String
)

data class DeleteResponse(
    val isSuccess: Boolean,
    val data: String,
    val errorMessage: String,
    val statusCode: Int
)

//Card Dto
data class BaseResponse<T>(
    val isSuccess: Boolean,
    val data: T,
    val errorMessage: String?,
    val statusCode: Int
)

data class CardResponse(
    val id: Int,
    val frontText: String,
    val backText: String,
    val nextReview: String,
    val repetition: Int,
    val status: Int,
    val learningStep: Int,
    val lastReviewed: String,
    val imageUrl: String?,
    val imagePublicId: String?,
    val createdAt: String,
    val updatedAt: String,
    val deckId: Int
)

//Card Gen Dto
data class CardGenResponse(
    val frontText: String,
    val backText: String,
    val imageUrl: String?,
)

data class GenerateRequest(
    val topic: String,
    val frontText: String?,
    val backText: String?,
    val generateImage: Boolean,
    val imagePromptLanguage: String,
    val enhancePrompt: Boolean,
    val aspectRatio: String
)

//Review Dto
data class ReviewReQuest(
    val rating: Int,
)

data class ReviewResponse(
    val id: Int,
    val frontText: String,
    val backText: String,
    val nextReview: String,
    val reviewTimeResult: ReviewTimeResult,
    val repetition: Int,
    val status: Int,
    val easinessFactor: Float,
    val learningStep: Int,
    val lastReviewed: String?,
    val imageUrl: String?,
    val imagePublicId: String?,
    val createdAt: String,
    val updatedAt: String?,
    val deckId: Int
)

data class ReviewTimeResult(
    val again: String,
    val hard: String,
    val good: String,
    val easy: String
)

//Profile Dto
data class ProfileResponse(
    val id: String,
    val userName: String,
    val email: String,
    val avatarUrl: String
)

//Stats Dto
data class ActivityStatsResponse(
    val dailyReviewCounts: List<DailyReviewCount>,
    val dailyAverage: Double,
    val dayLearnedPercent: Int,
    val longestStreak: Int,
    val currentStreak: Int
)

data class DailyReviewCount(
    val date: String,
    val count: Int
)


