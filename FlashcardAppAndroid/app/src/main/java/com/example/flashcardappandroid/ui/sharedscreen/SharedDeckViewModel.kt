package com.example.flashcardappandroid.ui.sharedscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.flashcardappandroid.data.TokenManager
import com.example.flashcardappandroid.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.flashcardappandroid.data.DeckResponse

class SharedDeckViewModel : ViewModel() {

    var deckList by mutableStateOf<List<DeckResponse>>(emptyList())
        private set

    var isLoaded by mutableStateOf(false)
        private set

    fun loadSharedDecksIfNeeded(context: Context) {
        if (isLoaded) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = TokenManager(context).getAccessToken()
                val response = RetrofitClient.api.getsharedDecks("Bearer $token")
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    deckList = response.body()?.data ?: emptyList()
                    isLoaded = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun reloadsharedDecks(context: Context) {
        isLoaded = false
        loadSharedDecksIfNeeded(context)
    }
}

