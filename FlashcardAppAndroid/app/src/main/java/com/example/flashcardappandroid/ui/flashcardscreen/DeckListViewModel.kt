import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.flashcardappandroid.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.flashcardappandroid.data.DeckResponse
import com.example.flashcardappandroid.data.UserSession
class DeckListViewModel : ViewModel() {

    var deckList by mutableStateOf<List<DeckResponse>>(emptyList())
        private set

    var isLoaded by mutableStateOf(false)
        private set

    var statLearnedPercent by mutableStateOf("0")
        private set

    var statDailyAverage by mutableStateOf("0")
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadDecksIfNeeded(context: Context) {
        if (isLoaded) return

        // Ưu tiên dùng deckList từ UserSession nếu có
        UserSession.deckList?.let {
            deckList = it
            statDailyAverage = UserSession.statDailyAverage.toString()
            statLearnedPercent = UserSession.statLearnedPercent.toString()
            isLoaded = true
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.getDecks()
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    deckList = response.body()?.data ?: emptyList()
                    UserSession.deckList = deckList
                    isLoaded = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Đồng thời gọi API lấy thống kê
        loadStats(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun reloadDecks(context: Context) {
        isLoaded = false

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.getDecks()
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    deckList = response.body()?.data ?: emptyList()
                    UserSession.deckList = deckList
                    isLoaded = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        loadStats(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadStats(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.getStats()

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val stats = response.body()?.data
                    val learnedPercent = stats?.dayLearnedPercent ?: 0.0
                    val average = stats?.dailyAverage ?: 0.0

                    statLearnedPercent = learnedPercent.toString()
                    statDailyAverage = average.toInt().toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


