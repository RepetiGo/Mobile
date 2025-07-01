package com.example.flashcardappandroid

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flashcardappandroid.network.RetrofitClient
import com.example.flashcardappandroid.ui.aigenscreen.AiGenCardResultScreen
import com.example.flashcardappandroid.ui.flashcardscreen.CardListScreen
import com.example.flashcardappandroid.ui.flashcardscreen.FlashcardStudyScreen
import com.example.flashcardappandroid.ui.flashcardscreen.RepetitionScreen
import com.example.flashcardappandroid.ui.profilescreen.SettingsScreen
import com.example.flashcardappandroid.ui.screen.LoginScreen
import com.example.flashcardappandroid.ui.screen.RegisterScreen
import com.example.flashcardappandroid.ui.screen.HomeScreen
import com.example.flashcardappandroid.ui.screen.VerifyScreen
import com.example.flashcardappandroid.ui.screen.ForgotPasswordScreen
import com.example.flashcardappandroid.ui.screen.ResetPasswordScreen
import com.example.flashcardappandroid.ui.theme.FlashcardAppAndroidTheme
import com.example.flashcardappandroid.ui.profilescreen.EditProfileScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        RetrofitClient.init(applicationContext)
        super.onCreate(savedInstanceState)
        setContent {
            FlashcardAppAndroidTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }
                    composable(
                        route = "home?tab={tab}",
                        arguments = listOf(
                            navArgument("tab") {
                                type = NavType.IntType
                                defaultValue = 0
                            }
                        )
                    ) { backStackEntry ->
                        val tabIndex = backStackEntry.arguments?.getInt("tab") ?: 0
                        HomeScreen(navController, initialTab = tabIndex)
                    }
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
                    composable("deck_cards/{deckId}") { backStackEntry ->
                        val deckId = backStackEntry.arguments?.getString("deckId")?.toIntOrNull()
                        deckId?.let {
                            CardListScreen(deckId = it, navController = navController)
                        }
                    }
                    composable(
                        route = "ai_card_result/{topic}/{frontText}/{backText}/{generateImage}",
                        arguments = listOf(
                            navArgument("topic") { type = NavType.StringType },
                            navArgument("frontText") {
                                type = NavType.StringType
                                defaultValue = ""
                            },
                            navArgument("backText") {
                                type = NavType.StringType
                                defaultValue = ""
                            },
                            navArgument("generateImage") {
                                type = NavType.BoolType
                            }
                        )
                    ) { backStackEntry ->
                        val topic = backStackEntry.arguments?.getString("topic") ?: ""
                        val frontText = backStackEntry.arguments?.getString("frontText")?.takeIf { it.isNotEmpty() }
                        val backText = backStackEntry.arguments?.getString("backText")?.takeIf { it.isNotEmpty() }
                        val generateImage = backStackEntry.arguments?.getBoolean("generateImage") ?: false

                        AiGenCardResultScreen(
                            topic = topic,
                            frontText = frontText,
                            backText = backText,
                            generateImage = generateImage,
                            navController = navController
                        )
                    }
                    composable("settings") { SettingsScreen(navController) }
                    composable("edit-profile") { EditProfileScreen(navController) }

                }
            }
        }
    }
}
