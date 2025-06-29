package com.example.flashcardappandroid.ui.screen

import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardappandroid.network.RetrofitClient
import kotlinx.coroutines.launch
import android.widget.Toast
import com.example.flashcardappandroid.data.ForgotPasswordRequest
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.graphics.nativeCanvas
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isFormVisible by remember { mutableStateOf(false) }

    // Animation states
    val alpha by animateFloatAsState(
        targetValue = if (isFormVisible) 1f else 0f,
        animationSpec = tween(800),
        label = "alpha"
    )

    val translateY by animateFloatAsState(
        targetValue = if (isFormVisible) 0f else 50f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "translateY"
    )

    LaunchedEffect(Unit) {
        delay(300)
        isFormVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFF6B73FF)
                    )
                )
            )
    ) {
        // Background decorative elements
        FloatingPasswordDecorations()

        // Main content container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .graphicsLayer(
                    alpha = alpha,
                    translationY = translateY
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // App logo/icon with glow effect
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF667eea).copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            radius = 80f
                        ),
                        shape = CircleShape
                    )
            ) {
                Card(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF667eea)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main content card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = "Quên Mật Khẩu",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp
                        ),
                        color = Color(0xFF2D3748),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lock illustration
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                Color(0xFF667eea).copy(alpha = 0.1f),
                                RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔐",
                            style = MaterialTheme.typography.displayLarge,
                            fontSize = 60.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Description
                    Text(
                        text = "Đừng lo lắng! Chúng tôi sẽ gửi hướng dẫn đặt lại mật khẩu đến email của bạn.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF718096),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email input field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Địa chỉ email") },
                        placeholder = { Text("example@email.com") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFF667eea)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF667eea),
                            focusedLabelColor = Color(0xFF667eea),
                            cursorColor = Color(0xFF667eea)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Continue button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                try {
                                    val response = RetrofitClient.api.forgotPassword(ForgotPasswordRequest(email))
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Vui lòng kiểm tra email để tiếp tục", Toast.LENGTH_SHORT).show()
                                        navController.navigate("reset-password")
                                    } else {
                                        Toast.makeText(context, "Email không hợp lệ hoặc lỗi hệ thống", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        enabled = email.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF667eea),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Đang gửi...")
                        } else {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gửi Email Đặt Lại",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF7FAFC)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Đã nhớ mật khẩu?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4A5568)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { navController.navigate("login") }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF667eea)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Quay lại Đăng nhập",
                            color = Color(0xFF667eea),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FloatingPasswordDecorations() {
    val decorations = remember {
        List(6) {
            PasswordDecorationState(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 20f + 15f,
                speed = Random.nextFloat() * 0.01f + 0.005f,
                emoji = listOf("🔐", "🔑", "🛡️", "🔒", "🗝️", "🔓").random()
            )
        }
    }

    var animationTime by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { frameTime ->
                animationTime = frameTime / 1_000_000_000f
            }
        }
    }

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        decorations.forEach { decoration ->
            val currentY = (decoration.y + animationTime * decoration.speed) % 1f
            val currentX = decoration.x + sin(animationTime * 0.5f + decoration.x * 3f) * 0.03f

            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    textSize = decoration.size * density
                    textAlign = android.graphics.Paint.Align.CENTER
                    alpha = (sin(animationTime + decoration.x * 5f) * 0.2f + 0.3f * 255).toInt()
                }
                drawText(
                    decoration.emoji,
                    currentX * size.width,
                    currentY * size.height,
                    paint
                )
            }
        }
    }
}

data class PasswordDecorationState(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val emoji: String
)
