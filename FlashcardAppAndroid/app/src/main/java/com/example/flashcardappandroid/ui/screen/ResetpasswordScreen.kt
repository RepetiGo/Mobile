package com.example.flashcardappandroid.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardappandroid.network.RetrofitClient
import kotlinx.coroutines.launch
import android.widget.Toast
import com.example.flashcardappandroid.data.ResetPasswordRequest

@Composable
fun ResetPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Đặt lại mật khẩu", style = MaterialTheme.typography.headlineMedium)
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
            label = { Text("Mật khẩu mới") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Nhập lại mật khẩu") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Mã xác nhận (code)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (password != confirmPassword) {
                    Toast.makeText(context, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                coroutineScope.launch {
                    isLoading = true
                    try {
                        val request = ResetPasswordRequest(email, password, confirmPassword, code)
                        val response = RetrofitClient.api.resetPassword(request)
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Password change successful", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") {
                                popUpTo("reset-password") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Thất bại: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank() && code.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Xác nhận")
        }
    }
}
