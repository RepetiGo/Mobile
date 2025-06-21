package com.example.flashcardappandroid.ui.screen

import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.flashcardappandroid.data.TokenManager
import com.example.flashcardappandroid.network.RetrofitClient
import kotlinx.coroutines.launch
import com.example.flashcardappandroid.data.LogOutRequest

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val coroutineScope = rememberCoroutineScope()

    Button(onClick = {
        coroutineScope.launch {
            try {
                val accessToken = tokenManager.getAccessToken()
                val refreshToken = tokenManager.getRefreshToken()
                if (accessToken != null && refreshToken != null) {
                    val response = RetrofitClient.api.logOut(
                        LogOutRequest(refreshToken),
                        "Bearer $accessToken"
                    )
                    if (response.isSuccessful) {
                        tokenManager.clearTokens()
                        Toast.makeText(context, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Đăng xuất thất bại", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Không tìm thấy token", Toast.LENGTH_SHORT).show()
                }
            }    catch (e: Exception) {
                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }) {
        Text("Đăng xuất")
    }
}
