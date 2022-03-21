package com.kwmkade.kotlinsamplewordbook

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CardViewModel : ViewModel() {
    val CARDS = MutableLiveData<Card>()

    fun addWord(content: String) {
        CARDS.value = Card(content)
    }
}