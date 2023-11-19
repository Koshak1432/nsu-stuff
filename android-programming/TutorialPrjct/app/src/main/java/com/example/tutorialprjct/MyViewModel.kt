package com.example.tutorialprjct

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
//    var fragmentCounter = 0

    val currentCounter: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

}