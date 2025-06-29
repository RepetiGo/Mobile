package com.example.flashcardappandroid.ui.flashcardscreen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.example.flashcardappandroid.data.TokenManager
import com.example.flashcardappandroid.network.RetrofitClient
import kotlinx.coroutines.launch
import com.example.flashcardappandroid.data.CardResponse
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.sp
import androidx.compose.material3.IconButton
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardListScreen(deckId: Int, navController: NavController) {
    val context = LocalContext.current
    var cards by remember { mutableStateOf<List<CardResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var frontText by remember { mutableStateOf("") }
    var backText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var deckName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }
    var editingCard: CardResponse? by remember { mutableStateOf(null) }
    val isEditing = editingCard != null
    val showDialog = showAddDialog || isEditing
    var cardToDelete by remember { mutableStateOf<CardResponse?>(null) }

    LaunchedEffect(deckId) {
        val token = TokenManager(context).getAccessToken()
        val response = RetrofitClient.api.getCardsByDeckId("Bearer $token", deckId)
        if (response.isSuccessful) {
            cards = response.body()?.data ?: emptyList()
            // Get deck info for title
            // Assuming you have an API to get deck details
            // deckName = response.body()?.deckName ?: "Danh sách thẻ"
        }
        isLoading = false
    }

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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Style,
                                contentDescription = null,
                                tint = Color(0xFF191647),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (deckName.isNotEmpty()) deckName else "Flashcards",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191647)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF191647)
                            )
                        }
                    },
                    actions = {
                        FloatingActionButton(
                            onClick = { showAddDialog = true },
                            containerColor = Color(0xFF191647),
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(end = 8.dp),
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 8.dp
                            )
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Card",
                                modifier = Modifier.size(24.dp)
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
        ) { padding ->

            if (showDialog) {
                AddCardDialog(
                    frontText = if (isEditing) editingCard!!.frontText else frontText,
                    onFrontTextChange = {
                        if (isEditing) editingCard = editingCard!!.copy(frontText = it) else frontText = it
                    },
                    backText = if (isEditing) editingCard!!.backText else backText,
                    onBackTextChange = {
                        if (isEditing) editingCard = editingCard!!.copy(backText = it) else backText = it
                    },
                    selectedImageUri = selectedImageUri,
                    onSelectImage = { imagePickerLauncher.launch("image/*") },
                    isSaving = isSaving,
                    onCancel = {
                        showAddDialog = false
                        editingCard = null
                        frontText = ""
                        backText = ""
                        selectedImageUri = null
                    },
                    action = if (isEditing) "Edit" else "Add",
                    onSave = {
                        isSaving = true
                        coroutineScope.launch {
                            val token = TokenManager(context).getAccessToken()
                            val frontPart = (if (isEditing) editingCard!!.frontText else frontText)
                                .toRequestBody("text/plain".toMediaType())
                            val backPart = (if (isEditing) editingCard!!.backText else backText)
                                .toRequestBody("text/plain".toMediaType())
                            val imagePart = selectedImageUri?.let { uri ->
                                val inputStream = context.contentResolver.openInputStream(uri)!!
                                val bytes = inputStream.readBytes()
                                val requestFile = bytes.toRequestBody("image/*".toMediaType())
                                MultipartBody.Part.createFormData("ImageFile", "image.jpg", requestFile)
                            }

                            val response = if (isEditing) {
                                RetrofitClient.api.updateCard(
                                    token = "Bearer $token",
                                    deckId = deckId,
                                    cardId = editingCard!!.id,
                                    frontText = frontPart,
                                    backText = backPart,
                                    imageFile = imagePart
                                )
                            } else {
                                RetrofitClient.api.addCard(
                                    token = "Bearer $token",
                                    deckId = deckId,
                                    frontText = frontPart,
                                    backText = backPart,
                                    imageFile = imagePart
                                )
                            }

                            if (response.isSuccessful && response.body()?.isSuccess == true) {
                                Toast.makeText(context, if (isEditing) "Cập nhật thành công" else "Đã thêm flashcard", Toast.LENGTH_SHORT).show()
                                val refresh = RetrofitClient.api.getCardsByDeckId("Bearer $token", deckId)
                                if (refresh.isSuccessful) {
                                    cards = refresh.body()?.data ?: emptyList()
                                }
                                editingCard = null
                                showAddDialog = false
                                frontText = ""
                                backText = ""
                                selectedImageUri = null
                            } else {
                                Toast.makeText(context, "Thao tác thất bại", Toast.LENGTH_SHORT).show()
                            }
                            isSaving = false
                        }
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Header Section với thông tin thống kê
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
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Your Flashcards",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF191647)
                                )
                                Text(
                                    text = "Review and manage your study cards",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF666666),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFF94D5F5),
                                                Color(0xFF94D5F5).copy(alpha = 0.7f)
                                            )
                                        ),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${cards.size}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF191647),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatCard(
                                icon = Icons.Default.Style,
                                title = "Cards",
                                value = "${cards.size}",
                                color = Color(0xFF4CAF50)
                            )
                            StatCard(
                                icon = Icons.Default.Visibility,
                                title = "Reviewed",
                                value = "${(cards.size * 0.6).toInt()}",
                                color = Color(0xFF2196F3)
                            )
                            StatCard(
                                icon = Icons.Default.CheckCircle,
                                title = "Mastered",
                                value = "${(cards.size * 0.3).toInt()}",
                                color = Color(0xFFFF9800)
                            )
                        }
                    }
                }

                // Cards List
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF94D5F5),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                } else if (cards.isEmpty()) {
                    // Empty state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    color = Color(0xFF94D5F5).copy(alpha = 0.1f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Style,
                                contentDescription = null,
                                tint = Color(0xFF94D5F5),
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "No cards yet",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF191647)
                        )

                        Text(
                            text = "Create your first flashcard to start studying",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF94D5F5),
                                contentColor = Color(0xFF191647)
                            ),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Create Your First Card",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cards) { card ->
                            EnhancedCardItem(
                                card = card,
                                onEdit = { editingCard = card },
                                onDelete = { cardToDelete = card }
                            )
                        }
                    }
                }
            }
            if (cardToDelete != null) {
                DeleteCardConfirmationDialog(
                    cardToDelete = cardToDelete,
                    onDismiss = { cardToDelete = null },
                    onConfirm = {
                        coroutineScope.launch {
                            try {
                                val token = TokenManager(context).getAccessToken()
                                val response = RetrofitClient.api.deleteCard(
                                    token = "Bearer $token",
                                    deckId = deckId,
                                    cardId = cardToDelete!!.id
                                )
                                if (response.isSuccessful || response.code() == 404) {
                                    cards = cards.filter { it.id != cardToDelete!!.id }
                                    Toast.makeText(context, "Đã xóa thẻ thành công!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Xóa thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                cardToDelete = null
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun EnhancedCardItem(
    card: CardResponse,
    onEdit: () -> Unit,
    onDelete: () -> Unit,)
{
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Front text with icon
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = Color(0xFF94D5F5).copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Q",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94D5F5)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Question",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF94D5F5),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = card.frontText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF191647),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0E0E0))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Back text with icon
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // A icon
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Answer",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = card.backText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Optional: Add action buttons row
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onEdit ,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFF94D5F5),
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(24.dp)
                    )
                }
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
            color = Color(0xFF191647)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AddCardDialog(
    frontText: String,
    onFrontTextChange: (String) -> Unit,
    backText: String,
    onBackTextChange: (String) -> Unit,
    selectedImageUri: Uri?,
    onSelectImage: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean,
    action: String
) {
    Dialog(onDismissRequest = onCancel) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon header
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(color = Color(0xFF94D5F5), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Style,
                        contentDescription = "Add Card",
                        tint = Color(0xFF191647),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "Create New Flashcard",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF191647),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Add a new flashcard to your deck",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Front text input
                OutlinedTextField(
                    value = frontText,
                    onValueChange = onFrontTextChange,
                    label = { Text("Question (Front)") },
                    placeholder = { Text("Enter question...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF94D5F5),
                        focusedLabelColor = Color(0xFF94D5F5),
                        cursorColor = Color(0xFF94D5F5)
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = "Question",
                            tint = Color(0xFF94D5F5)
                        )
                    },
                    singleLine = false,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Back text input
                OutlinedTextField(
                    value = backText,
                    onValueChange = onBackTextChange,
                    label = { Text("Answer (Back)") },
                    placeholder = { Text("Enter answer...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAF50),
                        focusedLabelColor = Color(0xFF4CAF50),
                        cursorColor = Color(0xFF4CAF50)
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Answer",
                            tint = Color(0xFF4CAF50)
                        )
                    },
                    singleLine = false,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Image selector
                OutlinedButton(
                    onClick = onSelectImage,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF9800)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF9800)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Select Image",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedImageUri != null) "Image Selected" else "Add Image (Optional)",
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF94D5F5)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF94D5F5)
                        )
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF94D5F5),
                            contentColor = Color(0xFF191647)
                        ),
                        enabled = frontText.isNotBlank() && backText.isNotBlank() && !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color(0xFF191647),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(action, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteCardConfirmationDialog(
    cardToDelete: CardResponse?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (cardToDelete != null) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Warning icon with animation
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Color(0xFFFF6B6B).copy(alpha = 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title
                    Text(
                        text = "Confirm card deletion",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = Color(0xFF2D3748),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Card preview
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF8F9FA)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            // Front side preview
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            Color(0xFF667eea),
                                            CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Front text:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF718096),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = cardToDelete.frontText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF2D3748),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Back side preview
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            Color(0xFF48BB78),
                                            CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Back text:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF718096),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = cardToDelete.backText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF2D3748),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Warning message
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFFFFF5F5),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "This action cannot be undone.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFF6B6B),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel button
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF4A5568)
                            )
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cancle",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }

                        // Delete button
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF6B6B),
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Delete",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

