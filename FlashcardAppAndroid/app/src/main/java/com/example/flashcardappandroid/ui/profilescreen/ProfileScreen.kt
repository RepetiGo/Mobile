package com.example.flashcardappandroid.ui.profilescreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.flashcardappandroid.R
import com.example.flashcardappandroid.data.ProfileResponse
import com.example.flashcardappandroid.data.TokenManager
import com.example.flashcardappandroid.data.UserSession
import com.example.flashcardappandroid.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val coroutineScope = rememberCoroutineScope()

    var profile by remember { mutableStateOf<ProfileResponse?>(UserSession.currentUser) }
    var isLoading by remember { mutableStateOf(profile == null) }
    var bio by remember { mutableStateOf(UserSession.bio ?: "Welcome to your learning journey! Share something about yourself to inspire others in the community.") }

    // Chỉ gọi API nếu chưa có trong UserSession
    LaunchedEffect(Unit) {
        if (profile == null || UserSession.shouldReloadProfile) {
            try {
                val response = RetrofitClient.api.getProfile()
                if (response.isSuccessful) {
                    val userProfile = response.body()?.data
                    profile = userProfile
                    UserSession.currentUser = userProfile
                    UserSession.shouldReloadProfile = false
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi khi tải profile", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF94D5F5).copy(alpha = 0.1f),
                            Color(0xFFFFFFFF)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF94D5F5))
        }
        return
    }

    // Giao diện chính
    profile?.let { user ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF94D5F5).copy(alpha = 0.1f),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF191647),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Profile",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF191647)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = Color(0xFF191647)
                        )
                    )
                },
                containerColor = Color.Transparent
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Header Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar with gradient background
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFF94D5F5),
                                                Color(0xFF94D5F5).copy(alpha = 0.7f)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = profile?.avatarUrl ?: R.drawable.boy,
                                    contentDescription = "Avatar",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // User Info - Centered
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = user.userName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF191647),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = user.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF666666),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Action Buttons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { navController.navigate("edit-profile") },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF94D5F5),
                                        contentColor = Color(0xFF191647)
                                    ),
                                    modifier = Modifier.height(48.dp).width(240.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Edit Profile",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Bio Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFF94D5F5),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "About You",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF191647)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666),
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Start
                            )
                        }
                    }

                    // Stats Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = Color(0xFF94D5F5),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Learning Stats",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF191647)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Stats Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatCard(
                                    icon = Icons.Default.LibraryBooks,
                                    title = "Decks",
                                    value = "5",
                                    color = Color(0xFF4CAF50)
                                )
                                StatCard(
                                    icon = Icons.Default.Style,
                                    title = "Cards",
                                    value = "120",
                                    color = Color(0xFF2196F3)
                                )
                                StatCard(
                                    icon = Icons.Default.EmojiEvents,
                                    title = "Streak",
                                    value = "7",
                                    color = Color(0xFFFF9800)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Settings Button
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { navController.navigate("settings") },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    "Settings",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF191647)
                                )
                                Text(
                                    "Manage your preferences",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF666666)
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF666666)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Logout Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    val refreshToken = tokenManager.getRefreshToken()
                                    if (refreshToken != null) {
                                        val json = """{"refreshToken":"$refreshToken"}"""
                                        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
                                        val response = RetrofitClient.api.logOut(
                                            body = requestBody,
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
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6B6B),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp)
                    ) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Đăng xuất",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    } ?: run {
        // fallback nếu profile == null
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF94D5F5).copy(alpha = 0.1f),
                            Color(0xFFFFFFFF)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Không thể tải thông tin người dùng",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFFF6B6B),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = color.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF191647),
            textAlign = TextAlign.Center
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center
        )
    }
}

