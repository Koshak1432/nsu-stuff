package com.example.jokeswithpunchline.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokeswithpunchline.config.RetrofitInstance
import com.example.jokeswithpunchline.model.Joke
import com.example.jokeswithpunchline.network.JokesApi
import kotlinx.coroutines.launch

class JokesViewModel: ViewModel() {
    var jokeListResponse: List<Joke> by mutableStateOf(listOf())

    fun getJokeList() {
        viewModelScope.launch {
            Log.i("GetRandomJokes", "get joke list")
            try {
                jokeListResponse = RetrofitInstance.api.getRandomJokes()
            } catch (e: Exception) {
                Log.e("GetRandomJokes", "getJokeList: caught exception", )
            }
        }
    }
}