package com.example.flashcardappandroid.ui.flashcardscreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.em

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
            onBack = { navController.popBackStack() }
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
            .height(500.dp)
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
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFF667eea)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Animated celebration emoji
            Text(
                text = "🎉",
                fontSize = 80.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Congratulations!",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You've completed all $totalCards cards!",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF667eea)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back to Deck",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


