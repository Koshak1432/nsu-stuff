package com.example.jokeswithpunchline.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokeswithpunchline.model.Joke
import com.example.jokeswithpunchline.network.JokesApiService
import kotlinx.coroutines.launch

class JokesViewModel: ViewModel() {
    var jokeListResponse: List<Joke> by mutableStateOf(listOf())
    var errorMessage: String by mutableStateOf("")

    fun getJokeList() {
        viewModelScope.launch {
            val apiService = JokesApiService.getInstance()
            try {
                jokeListResponse = apiService.getRandomJokes()
            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }
}