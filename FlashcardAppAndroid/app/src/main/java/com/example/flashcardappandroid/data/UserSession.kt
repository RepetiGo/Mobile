package com.example.flashcardappandroid.data

object UserSession {
    var currentUser: ProfileResponse? = null
    var bio: String? = "Tell us about yourself"
    var shouldReloadProfile: Boolean = false
    var deckList: List<DeckResponse>? = null
    var shareddeckList: List<DeckResponse>? = null
    var statLearnedPercent: String? = null
    var statDailyAverage: String? = null
}