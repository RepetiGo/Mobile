package com.example.flashcardappandroid.ui.sharedscreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.flashcardappandroid.data.DeckResponse
import androidx.compose.material.icons.Icons
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun DeckCard(
    deck: DeckResponse,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() }
            .width(320.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF8FCFF),
                                Color(0xFFEEF7FF)
                            )
                        )
                    )
            )

            // Content - Centered layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Visibility indicator at top center
                Box(
                    modifier = Modifier
                        .background(
                            color = if (deck.visibility == 1)
                                Color(0xFF4CAF50).copy(alpha = 0.1f)
                            else
                                Color(0xFFFF9800).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (deck.visibility == 1)
                                Icons.Default.Public
                            else
                                Icons.Default.Lock,
                            contentDescription = if (deck.visibility == 1) "Public" else "Private",
                            tint = if (deck.visibility == 1)
                                Color(0xFF4CAF50)
                            else
                                Color(0xFFFF9800),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (deck.visibility == 1) "Public" else "Private",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (deck.visibility == 1)
                                Color(0xFF4CAF50)
                            else
                                Color(0xFFFF9800),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title - centered
                Text(
                    text = deck.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF191647),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description - centered
                Text(
                    text = deck.description?.takeIf { it.isNotBlank() } ?: "Không có mô tả",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats - centered horizontally
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(32.dp)
                            .background(Color(0xFFE0E0E0))
                    )
                }
            }
        }
    }
}
