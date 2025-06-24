package com.example.flashcardappandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flashcardappandroid.network.RetrofitClient
import com.example.flashcardappandroid.ui.flashcardscreen.FlashcardStudyScreen
import com.example.flashcardappandroid.ui.flashcardscreen.RepetitionScreen
import com.example.flashcardappandroid.ui.screen.LoginScreen
import com.example.flashcardappandroid.ui.screen.RegisterScreen
import com.example.flashcardappandroid.ui.screen.HomeScreen
import com.example.flashcardappandroid.ui.screen.VerifyScreen
import com.example.flashcardappandroid.ui.screen.ForgotPasswordScreen
import com.example.flashcardappandroid.ui.screen.ResetPasswordScreen
import com.example.flashcardappandroid.ui.theme.FlashcardAppAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        RetrofitClient.init(applicationContext)
        super.onCreate(savedInstanceState)
        setContent {
            FlashcardAppAndroidTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }
                    composable("home") { HomeScreen(navController) } // Thêm dòng này
                    composable("verify/{email}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        VerifyScreen(navController, email)
                    }
                    composable("forgot-password") { ForgotPasswordScreen(navController) }
                    composable("reset-password") { ResetPasswordScreen(navController) }
                    composable("flashcard_study/{deckId}") { backStackEntry ->
                        val deckId = backStackEntry.arguments?.getString("deckId")?.toIntOrNull()
                        deckId?.let { FlashcardStudyScreen(deckId = it, navController = navController) }
                    }
                    composable("repetition/{deckId}") { backStackEntry ->
                        val deckId = backStackEntry.arguments?.getString("deckId")?.toIntOrNull()
                        deckId?.let { RepetitionScreen(deckId = it, navController = navController) }
                    }
                }
            }
        }
    }
}
