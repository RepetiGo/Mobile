package com.example.flashcardappandroid.ui.aigenscreen

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import com.example.flashcardappandroid.data.TokenManager
import com.example.flashcardappandroid.network.RetrofitClient
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.graphicsLayer
import com.example.flashcardappandroid.data.CardGenResponse
import com.example.flashcardappandroid.data.GenerateRequest
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextButton
import com.example.flashcardappandroid.ui.flashcardscreen.ImprovedFlashcard
import com.example.flashcardappandroid.data.DeckResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch

@Composable
fun AiGenCardResultScreen(
    topic: String,
    frontText: String?,
    backText: String?,
    generateImage: Boolean,
    navController: NavController,
) {
    val context = LocalContext.current
    var card by remember { mutableStateOf<CardGenResponse?>(null) }
    var loading by remember { mutableStateOf(true) }
    var isFlipped by remember { mutableStateOf(false) }
    val dragOffsetX by remember { mutableFloatStateOf(0f) }
    val cardRotation by animateFloatAsState(
        targetValue = dragOffsetX / 20f,
        animationSpec = tween(150),
        label = "cardRotation"
    )
    var showDialog by remember { mutableStateOf(false) }
    var decks by remember { mutableStateOf<List<DeckResponse>>(emptyList()) }
    var isLoadingDecks by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        try {
            val request = GenerateRequest(
                topic = topic,
                frontText = frontText.takeIf { it?.isNotBlank() == true },
                backText = backText.takeIf { it?.isNotBlank() == true },
                generateImage = generateImage,
                imagePromptLanguage = "Auto",
                enhancePrompt = true,
                aspectRatio = "16:9"
            )
            val response = RetrofitClient.api.generateCard(request)
            if (response.isSuccessful) {
                card = response.body()?.data
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi khi tạo thẻ", Toast.LENGTH_SHORT).show()
        } finally {
            loading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFF4b79a1)
                    )
                )
            )
    ) {
        if (loading) {
            LoadingScreen()
        } else {
            card?.let { c ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Status bar spacer
                    Spacer(modifier = Modifier.height(32.dp))

                    // Enhanced Top Bar
                    TopBar(
                        onBackClick = { navController.popBackStack() }
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                    )
                    {
                        ImprovedFlashcard(
                        frontText = c.frontText,
                        backText = c.backText,
                        imageUrl = c.imageUrl,
                        isFlipped = isFlipped,
                        onFlip = { isFlipped = !isFlipped },
                        modifier = Modifier
                            .graphicsLayer {
                                rotationZ = cardRotation
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Enhanced save button
                    EnhancedSaveButton(
                        onClick = {
                            showDialog = true
                            isLoadingDecks = true
                            coroutineScope.launch {
                                try {
                                    val res = RetrofitClient.api.getDecks()
                                    if (res.isSuccessful) {
                                        res.body()?.data?.let {
                                            decks = it
                                        }
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Lỗi khi lấy deck", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isLoadingDecks = false
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Enhanced dialog
        if (showDialog) {
            EnhancedDeckSelectionDialog(
                decks = decks,
                isLoading = isLoadingDecks,
                onDismiss = { showDialog = false },
                onDeckSelected = { deck ->
                    showDialog = false
                    coroutineScope.launch {
                        try {
                            val front = card!!.frontText.toRequestBody("text/plain".toMediaType())
                            val back = card!!.backText.toRequestBody("text/plain".toMediaType())
                            val image = card!!.imageUrl?.toRequestBody("text/plain".toMediaType())
                            val res = RetrofitClient.api.addGenCard(
                                deckId = deck.id,
                                frontText = front,
                                backText = back,
                                imageUrl = image
                            )
                            if (res.isSuccessful) {
                                Toast.makeText(context, "Đã thêm thẻ vào ${deck.name}", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Thêm thẻ thất bại", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Lỗi khi thêm thẻ", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "🎴 Đang tạo thẻ học...",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Vui lòng đợi trong giây lát",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun TopBar(
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
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun EnhancedSaveButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "buttonScale"
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = buttonScale
                scaleY = buttonScale
            }
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(28.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            ),
            interactionSource = interactionSource
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = null,
                    tint = Color(0xFF667eea),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Lưu vào Deck",
                    color = Color(0xFF667eea),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EnhancedDeckSelectionDialog(
    decks: List<DeckResponse>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onDeckSelected: (DeckResponse) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Folder,
                    contentDescription = null,
                    tint = Color(0xFF667eea),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Chọn Deck để lưu",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )
            }
        },
        text = {
            Column {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF667eea),
                            strokeWidth = 3.dp
                        )
                    }
                } else if (decks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.FolderOff,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Không có deck nào",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(decks) { deck ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onDeckSelected(deck) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF7FAFC)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                Color(0xFF667eea).copy(alpha = 0.1f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = deck.name.take(1).uppercase(),
                                            color = Color(0xFF667eea),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = deck.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF2D3748)
                                        )
                                        deck.description?.let { desc ->
                                            Text(
                                                text = desc,
                                                fontSize = 14.sp,
                                                color = Color(0xFF718096),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF667eea),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF667eea)
                )
            ) {
                Text(
                    text = "Đóng",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}