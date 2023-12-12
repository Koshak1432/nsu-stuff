package com.example.jokeswithpunchline.repository

import com.example.jokeswithpunchline.model.Joke
import kotlinx.coroutines.delay

class JokesRepository {
    private val remoteDataSource = (1..100).map {
        Joke(
            id = it,
            type = "type $it",
            setup = "setup $it",
            punchline = "punch $it"
        )
    }
    suspend fun getJokes(page: Int, pageSize: Int): Result<List<Joke>> {
        delay(2000L)
        val startingIndex = page * pageSize
        return if(startingIndex + pageSize <= remoteDataSource.size) {
            Result.success(remoteDataSource.slice(startingIndex until startingIndex + pageSize))
        } else {
            Result.success(emptyList())
        }
    }
}