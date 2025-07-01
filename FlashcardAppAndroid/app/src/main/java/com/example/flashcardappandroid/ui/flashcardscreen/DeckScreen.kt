package com.example.flashcardappandroid.ui.flashcardscreen

import DeckListViewModel
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.lifecycle.viewmodel.compose.viewModel
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
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
import com.example.flashcardappandroid.data.CreateDeckRequest
import com.example.flashcardappandroid.data.TokenManager
import com.example.flashcardappandroid.network.RetrofitClient
import kotlinx.coroutines.launch
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import com.example.flashcardappandroid.data.DeckResponse
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.rememberLazyListState

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckListScreen(
    navController: NavController,
    viewModel: DeckListViewModel = viewModel()
) {
    var showAddDeckDialog by remember { mutableStateOf(false) }
    var deckName by remember { mutableStateOf("") }
    var deckDescription by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(true) } // true = public (1), false = private (0)
    val context = LocalContext.current
    val deckList = viewModel.deckList
    var statDailyAverage = viewModel.statDailyAverage
    var statLearnedPercent = viewModel.statLearnedPercent
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }
    var selectedDeck by remember { mutableStateOf<DeckResponse?>(null) }
    var showEditDeckDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val publicDecks = deckList.filter { it.visibility == 1 }
    val privateDecks = deckList.filter { it.visibility == 0 }

    LaunchedEffect(Unit) {
        viewModel.loadDecksIfNeeded(context)
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
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color(0xFF191647),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Flashcard",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    actions = {
                        FloatingActionButton(
                            onClick = { showAddDeckDialog = true },
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
                                contentDescription = "Add Deck",
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
        ) { innerPadding ->

            // Dialog
            if (showAddDeckDialog) {
                AddDeckDialog(
                    deckName = deckName,
                    onDeckNameChange = { deckName = it },
                    deckDescription = deckDescription,
                    onDeckDescriptionChange = { deckDescription = it },
                    isPublic = isPublic,
                    onPrivacyChange = { isPublic = it },
                    onCancel = {
                        showAddDeckDialog = false
                        deckName = ""
                        deckDescription = ""
                        isPublic = true
                    },
                    onSave = {
                        coroutineScope.launch {
                            try {
                                val token = TokenManager(context).getAccessToken()
                                val request = CreateDeckRequest(
                                    name = deckName,
                                    description = deckDescription.ifBlank { null },
                                    visibility = if (isPublic) 1 else 0
                                )
                                val response = RetrofitClient.api.createDeck(request)
                                if (response.isSuccessful && response.body()?.isSuccess == true) {
                                    viewModel.reloadDecks(context)
                                    Toast.makeText(context, "Tạo deck thành công", Toast.LENGTH_SHORT).show()
                                    showAddDeckDialog = false
                                    deckName = ""
                                    deckDescription = ""
                                    isPublic = true
                                } else {
                                    Toast.makeText(context, "Tạo deck thất bại", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabledSave = deckName.isNotBlank(),
                    action = "Add"
                )
            }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
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
                                        text = "Welcome back!",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF191647)
                                    )
                                    Text(
                                        text = "Explore other people's decks",
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
                                    Column {
                                        Text(
                                            text = "${deckList.size}",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF191647),
                                            textAlign = TextAlign.Center,  // Căn giữa văn bản
                                            modifier = Modifier.fillMaxWidth()  // Bắt buộc để textAlign hoạt động
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Thống kê nhanh
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatCard(
                                    icon = Icons.Default.LibraryBooks,
                                    title = "Decks",
                                    value = "${deckList.size}",
                                    color = Color(0xFF4CAF50)
                                )
                                StatCard(
                                    icon = Icons.Default.TrendingUp,
                                    title = "LearnedPercent",
                                    value = "${statLearnedPercent}%",
                                    color = Color(0xFF2196F3)
                                )
                                StatCard(
                                    icon = Icons.Default.EmojiEvents,
                                    title = "DailyAverage",
                                    value = statDailyAverage,
                                    color = Color(0xFFFF9800)
                                )
                            }
                        }
                    }

//                // Deck List Section
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp, vertical = 8.dp)
//                ) {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "Your decks",
//                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 24.sp),
//                            fontWeight = FontWeight.SemiBold,
//                            color = Color(0xFF191647)
//                        )
//                    }
//                }

                    // Deck Cards
                        if (deckList.isEmpty()) {
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
                                        imageVector = Icons.Default.LibraryBooks,
                                        contentDescription = null,
                                        tint = Color(0xFF94D5F5),
                                        modifier = Modifier.size(48.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "You don't have any deck yet",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF191647)
                                )

                                Text(
                                    text = "Create your first deck to start your learning journey",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF666666),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                Button(
                                    onClick = { showAddDeckDialog = true },
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
                                        "Create Your First Deck",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Column {
                                DeckSectionRow(
                                    title = "Public Decks",
                                    decks = publicDecks,
                                    onDeckClick = {
                                        selectedDeck = it
                                        showSheet = true
                                    }
                                )

                                DeckSectionRow(
                                    title = "Private Decks",
                                    decks = privateDecks,
                                    onDeckClick = {
                                        selectedDeck = it
                                        showSheet = true
                                    }
                                )
                            }
                        }
                }
        }

        if (showEditDeckDialog && selectedDeck != null) {
            AddDeckDialog(
                deckName = deckName,
                onDeckNameChange = { deckName = it },
                deckDescription = deckDescription,
                onDeckDescriptionChange = { deckDescription = it },
                isPublic = isPublic,
                onPrivacyChange = { isPublic = it },
                onCancel = {
                    showEditDeckDialog = false
                    deckName = ""
                    deckDescription = ""
                    isPublic = true
                },
                onSave = {
                    coroutineScope.launch {
                        try {
                            val response = RetrofitClient.api.updateDeck(
                                deckId = selectedDeck!!.id,
                                request = CreateDeckRequest(
                                    name = deckName,
                                    description = deckDescription.ifBlank { null },
                                    visibility = if (isPublic) 1 else 0
                                )
                            )
                            if (response.isSuccessful && response.body()?.isSuccess == true) {
                                viewModel.reloadDecks(context)
                                Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                                showEditDeckDialog = false
                                selectedDeck = null
                            } else {
                                Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabledSave = deckName.isNotBlank(),
                action = "Save"
            )
        }

        if (showDeleteConfirmDialog && selectedDeck != null) {
            DeleteConfirmationDialog(
                showDialog = showDeleteConfirmDialog,
                selectedDeck = selectedDeck,
                onDismiss = {
                    showDeleteConfirmDialog = false
                    selectedDeck = null
                },
                onConfirm = {
                    coroutineScope.launch {
                        try {
                            val response = RetrofitClient.api.deleteDeck(
                                deckId = selectedDeck!!.id
                            )

                            if (response.isSuccessful || response.code() == 404) {
                                Toast.makeText(context, "Deck deleted successfully!", Toast.LENGTH_SHORT).show()
                                viewModel.reloadDecks(context)
                            } else {
                                Toast.makeText(context, "Delete failed, please try again", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            showDeleteConfirmDialog = false
                            selectedDeck = null
                        }
                    }
                }
            )
        }

        if (showSheet && selectedDeck != null) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState
            ) {
                DeckOptionsBottomSheet(
                    onDismiss = { showSheet = false },
                    onView = {
                        navController.navigate("deck_cards/${selectedDeck!!.id}")
                        showSheet = false },
                    onFlashcard = {
                        navController.navigate("flashcard_study/${selectedDeck!!.id}")
                        showSheet = false },
                    onRepetition = {
                        navController.navigate("repetition/${selectedDeck!!.id}")
                        showSheet = false},
                    onEdit = {
                        deckName = selectedDeck?.name ?: ""
                        deckDescription = selectedDeck?.description ?: ""
                        isPublic = selectedDeck?.visibility == 1
                        showEditDeckDialog = true
                        showSheet = false },
                    onDelete = {
                        showDeleteConfirmDialog = true
                        showSheet = false }
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

//@Composable
//private fun EnhancedDeckCard(deck: Any) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .animateContentSize()
//            .clickable { /* TODO: Navigate to deck details */ },
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = Color.White
//        ),
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = 2.dp,
//            hoveredElevation = 6.dp
//        )
//    ) {
//        Column(
//            modifier = Modifier.padding(20.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.Top
//            ) {
//                Column(
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text(
//                        text = "Deck Name", // Replace with deck.name
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF191647)
//                    )
//
//                    Text(
//                        text = "Deck description here...", // Replace with deck.description
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color(0xFF666666),
//                        modifier = Modifier.padding(top = 4.dp),
//                        maxLines = 2,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                }
//
//                // Privacy indicator
//                Box(
//                    modifier = Modifier
//                        .background(
//                            color = Color(0xFF4CAF50).copy(alpha = 0.1f),
//                            shape = RoundedCornerShape(8.dp)
//                        )
//                        .padding(horizontal = 8.dp, vertical = 4.dp)
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Public, // Use Icons.Default.Lock for private
//                            contentDescription = null,
//                            tint = Color(0xFF4CAF50),
//                            modifier = Modifier.size(12.dp)
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(
//                            text = "Public", // Replace with actual visibility
//                            style = MaterialTheme.typography.labelSmall,
//                            color = Color(0xFF4CAF50),
//                            fontWeight = FontWeight.Medium
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Stats row
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Style,
//                        contentDescription = null,
//                        tint = Color(0xFF94D5F5),
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "24 thẻ", // Replace with actual card count
//                        style = MaterialTheme.typography.bodySmall,
//                        color = Color(0xFF666666)
//                    )
//                }
//
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Schedule,
//                        contentDescription = null,
//                        tint = Color(0xFF94D5F5),
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "2 ngày trước", // Replace with actual last studied
//                        style = MaterialTheme.typography.bodySmall,
//                        color = Color(0xFF666666)
//                    )
//                }
//
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .size(8.dp)
//                            .background(
//                                color = Color(0xFF4CAF50),
//                                shape = CircleShape
//                            )
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "75%", // Replace with actual progress
//                        style = MaterialTheme.typography.bodySmall,
//                        color = Color(0xFF666666),
//                        fontWeight = FontWeight.Medium
//                    )
//                }
//            }
//        }
//    }
//}

@Composable
fun AddDeckDialog(
    deckName: String,
    onDeckNameChange: (String) -> Unit,
    deckDescription: String,
    onDeckDescriptionChange: (String) -> Unit,
    isPublic: Boolean,
    onPrivacyChange: (Boolean) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    enabledSave: Boolean,
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
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Deck",
                        tint = Color(0xFF191647),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "Create New Deck",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF191647),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Add a new flashcard deck to start studying",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Input: Name
                OutlinedTextField(
                    value = deckName,
                    onValueChange = onDeckNameChange,
                    label = { Text("Deck title") },
                    placeholder = { Text("Enter deck title...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF94D5F5),
                        focusedLabelColor = Color(0xFF94D5F5),
                        cursorColor = Color(0xFF94D5F5)
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Deck Name",
                            tint = Color(0xFF94D5F5)
                        )
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input: Description
                OutlinedTextField(
                    value = deckDescription,
                    onValueChange = onDeckDescriptionChange,
                    label = { Text("Description") },
                    placeholder = { Text("Enter deck description ...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF94D5F5),
                        focusedLabelColor = Color(0xFF94D5F5),
                        cursorColor = Color(0xFF94D5F5)
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Description",
                            tint = Color(0xFF94D5F5)
                        )
                    },
                    maxLines = 3,
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Privacy toggle
                PrivacySelector(
                    isPublic = isPublic,
                    onPrivacyChange = onPrivacyChange
                )

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
                        enabled = enabledSave
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(action , fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrivacySelector(isPublic: Boolean, onPrivacyChange: (Boolean) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(Icons.Default.Visibility, contentDescription = "Visibility", tint = Color(0xFF94D5F5))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Privacy", fontWeight = FontWeight.Medium, color = Color(0xFF191647))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Public Card
            Card(
                modifier = Modifier.weight(1f).clickable { onPrivacyChange(true) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPublic) Color(0xFF94D5F5) else Color(0xFFF5F5F5)
                ),
                border = BorderStroke(
                    1.dp,
                    if (isPublic) Color(0xFF94D5F5) else Color(0xFFE0E0E0)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Public, contentDescription = null, tint = if (isPublic) Color(0xFF191647) else Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Public", fontWeight = if (isPublic) FontWeight.Bold else FontWeight.Normal)
                }
            }

            // Private Card
            Card(
                modifier = Modifier.weight(1f).clickable { onPrivacyChange(false) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (!isPublic) Color(0xFF94D5F5) else Color(0xFFF5F5F5)
                ),
                border = BorderStroke(
                    1.dp,
                    if (!isPublic) Color(0xFF94D5F5) else Color(0xFFE0E0E0)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = if (!isPublic) Color(0xFF191647) else Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Private", fontWeight = if (!isPublic) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}

@Composable
fun DeckOptionsBottomSheet(
    onDismiss: () -> Unit,
    onView: () -> Unit,
    onFlashcard: () -> Unit,
    onRepetition: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val options = listOf(
            Triple("View", Icons.Default.Visibility, onView),
            Triple("Flashcard", Icons.Default.Style, onFlashcard),
            Triple("Repetition", Icons.Default.Repeat, onRepetition),
            Triple("Edit", Icons.Default.Edit, onEdit),
            Triple("Delete", Icons.Default.Delete, onDelete)
        )

        options.forEachIndexed { index, (label, icon, action) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        action()
                        onDismiss()
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center // Thay đổi thành căn giữa
                ) {
                    Spacer(modifier = Modifier.width(36.dp))
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (label == "Delete") {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(24.dp),
                    )

                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = if (label == "Delete") {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp) // Khoảng cách với icon
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .offset(x = (-32).dp) // Điều chỉnh bù trừ cho icon
                    )
                }
            }

            if (index < options.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DeckSectionRow(
    title: String,
    decks: List<DeckResponse>,
    onDeckClick: (DeckResponse) -> Unit
) {
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Spacer(modifier = Modifier.width(24.dp))
            Icon(
                imageVector = if (title == "Public Decks")
                    Icons.Default.Public
                else
                    Icons.Default.Lock,
                contentDescription = title,
                tint = if (title == "Public Decks")
                    Color(0xFF4CAF50)
                else
                    Color(0xFFFF9800),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 20.sp),
                color = if (title == "Public Decks")
                    Color(0xFF4CAF50)
                else
                    Color(0xFFFF9800),
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(decks) { deck ->
                DeckCard(
                    deck = deck,
                    onClick = { onDeckClick(deck) },
                )
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    showDialog: Boolean,
    selectedDeck: DeckResponse?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
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
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title
                    Text(
                        text = "Xác nhận xóa",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = Color(0xFF2D3748),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Deck name highlight
                    if (selectedDeck != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF7FAFC)
                            )
                        ) {
                            Text(
                                text = "\"${selectedDeck.name}\"",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color(0xFF667eea),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text = "Are you sure you want to delete this deck set?\n\nThis action cannot be undone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF718096),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

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
                                text = "Cancel",
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






