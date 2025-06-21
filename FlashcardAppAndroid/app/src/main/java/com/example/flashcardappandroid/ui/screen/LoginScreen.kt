
package com.example.flashcardappandroid.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardappandroid.data.LoginRequest
import com.example.flashcardappandroid.data.TokenManager
import com.example.flashcardappandroid.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        // Phần chính (ở giữa màn hình)
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val response = RetrofitClient.api.login(LoginRequest(email, password))
                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                if (loginResponse != null) {
                                    TokenManager(context).saveTokens(
                                        loginResponse.data.accessToken,
                                        loginResponse.data.refreshToken,
                                        loginResponse.data.id
                                    )
                                    Toast.makeText(context, "Login success!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("home")
                                }
                            } else {
                                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { navController.navigate("forgot-password") }) {
                Text("Forgot password?")
            }
        }

        // Phần "Don't have an account?" (ở dưới cùng)
        TextButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier.align(Alignment.BottomCenter) // Căn dưới cùng
        ) {
            Text("Don't have an account? Register")
        }
    }
}
