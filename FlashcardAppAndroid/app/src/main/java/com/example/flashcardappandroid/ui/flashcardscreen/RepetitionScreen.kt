package com.example.flashcardappandroid.ui.flashcardscreen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.flashcardappandroid.network.RetrofitClient
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import com.example.flashcardappandroid.data.ReviewReQuest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.ui.geometry.Offset
import com.example.flashcardappandroid.data.ReviewResponse
import com.example.flashcardappandroid.data.ReviewTimeResult

@Composable
fun RepetitionScreen(deckId: Int, navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var cards by remember { mutableStateOf<List<ReviewResponse>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(deckId) {
        val response = RetrofitClient.api.getDueCards(deckId)
        if (response.isSuccessful) {
            cards = response.body()?.data ?: emptyList()
        }
        isLoading = false
    }

    if (isLoading) {
        LoadingScreen()
        return
    }

    if (!isLoading && cards.isEmpty()) {
        EmptyDueCardScreen(onBack = { navController.popBackStack() })
        return
    }

    if (currentIndex >= cards.size) {
        CongratulationScreen2(
            totalCards = cards.size,
            onBack = { navController.popBackStack() }
        )
        return
    }

    val card = cards[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF667eea),
                    Color(0xFF764ba2)
                )
            ))
    ) {
        Spacer(Modifier.height(32.dp))

        // Top Bar with Progress
        StudyTopBar(
            currentIndex = currentIndex,
            totalCards = cards.size,
            progress = progress,
            onBackClick = { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )
        {
            ImprovedFlashcard(
                frontText = card.frontText,
                backText = card.backText,
                imageUrl = card.imageUrl,
                isFlipped = isFlipped,
                onFlip = { isFlipped = !isFlipped },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        RepetitionButtons(
            reviewTime = card.reviewTimeResult,
            onRate = { rating ->
                coroutineScope.launch {
                    val request = ReviewReQuest(rating = rating)
                    RetrofitClient.api.reviewCard(
                        deckId = deckId,
                        cardId = card.id,
                        request
                    )
                    currentIndex++
                    isFlipped = false
                }
            }
        )
    }
}

@Composable
fun RepetitionButtons(
    reviewTime: ReviewTimeResult,
    onRate: (rating: Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val labels = listOf("Again", "Hard", "Good", "Easy")
    val colors = listOf(
        Color(0xFFE53E3E), // Red
        Color(0xFFFF8C00), // Orange
        Color(0xFF38A169), // Green
        Color(0xFF3182CE)  // Blue
    )
    val icons = listOf(
        Icons.Default.Refresh,     // Again
        Icons.Default.ThumbDown,   // Hard
        Icons.Default.ThumbUp,     // Good
        Icons.Default.Star         // Easy
    )
    val times = listOf(
        reviewTime.again,
        reviewTime.hard,
        reviewTime.good,
        reviewTime.easy
    )

    var selectedButton by remember { mutableStateOf(-1) }
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(selectedButton) {
        if (selectedButton != -1) {
            isAnimating = true
            delay(200)
            onRate(selectedButton)
            delay(100)
            isAnimating = false
            selectedButton = -1
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            labels.forEachIndexed { index, label ->
                val isSelected = selectedButton == index
                val animatedScale by animateFloatAsState(
                    targetValue = if (isSelected) 0.95f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh
                    ),
                    label = "buttonScale"
                )

                val animatedElevation by animateDpAsState(
                    targetValue = if (isSelected) 2.dp else 8.dp,
                    animationSpec = tween(200),
                    label = "buttonElevation"
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Time display with background
                    Text(
                        text = times[index],
                        style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium
                        ),
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(vertical = 6.dp),
                        textAlign = TextAlign.Center
                        )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Main button
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(
                                scaleX = animatedScale,
                                scaleY = animatedScale
                            )
                            .clickable(
                                enabled = enabled && !isAnimating,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                selectedButton = index
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (enabled) colors[index] else colors[index].copy(alpha = 0.5f)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = animatedElevation
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Icon with glow effect
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected && isAnimating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = icons[index],
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Label
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Progress indicator when processing
        if (isAnimating) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White.copy(alpha = 0.8f),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}

// Compact version for smaller spaces
@Composable
fun CompactRepetitionButtons(
    reviewTime: ReviewTimeResult,
    onRate: (rating: Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val labels = listOf("Again", "Hard", "Good", "Easy")
    val colors = listOf(
        Color(0xFFE53E3E),
        Color(0xFFFF8C00),
        Color(0xFF38A169),
        Color(0xFF3182CE)
    )
    val times = listOf(
        reviewTime.again,
        reviewTime.hard,
        reviewTime.good,
        reviewTime.easy
    )

    var selectedButton by remember { mutableStateOf(-1) }

    LaunchedEffect(selectedButton) {
        if (selectedButton != -1) {
            delay(150)
            onRate(selectedButton)
            selectedButton = -1
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEachIndexed { index, label ->
            val isSelected = selectedButton == index

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Time display
                Text(
                    text = times[index],
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Button
                Button(
                    onClick = { if (enabled) selectedButton = index },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = enabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors[index],
                        contentColor = Color.White,
                        disabledContainerColor = colors[index].copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = if (isSelected) 2.dp else 6.dp
                    )
                ) {
                    if (isSelected) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

// Data class for review times (if not already defined)
data class ReviewTimeResult(
    val again: String,
    val hard: String,
    val good: String,
    val easy: String
)

// Usage example
@Composable
fun RepetitionButtonsExample() {
    val sampleReviewTime = ReviewTimeResult(
        again = "1m",
        hard = "6m",
        good = "1d",
        easy = "4d"
    )

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
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        RepetitionButtons(
            reviewTime = sampleReviewTime,
            onRate = { rating ->
                println("Selected rating: $rating")
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Compact Version:",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CompactRepetitionButtons(
            reviewTime = sampleReviewTime,
            onRate = { rating ->
                println("Compact selected rating: $rating")
            }
        )
    }
}

@Composable
fun CongratulationScreen2(
    totalCards: Int,
    onBack: () -> Unit
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
                // button - Back to Deck
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
fun EmptyDueCardScreen(onBack: () -> Unit) {
    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    var showFloatingElements by remember { mutableStateOf(false) }

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
        animationSpec = tween(1000),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
        delay(800)
        showFloatingElements = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFF6B73FF)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
    ) {
        // Floating decorative elements
        if (showFloatingElements) {
            FloatingDecorations()
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
            // Main illustration with glow effect
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            radius = 120f
                        ),
                        shape = CircleShape
                    )
            ) {
                Card(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "🎯",
                            fontSize = 64.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Success message with enhanced typography
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "All Caught Up!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "No cards to review right now",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Status indicators
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatusIndicator(
                            icon = "✅",
                            title = "Complete",
                            subtitle = "All done"
                        )

                        StatusIndicator(
                            icon = "🎉",
                            title = "Great Job",
                            subtitle = "Keep it up"
                        )

                        StatusIndicator(
                            icon = "⚡",
                            title = "Ready",
                            subtitle = "For more"
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
                // Primary action - Back to Decks
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF667eea)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Back to Decks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(
    icon: String,
    title: String,
    subtitle: String
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
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun FloatingDecorations() {
    val decorations = remember {
        List(15) {
            DecorationState(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 12f + 8f,
                speed = Random.nextFloat() * 0.015f + 0.005f,
                emoji = listOf("📚", "✨", "🌟", "💫", "⭐", "🎯", "📖").random(),
                rotation = Random.nextFloat() * 360f
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
            val currentX = decoration.x + sin(animationTime + decoration.x * 5f) * 0.05f
            val currentRotation = decoration.rotation + animationTime * 20f

            drawContext.canvas.nativeCanvas.apply {
                save()
                translate(currentX * size.width, currentY * size.height)
                rotate(currentRotation)

                val paint = android.graphics.Paint().apply {
                    textSize = decoration.size * density
                    textAlign = android.graphics.Paint.Align.CENTER
                    alpha = (sin(animationTime * 2f + decoration.x * 10f) * 0.3f + 0.7f * 255).toInt()
                }
                drawText(
                    decoration.emoji,
                    0f,
                    decoration.size * density / 3f,
                    paint
                )
                restore()
            }
        }
    }
}

data class DecorationState(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val emoji: String,
    val rotation: Float
)


