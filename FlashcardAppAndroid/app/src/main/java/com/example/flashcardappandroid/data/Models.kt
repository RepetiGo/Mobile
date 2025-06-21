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

//Resend Dto
data class ResendResponse(
    val isSuccess: Boolean,
    val data: RefreshData,
    val errorMessage: String,
    val statusCode: Int
)

