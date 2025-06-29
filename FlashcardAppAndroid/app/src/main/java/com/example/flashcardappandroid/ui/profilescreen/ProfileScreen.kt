package com.example.flashcardappandroid.ui.profilescreen

import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.flashcardappandroid.data.ProfileResponse
import com.example.flashcardappandroid.data.TokenManager
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.draw.clip
import com.example.flashcardappandroid.network.RetrofitClient
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.flashcardappandroid.R // ✅ đúng

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val coroutineScope = rememberCoroutineScope()

    var profile by remember { mutableStateOf<ProfileResponse?>(UserSession.currentUser) }
    var isLoading by remember { mutableStateOf(profile == null) }

    var bio by remember { mutableStateOf("Tell us about yourself") }

    // Chỉ gọi API nếu chưa có trong UserSession
    LaunchedEffect(Unit) {
        if (profile == null) {
            try {
                val token = tokenManager.getAccessToken()
                val response = RetrofitClient.api.getProfile("Bearer $token")
                if (response.isSuccessful) {
                    val userProfile = response.body()?.data
                    profile = userProfile
                    UserSession.currentUser = userProfile
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi khi tải profile", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Giao diện chính
    profile?.let { user ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDF6EC))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            // Card thông tin cá nhân
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = profile?.avatarUrl ?: R.drawable.boy,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(user.userName, fontWeight = FontWeight.Bold)
                    Text(user.email, color = Color.Gray)
                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { navController.navigate("edit-profile") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Edit Profile")
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Card Bio
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFEF6C00))
                        Spacer(Modifier.width(8.dp))
                        Text("About You", fontWeight = FontWeight.SemiBold, color = Color(0xFFEF6C00))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(text = bio, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }

            Spacer(Modifier.weight(1f))

            // Đăng xuất
            Button(
                onClick = {
                    tokenManager.clearTokens()
                    Toast.makeText(context, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Đăng xuất", color = Color.White)
            }
        }
    } ?: run {
        // fallback nếu profile == null
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không thể tải thông tin người dùng", color = Color.Red)
        }
    }
}


