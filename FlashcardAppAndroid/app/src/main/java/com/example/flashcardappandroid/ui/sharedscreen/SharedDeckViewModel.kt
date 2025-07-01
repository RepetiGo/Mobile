package com.example.flashcardappandroid.ui.sharedscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.flashcardappandroid.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.flashcardappandroid.data.DeckResponse
import com.example.flashcardappandroid.data.UserSession

class SharedDeckViewModel : ViewModel() {

    var deckList by mutableStateOf<List<DeckResponse>>(emptyList())
        private set

    var isLoaded by mutableStateOf(false)
        private set

    fun loadSharedDecksIfNeeded(context: Context) {
        if (isLoaded) return

        // Ưu tiên dùng deckList từ UserSession nếu có
        UserSession.shareddeckList?.let {
            deckList = it
            isLoaded = true
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.getsharedDecks()
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
        if (isLoaded) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.getsharedDecks()
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    deckList = response.body()?.data ?: emptyList()
                    isLoaded = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

