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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.em
import com.example.flashcardappandroid.data.ReviewReQuest
import kotlinx.coroutines.launch

@Composable
fun RepetitionScreen(deckId: Int, navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var cards by remember { mutableStateOf<List<CardResponse>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(deckId) {
        val token = TokenManager(context).getAccessToken()
        val response = RetrofitClient.api.getDueCards("Bearer $token", deckId)
        if (response.isSuccessful) {
            cards = response.body()?.data ?: emptyList()
        }
        isLoading = false
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
            .background(Brush.verticalGradient(listOf(Color(0xFF2193b0), Color(0xFF6dd5ed))))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.height(32.dp))
        Text("Repetition", style = MaterialTheme.typography.headlineMedium, color = Color.White)

        ImprovedFlashcard(
            frontText = card.frontText,
            backText = card.backText,
            imageUrl = card.imageUrl,
            isFlipped = isFlipped,
            onFlip = { isFlipped = !isFlipped },
            modifier = Modifier.fillMaxWidth()
        )

        RepetitionButtons(
            onRate = { rating ->
                coroutineScope.launch {
                    val token = TokenManager(context).getAccessToken()
                    val request = ReviewReQuest(rating = rating)
                    RetrofitClient.api.reviewCard(
                        token = "Bearer $token",
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
fun RepetitionButtons(onRate: (rating: Int) -> Unit) {
    val labels = listOf("Again", "Hard", "Good", "Easy")
    val colors = listOf(Color.Red, Color(0xFFFF9800), Color(0xFF4CAF50), Color(0xFF2196F3))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        labels.forEachIndexed { index, label ->
            Button(
                onClick = { onRate(index) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors[index],
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(label)
            }
        }
    }
}

