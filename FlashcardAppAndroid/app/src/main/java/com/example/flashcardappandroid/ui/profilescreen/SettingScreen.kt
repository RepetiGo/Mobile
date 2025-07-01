package com.example.flashcardappandroid.ui.profilescreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flashcardappandroid.network.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var newCardsPerDay by remember { mutableStateOf("20") }
    var maxReviewsPerDay by remember { mutableStateOf("200") }
    var startingEasinessFactor by remember { mutableStateOf("2.5") }
    var learningSteps by remember { mutableStateOf("25m 1d") }
    var graduatingInterval by remember { mutableStateOf("3") }
    var easyInterval by remember { mutableStateOf("4") }
    var relearningSteps by remember { mutableStateOf("30m") }
    var minimumInterval by remember { mutableStateOf("1") }
    var maximumInterval by remember { mutableStateOf("180") }
    var easyBonus by remember { mutableStateOf("1.5") }
    var hardInterval by remember { mutableStateOf("1.2") }
    var newInterval by remember { mutableStateOf("0.2") }

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
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = Color(0xFF191647),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Settings",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191647)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF191647)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // General Settings Card
                SettingsCard(
                    title = "General Settings",
                    icon = Icons.Default.Tune,
                    iconColor = Color(0xFF4CAF50)
                ) {
                    SettingField(
                        label = "New Cards Per Day",
                        hint = "Số lượng thẻ 'new' tối đa mỗi ngày (mặc định: 20)",
                        value = newCardsPerDay,
                        onValueChange = { newCardsPerDay = it }
                    )

                    SettingField(
                        label = "Max Reviews Per Day",
                        hint = "Số lượng thẻ 'review' tối đa mỗi ngày (mặc định: 200)",
                        value = maxReviewsPerDay,
                        onValueChange = { maxReviewsPerDay = it }
                    )

                    SettingField(
                        label = "Starting Easiness Factor",
                        hint = "Chỉ số nhân khi nhấn 'Good', mặc định: 2.5",
                        value = startingEasinessFactor,
                        onValueChange = { startingEasinessFactor = it }
                    )
                }

                // Learning Settings Card
                SettingsCard(
                    title = "Learning Settings",
                    icon = Icons.Default.School,
                    iconColor = Color(0xFF2196F3)
                ) {
                    SettingField(
                        label = "Learning Steps",
                        hint = "Khoảng thời gian giữa các bước học (mặc định: 25m 1d)",
                        value = learningSteps,
                        onValueChange = { learningSteps = it }
                    )

                    SettingField(
                        label = "Graduating Interval",
                        hint = "Số ngày chờ sau khi hoàn thành học (mặc định: 3)",
                        value = graduatingInterval,
                        onValueChange = { graduatingInterval = it }
                    )

                    SettingField(
                        label = "Easy Interval",
                        hint = "Số ngày khi nhấn 'Easy' (mặc định: 4)",
                        value = easyInterval,
                        onValueChange = { easyInterval = it }
                    )

                    SettingField(
                        label = "Relearning Steps",
                        hint = "Thời gian nhấn 'Again' với thẻ 'review' (mặc định: 30m)",
                        value = relearningSteps,
                        onValueChange = { relearningSteps = it }
                    )
                }

                // Advanced Settings Card
                SettingsCard(
                    title = "Advanced Settings",
                    icon = Icons.Default.Psychology,
                    iconColor = Color(0xFFFF9800)
                ) {
                    SettingField(
                        label = "Minimum Interval",
                        hint = "Thời gian tối thiểu giữa các lần ôn (mặc định: 1)",
                        value = minimumInterval,
                        onValueChange = { minimumInterval = it }
                    )

                    SettingField(
                        label = "Maximum Interval",
                        hint = "Thời gian tối đa giữa các lần ôn (mặc định: 180)",
                        value = maximumInterval,
                        onValueChange = { maximumInterval = it }
                    )

                    SettingField(
                        label = "Easy Bonus",
                        hint = "Hệ số khi nhấn 'Easy' (mặc định: 1.5)",
                        value = easyBonus,
                        onValueChange = { easyBonus = it }
                    )

                    SettingField(
                        label = "Hard Interval",
                        hint = "Hệ số khi nhấn 'Hard' (mặc định: 1.2)",
                        value = hardInterval,
                        onValueChange = { hardInterval = it }
                    )

                    SettingField(
                        label = "New Interval",
                        hint = "Hệ số khi nhấn 'Again' (mặc định: 0.2)",
                        value = newInterval,
                        onValueChange = { newInterval = it },
                        isLast = true
                    )
                }

                // Save Button
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
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    val settings = mapOf(
                                        "newCardsPerDay" to newCardsPerDay.toIntOrNull(),
                                        "maxReviewsPerDay" to maxReviewsPerDay.toIntOrNull(),
                                        "startingEasinessFactor" to startingEasinessFactor.toDoubleOrNull(),
                                        "learningSteps" to learningSteps,
                                        "graduatingInterval" to graduatingInterval.toIntOrNull(),
                                        "easyInterval" to easyInterval.toIntOrNull(),
                                        "relearningSteps" to relearningSteps,
                                        "minimumInterval" to minimumInterval.toIntOrNull(),
                                        "maximumInterval" to maximumInterval.toIntOrNull(),
                                        "easyBonus" to easyBonus.toDoubleOrNull(),
                                        "hardInterval" to hardInterval.toDoubleOrNull(),
                                        "newInterval" to newInterval.toDoubleOrNull(),
                                    )
                                    val response = RetrofitClient.api.updateSettings(settings)
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Lưu thành công", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    } else {
                                        Toast.makeText(context, "Lưu thất bại", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF94D5F5),
                            contentColor = Color(0xFF191647)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .height(56.dp)
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Save Settings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    content: @Composable () -> Unit
) {
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = iconColor.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191647)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
private fun SettingField(
    label: String,
    hint: String,
    value: String,
    onValueChange: (String) -> Unit,
    isLast: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                color = Color(0xFF191647)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF94D5F5),
            unfocusedBorderColor = Color(0xFF94D5F5).copy(alpha = 0.5f),
            focusedLabelColor = Color(0xFF94D5F5),
            unfocusedLabelColor = Color(0xFF666666),
            focusedTextColor = Color(0xFF191647),
            unfocusedTextColor = Color(0xFF191647),
            cursorColor = Color(0xFF94D5F5)
        ),
        singleLine = true
    )

    Text(
        text = hint,
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF666666),
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )

    if (!isLast) {
        Spacer(modifier = Modifier.height(16.dp))
    }
}