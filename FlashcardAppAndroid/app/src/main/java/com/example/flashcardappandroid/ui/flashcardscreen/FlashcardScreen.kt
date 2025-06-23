package com.example.flashcardappandroid.ui.flashcardscreen

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.example.flashcardappandroid.data.CreateDeckRequest
import com.example.flashcardappandroid.data.TokenManager
import com.example.flashcardappandroid.network.RetrofitClient
import kotlinx.coroutines.launch


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
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadDecksIfNeeded(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flashcard") },
                actions = {
                    IconButton(onClick = {
                        showAddDeckDialog = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF94D5F5),
                    titleContentColor = Color(0xFF191647)
                )
            )
        },
        containerColor = Color(0xFFFFFFFF)
    ) { innerPadding ->

        // Improved Custom Dialog
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
                    // TODO: Save logic
                    coroutineScope.launch {
                        try {
                            val token = TokenManager(context).getAccessToken()
                            val request = CreateDeckRequest(
                                name = deckName,
                                description = deckDescription.ifBlank { null },
                                visibility = if (isPublic) 1 else 0
                            )
                            val response = RetrofitClient.api.createDeck("Bearer $token", request)
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
                enabledSave = deckName.isNotBlank()
            )
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                "Home",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF191647),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn {
                items(deckList) { deck ->
                    DeckCard(deck)
                    Spacer(modifier = Modifier.height(12.dp))
                }
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
    enabledSave: Boolean
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
                    text = "Tạo Deck Mới",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF191647),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Thêm một deck flashcard mới để bắt đầu học tập",
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
                    label = { Text("Tên Deck") },
                    placeholder = { Text("Nhập tên deck...") },
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
                    label = { Text("Mô tả") },
                    placeholder = { Text("Nhập mô tả deck...") },
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
                        Text("Hủy", fontWeight = FontWeight.Medium)
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
                            Text("Tạo Deck", fontWeight = FontWeight.Bold)
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
            Text("Quyền riêng tư", fontWeight = FontWeight.Medium, color = Color(0xFF191647))
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




