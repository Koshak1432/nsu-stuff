package com.example.tutorialprjct

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel() : ViewModel() {
    val currentCounter: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    private val isTextShow: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun setTextShow(isShown: Boolean) {
        isTextShow.value = isShown
    }

    fun isTextShow(): Boolean{
        return isTextShow.value == true
    }


    fun updateCounter(numFragments: Int) {
        currentCounter.value = numFragments
    }

    fun getCounter(): Int? {
        return currentCounter.value
    }

}