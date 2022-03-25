package com.kwmkade.kotlinsamplewordbook

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CardViewModel : ViewModel() {
    val CARDS = MutableLiveData<Card>()

    private val mRecordedWords: MutableSet<String> = mutableSetOf()

    fun addWord(content: String) {
        if (mRecordedWords.contains(content)) {
            return
        }
        CARDS.value = Card(content)
        mRecordedWords.add(content)
    }
}