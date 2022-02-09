package com.bay.wordlesolver.domain

import android.app.Application
import androidx.compose.ui.platform.LocalContext

class DictionaryData: Application() {
    companion object {
        var guesses = mutableListOf<String>()
    }
}
