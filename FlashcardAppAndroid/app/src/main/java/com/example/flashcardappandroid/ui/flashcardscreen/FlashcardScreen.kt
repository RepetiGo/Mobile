package com.example.flashcardappandroid.ui.flashcardscreen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.flashcardappandroid.data.TokenManager
import com.example.flashcardappandroid.network.RetrofitClient
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flashcardappandroid.data.CardResponse
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.em
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun FlashcardStudyScreen(deckId: Int, navController: NavController) {
    val context = LocalContext.current
    var cards by remember { mutableStateOf<List<CardResponse>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var dragOffsetX by remember { mutableStateOf(0f) }
    var progress by remember { mutableStateOf(0f) }

    // Animation states
    val cardOffsetX by animateFloatAsState(
        targetValue = dragOffsetX,
        animationSpec = tween(150),
        label = "cardOffset"
    )

    val cardRotation by animateFloatAsState(
        targetValue = dragOffsetX / 20f,
        animationSpec = tween(150),
        label = "cardRotation"
    )

    LaunchedEffect(deckId) {
        try {
            val token = TokenManager(context).getAccessToken()
            val response = RetrofitClient.api.getCardsByDeckId("Bearer $token", deckId)
            if (response.isSuccessful) {
                cards = response.body()?.data ?: emptyList()
            }
        } finally {
            isLoading = false
        }
    }

    // Update progress when currentIndex changes
    LaunchedEffect(currentIndex, cards.size) {
        if (cards.isNotEmpty()) {
            progress = (currentIndex.toFloat() + 1) / cards.size.toFloat()
        }
    }

    if (isLoading) {
        LoadingScreen()
        return
    }

    if (currentIndex >= cards.size) {
        CongratulationScreen(
            totalCards = cards.size,
            onBack = { navController.popBackStack() },
            onStudyAgain = {
                currentIndex = 0
                isFlipped = false
            }
        )
        return
    }

    val card = cards[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            )
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Top Bar with Progress
        StudyTopBar(
            currentIndex = currentIndex,
            totalCards = cards.size,
            progress = progress,
            onBackClick = { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.height(80.dp))
        // Main Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            when {
                                dragOffsetX < -150 -> {
                                    // Swipe left: next card
                                    if (currentIndex < cards.lastIndex) {
                                        currentIndex++
                                        isFlipped = false
                                    } else {
                                        currentIndex++
                                    }
                                }
                                dragOffsetX > 150 -> {
                                    // Swipe right: previous card
                                    if (currentIndex > 0) {
                                        currentIndex--
                                        isFlipped = false
                                    }
                                }
                            }
                            dragOffsetX = 0f
                        },
                        onDrag = { change, dragAmount ->
                            dragOffsetX += dragAmount.x
                        }
                    )
                },
        ) {
            // Background cards for depth effect
            if (currentIndex < cards.lastIndex) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .scale(0.95f)
                        .alpha(0.7f)
                        .offset(y = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                ) {}
            }

            // Main flashcard
            ImprovedFlashcard(
                frontText = card.frontText,
                backText = card.backText,
                imageUrl = card.imageUrl,
                isFlipped = isFlipped,
                onFlip = { isFlipped = !isFlipped },
                modifier = Modifier
                    .offset(x = cardOffsetX.dp)
                    .graphicsLayer {
                        rotationZ = cardRotation
                    }
            )

            // Swipe indicators
            SwipeIndicators(
                offsetX = dragOffsetX,
                canGoBack = currentIndex > 0,
                canGoForward = currentIndex < cards.lastIndex
            )
        }

        // Bottom Controls
        BottomControls(
            isFlipped = isFlipped,
            canGoBack = currentIndex > 0,
            canGoForward = currentIndex < cards.lastIndex,
            onFlip = { isFlipped = !isFlipped },
            onPrevious = {
                if (currentIndex > 0) {
                    currentIndex--
                    isFlipped = false
                }
            },
            onNext = {
                if (currentIndex < cards.lastIndex) {
                    currentIndex++
                    isFlipped = false
                } else {
                    currentIndex++
                }
            }
        )
    }
}

