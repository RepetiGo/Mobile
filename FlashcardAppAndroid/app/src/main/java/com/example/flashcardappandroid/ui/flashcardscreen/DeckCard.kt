package com.example.flashcardappandroid.ui.flashcardscreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.flashcardappandroid.data.DeckResponse

@Composable
fun DeckCard(deck: DeckResponse) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        modifier = Modifier.fillMaxWidth().height(150.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = deck.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF191647)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = deck.description ?: "...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF191647),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
