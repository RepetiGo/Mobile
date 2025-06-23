package com.example.flashcardappandroid.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardappandroid.ui.flashcardscreen.DeckListScreen

@Composable
fun HomeScreen(navController: NavController) {
    var selectedIndex by remember { mutableStateOf(0) }

    val items = listOf("Trang chủ", "Quiz", "GroupChat", "Profile")
    val icons = listOf(Icons.Default.Home, Icons.Default.Quiz, Icons.Default.Chat, Icons.Default.Person)

    Scaffold(
        bottomBar = {
            // Custom Navigation Bar với thiết kế hiện đại
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp, vertical = 2.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 12.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationItem(
                            icon = icons[index],
                            label = item,
                            isSelected = selectedIndex == index,
                            onClick = { selectedIndex = index },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 69.dp)
        ) {
            when (selectedIndex) {
                0 -> DeckListScreen(navController)
                1 -> QuizScreen()
                2 -> GroupChatScreen()
                3 -> ProfileScreen(navController)
            }
        }
    }
}

@Composable
private fun NavigationItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animationDuration = 300
    val iconSize by animateDpAsState(
        targetValue = if (isSelected) 28.dp else 24.dp,
        animationSpec = tween(animationDuration),
        label = "iconSize"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF191647) else Color(0xFF9E9E9E),
        animationSpec = tween(animationDuration),
        label = "iconColor"
    )

    val labelColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF191647) else Color(0xFF9E9E9E),
        animationSpec = tween(animationDuration),
        label = "labelColor"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                indication = LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon với background animated
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (isSelected) Color(0xFF94D5F5).copy(alpha = 0.2f) else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                )
                .animateContentSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Label với animation
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Indicator dot
        if (isSelected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        color = Color(0xFF94D5F5),
                        shape = CircleShape
                    )
            )
        }
    }
}