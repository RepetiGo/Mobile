package com.example.flashcardappandroid.ui.sharedscreen

import DeckListViewModel
import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.lazy.LazyColumn
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import com.example.flashcardappandroid.data.DeckResponse
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedScreen(
    navController: NavController,
    viewModel: SharedDeckViewModel = viewModel(),
    deckviewModel: DeckListViewModel = viewModel()
) {
    var showAddDeckDialog by remember { mutableStateOf(false) }
    var deckName by remember { mutableStateOf("") }
    var deckDescription by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(true) } // true = public (1), false = private (0)
    val context = LocalContext.current
    val deckList = viewModel.deckList
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }
    var selectedDeck by remember { mutableStateOf<DeckResponse?>(null) }
    var showEditDeckDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadSharedDecksIfNeeded(context)
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
                                "Shared Decks",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
//                    actions = {
//                        FloatingActionButton(
//                            onClick = { showAddDeckDialog = true },
//                            containerColor = Color(0xFF191647),
//                            contentColor = Color.White,
//                            modifier = Modifier
//                                .size(48.dp)
//                                .padding(end = 8.dp),
//                            elevation = FloatingActionButtonDefaults.elevation(
//                                defaultElevation = 6.dp,
//                                pressedElevation = 8.dp
//                            )
//                        ) {
//                            Icon(
//                                Icons.Default.Add,
//                                contentDescription = "Add Deck",
//                                modifier = Modifier.size(24.dp)
//                            )
//                        }
//                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color(0xFF191647)
                    )
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->

            // Dialog không thay đổi
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
                                val response = RetrofitClient.api.cloneDeck("Bearer $token", selectedDeck!!.id, request)
                                if (response.isSuccessful && response.body()?.isSuccess == true) {
                                    deckviewModel.reloadDecks(context)
                                    Toast.makeText(context, "get deck successfully", Toast.LENGTH_SHORT).show()
                                    showAddDeckDialog = false
                                    deckName = ""
                                    deckDescription = ""
                                    isPublic = true
                                } else {
                                    Toast.makeText(context, "get deck failed", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabledSave = deckName.isNotBlank(),
                    action = "Get"
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
                                    text = "Let's explore!",
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
                    }
                }

                // Deck List Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Public decks",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF191647)
                        )
                    }
                }

                // Deck Cards
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = {
                        isRefreshing = true
                        viewModel.reloadsharedDecks(context)
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(500)
                            isRefreshing = false
                        }
                    }
                ) {
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
                                text = "Don't have any public deck yet",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191647)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(deckList) { deck ->
                                DeckCard(
                                    deck = deck,
                                    onClick = {
                                        selectedDeck = deck
                                        showSheet = true
                                    }
                                )
                            }
                        }
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
                            val token = TokenManager(context).getAccessToken()
                            val response = RetrofitClient.api.updateDeck(
                                token = "Bearer $token",
                                deckId = selectedDeck!!.id,
                                request = CreateDeckRequest(
                                    name = deckName,
                                    description = deckDescription.ifBlank { null },
                                    visibility = if (isPublic) 1 else 0
                                )
                            )
                            if (response.isSuccessful && response.body()?.isSuccess == true) {
                                viewModel.reloadsharedDecks(context)
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
                action = "Get"
            )
        }

        if (showDeleteConfirmDialog && selectedDeck != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            try {
                                val token = TokenManager(context).getAccessToken()
                                val response = RetrofitClient.api.deleteDeck(
                                    token = "Bearer $token",
                                    deckId = selectedDeck!!.id
                                )

                                if (response.isSuccessful || response.code() == 404) {
                                    Toast.makeText(context, "Deck deleted successfully", Toast.LENGTH_SHORT).show()
                                    viewModel.reloadsharedDecks(context)
                                } else {
                                    Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                showDeleteConfirmDialog = false
                                selectedDeck = null
                            }
                        }
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("No")
                    }
                },
                title = { Text("Confirm Delete") },
                text = { Text("Are you sure you want to delete this deck?") }
            )
        }

        if (showSheet && selectedDeck != null) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState
            ) {
                DeckOptionsBottomSheet(
                    onDismiss = { showSheet = false },
                    onFlashcard = {
                        navController.navigate("flashcard_study/${selectedDeck!!.id}")
                        showSheet = false },
                    onSave = {
                        showAddDeckDialog = true
                        showSheet = false },
                )
            }
        }
    }
}

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
                    text = "Get Public Deck",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF191647),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Get a new flashcard deck to start studying",
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
    onFlashcard: () -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val options = listOf(
            Triple("Flashcard", Icons.Default.Style, onFlashcard),
            Triple("Get", Icons.Default.Save, onSave),
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





