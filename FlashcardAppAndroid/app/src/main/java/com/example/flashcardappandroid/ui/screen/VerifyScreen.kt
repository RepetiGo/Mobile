package com.example.flashcardappandroid.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardappandroid.network.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.widget.Toast

@Composable
fun VerifyScreen(navController: NavController, email: String) {
    val context = LocalContext.current
    var secondsLeft by remember { mutableStateOf(30) }
    val coroutineScope = rememberCoroutineScope()
    var isResending by remember { mutableStateOf(false) }

    // Countdown logic
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Verify Your Email", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "We've sent a verification link to your email. Click the link to verify.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (secondsLeft > 0) {
            Text("Resend code in $secondsLeft seconds")
        } else {
            Text(
                if (isResending) "Resending..." else "Resend verification link",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(enabled = !isResending) {
                    coroutineScope.launch {
                        isResending = true
                        try {
                            val response = RetrofitClient.api.resendConfirmationEmail(email)
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Verification email resent", Toast.LENGTH_SHORT).show()
                                secondsLeft = 30
                            } else {
                                Toast.makeText(context, "Resend failed", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isResending = false
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Back to Login",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                navController.navigate("login")
            }
        )
    }
}
