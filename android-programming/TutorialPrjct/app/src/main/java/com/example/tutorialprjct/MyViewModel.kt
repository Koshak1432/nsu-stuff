package com.example.tutorialprjct

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class MyViewModel() : ViewModel() {
    val currentCounter: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    fun updateCounter(numFragments: Int) {
        currentCounter.value = 0
    }

}