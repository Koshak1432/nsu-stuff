package com.example.jokeswithpunchline.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokeswithpunchline.config.Constants
import com.example.jokeswithpunchline.model.Joke
import com.example.jokeswithpunchline.network.DefaultPaginator
import com.example.jokeswithpunchline.repository.JokesRepository
import kotlinx.coroutines.launch

class JokesViewModel : ViewModel() {
    private val repository = JokesRepository()
    var state by mutableStateOf(ScreenState())

    private val paginator = DefaultPaginator(
        initialKey = state.page,
        onLoadUpdated = {
            state = state.copy(isLoading = it)
        },
        onRequest = { nextPage ->
            repository.getJokes(nextPage, Constants.JOKES_NUM)
        },
        getNextKey = {
            state.page + 1
        },
        onError = {
            state = state.copy(error = it?.localizedMessage)
        },
        onSuccess = { items, newKey ->
            state = state.copy(
                items = state.items + items,
                page = newKey
            )
        }
    )

    init {
        loadNextJokes()
    }

    fun loadNextJokes() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    fun reset() {
        state = ScreenState()
        loadNextJokes()
    }
}

data class ScreenState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val items: List<Joke> = emptyList(),
    val error: String? = null,
    val page: Int = 0
)