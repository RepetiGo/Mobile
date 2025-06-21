
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
import com.example.flashcardappandroid.data.RegisterRequest
import com.example.flashcardappandroid.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
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
                Text("Register", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth() // Đặt độ rộng tối đa
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth() // Đặt độ rộng tối đa
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth() // Đặt độ rộng tối đa
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                val response = RetrofitClient.api.register(RegisterRequest(username, email, password))
                                if (response.isSuccessful) {
                                    navController.navigate("verify/${email}")
                                } else {
                                    Toast.makeText(context, "Register failed", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth() // Đặt độ rộng tối đa
                ) {
                    Text("Register")
                }
            }

            // Phần "Already have an account?" (ở dưới cùng)
            TextButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.align(Alignment.BottomCenter) // Căn dưới cùng
            ) {
                Text("Already have an account? Login")
            }
        }
    }
}