//@Composable
//fun ProfileScreen(navController: NavController) {
//    val context = LocalContext.current
//    val tokenManager = remember { TokenManager(context) }
//    val coroutineScope = rememberCoroutineScope()
//
//    var profile by remember { mutableStateOf<ProfileResponse?>(UserSession.currentUser) }
//    var isLoading by remember { mutableStateOf(profile == null) }
//
//    var bio by remember { mutableStateOf(UserSession.bio ?: "Tell us about yourself") }
//
//    // Chỉ gọi API nếu chưa có trong UserSession
//    LaunchedEffect(Unit) {
//        if (profile == null || UserSession.shouldReloadProfile) {
//            try {
//                val token = tokenManager.getAccessToken()
//                val response = RetrofitClient.api.getProfile("Bearer $token")
//                if (response.isSuccessful) {
//                    val userProfile = response.body()?.data
//                    profile = userProfile
//                    UserSession.currentUser = userProfile
//                    UserSession.shouldReloadProfile = false // reset lại
//                }
//            } catch (e: Exception) {
//                Toast.makeText(context, "Lỗi khi tải profile", Toast.LENGTH_SHORT).show()
//            } finally {
//                isLoading = false
//            }
//        }
//    }
//
//    if (isLoading) {
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            CircularProgressIndicator()
//        }
//        return
//    }
//
//    // Giao diện chính
//    profile?.let { user ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color(0xFFFDF6EC))
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text("Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
//            Spacer(Modifier.height(16.dp))
//
//            // Card thông tin cá nhân
//            Card(
//                shape = RoundedCornerShape(24.dp),
//                elevation = CardDefaults.cardElevation(6.dp),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column(
//                    modifier = Modifier.padding(24.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    AsyncImage(
//                        model = profile?.avatarUrl ?: R.drawable.boy,
//                        contentDescription = "Avatar",
//                        modifier = Modifier
//                            .size(96.dp)
//                            .clip(CircleShape)
//                            .background(Color.LightGray),
//                        contentScale = ContentScale.Crop
//                    )
//                    Spacer(Modifier.height(12.dp))
//                    Text(user.userName, fontWeight = FontWeight.Bold)
//                    Text(user.email, color = Color.Gray)
//                    Spacer(Modifier.height(16.dp))
//
//                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//                        Button(
//                            onClick = { navController.navigate("edit-profile") },
//                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
//                        ) {
//                            Icon(Icons.Default.Edit, contentDescription = null)
//                            Spacer(Modifier.width(6.dp))
//                            Text("Edit Profile")
//                        }
//                    }
//                }
//            }
//
//            Spacer(Modifier.height(20.dp))
//
//            // Card Bio
//            Card(
//                shape = RoundedCornerShape(16.dp),
//                elevation = CardDefaults.cardElevation(4.dp),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column(Modifier.padding(16.dp)) {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFEF6C00))
//                        Spacer(Modifier.width(8.dp))
//                        Text("About You", fontWeight = FontWeight.SemiBold, color = Color(0xFFEF6C00))
//                    }
//                    Spacer(Modifier.height(8.dp))
//                    Text(text = bio, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
//                }
//            }
//
//            Spacer(Modifier.weight(1f))
//
//            // Settings
//            Button(
//                onClick = { navController.navigate("settings") },
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // màu xanh
//            ) {
//                Icon(Icons.Default.Settings, contentDescription = null)
//                Spacer(Modifier.width(6.dp))
//                Text("Settings")
//            }
//
//            Spacer(Modifier.height(12.dp))
//
//            // Đăng xuất
//            Button(
//                onClick = {
//                    coroutineScope.launch {
//                        try {
//                            val refreshToken = tokenManager.getRefreshToken()
//                            if (refreshToken != null) {
//                                val json = """{"refreshToken":"$refreshToken"}"""
//                                val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
//                                val response = RetrofitClient.api.logOut(
//                                    body = requestBody,
//                                )
//                                if (response.isSuccessful) {
//                                    tokenManager.clearTokens()
//                                    Toast.makeText(context, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
//                                    navController.navigate("login") {
//                                        popUpTo("home") { inclusive = true }
//                                    }
//                                } else {
//                                    Toast.makeText(context, "Đăng xuất thất bại", Toast.LENGTH_SHORT).show()
//                                }
//                            } else {
//                                Toast.makeText(context, "Không tìm thấy token", Toast.LENGTH_SHORT).show()
//                            }
//                        } catch (e: Exception) {
//                            Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                },
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp)
//            ) {
//                Text("Đăng xuất", color = Color.White)
//            }
//        }
//    } ?: run {
//        // fallback nếu profile == null
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            Text("Không thể tải thông tin người dùng", color = Color.Red)
//        }
//    }
//}