@Composable
fun StudyTopBar(
    currentIndex: Int,
    totalCards: Int,
    progress: Float,
    onBackClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "${currentIndex + 1} / $totalCards",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun ImprovedFlashcard(
    frontText: String,
    backText: String,
    imageUrl: String?,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(600),
        label = "flipRotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(560.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { onFlip() },
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = if (rotation > 90f) 180f else 0f
                }
        ) {
            if (rotation <= 90f) {
                // Front side
                FlashcardContent(
                    text = frontText,
                    imageUrl = imageUrl,
                    isBack = false
                )
            } else {
                // Back side
                FlashcardContent(
                    text = backText,
                    imageUrl = null,
                    isBack = true
                )
            }
        }
    }
}

@Composable
fun FlashcardContent(
    text: String,
    imageUrl: String?,
    isBack: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isBack && imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(
            text = text,
            style = if (isBack) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = if (isBack) Color(0xFF667eea) else Color.Black,
            fontWeight = if (isBack) FontWeight.Bold else FontWeight.Medium,
            lineHeight = 1.4.em
        )
    }
}

@Composable
fun SwipeIndicators(
    offsetX: Float,
    canGoBack: Boolean,
    canGoForward: Boolean
) {
    // Left indicator (Previous)
    if (canGoBack && offsetX > 50) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Surface(
                color = Color.Green.copy(alpha = 0.8f),
                shape = CircleShape,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    // Right indicator (Next)
    if (canGoForward && offsetX < -50) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 32.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Surface(
                color = Color.Blue.copy(alpha = 0.8f),
                shape = CircleShape,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun BottomControls(
    isFlipped: Boolean,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onFlip: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.95f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            IconButton(
                onClick = onPrevious,
                enabled = canGoBack,
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        if (canGoBack) Color(0xFF667eea) else Color.Gray.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous",
                    tint = Color.White
                )
            }

            // Flip button
            Button(
                onClick = onFlip,
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(min = 120.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFlipped) Color(0xFF764ba2) else Color(0xFF667eea)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(
                    if (isFlipped) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Flip",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isFlipped) "Hide" else "Reveal")
            }

            // Next button
            IconButton(
                onClick = onNext,
                enabled = canGoForward,
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        if (canGoForward) Color(0xFF667eea) else Color.Gray.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading flashcards...",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun CongratulationScreen(
    totalCards: Int,
    onBack: () -> Unit,
    onStudyAgain: () -> Unit
) {
    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
        delay(600)
        showConfetti = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFF2E1065)
                    ),
                    radius = 800f
                )
            )
    ) {
        // Floating particles background
        if (showConfetti) {
            FloatingParticles()
        }

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                }
        ) {
            // Trophy icon with glow effect
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Yellow.copy(alpha = 0.3f),
                                Color.Transparent
                            ),
                            radius = 100f
                        ),
                        shape = CircleShape
                    )
            ) {
                Text(
                    text = "🏆",
                    fontSize = 80.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Success message with gradient text effect
            Text(
                text = "Perfect!",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = Color.White,
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Study Session Complete",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Stats card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatItem(
                            icon = "📚",
                            value = totalCards.toString(),
                            label = "Cards Studied"
                        )

                        StatItem(
                            icon = "⭐",
                            value = "100%",
                            label = "Completion"
                        )

                        StatItem(
                            icon = "🎯",
                            value = "Great!",
                            label = "Performance"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Primary button - Study Again
                Button(
                    onClick = onStudyAgain,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF667eea)
                    ),
                    shape = RoundedCornerShape(28.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Study Again",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary button - Back to Deck
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = BorderStroke(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Back to Deck",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(
    icon: String,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun FloatingParticles() {
    val particles = remember {
        List(20) {
            ParticleState(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 8f + 4f,
                speed = Random.nextFloat() * 0.02f + 0.01f,
                emoji = listOf("✨", "⭐", "🎉", "💫", "🌟").random()
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
        particles.forEach { particle ->
            val currentY = (particle.y + animationTime * particle.speed) % 1f
            val currentX = particle.x + sin(animationTime * 2f + particle.x * 10f) * 0.1f

            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    textSize = particle.size * density
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawText(
                    particle.emoji,
                    currentX * size.width,
                    currentY * size.height,
                    paint
                )
            }
        }
    }
}

data class ParticleState(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val emoji: String
)


