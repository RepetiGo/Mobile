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
import com.example.flashcardappandroid.data.ForgotPasswordRequest

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Quên mật khẩu", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    isLoading = true
                    try {
                        val response = RetrofitClient.api.forgotPassword(ForgotPasswordRequest(email))
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Vui lòng kiểm tra email để tiếp tục", Toast.LENGTH_SHORT).show()
                            navController.navigate("reset-password")
                        } else {
                            Toast.makeText(context, "Email không hợp lệ hoặc lỗi hệ thống", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = email.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tiếp tục")
        }
    }
}
