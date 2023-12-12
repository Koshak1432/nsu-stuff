package com.example.jokeswithpunchline.repository

import com.example.jokeswithpunchline.config.RetrofitInstance
import com.example.jokeswithpunchline.model.Joke
import kotlinx.coroutines.delay

class JokesRepository {
    suspend fun getJokes(page: Int, pageSize: Int): Result<List<Joke>> {
        delay(2000L)
        val response = try {
            RetrofitInstance.api.getRandomJokes()
        } catch (e: Exception) {
            return Result.failure(e)
        }
        if (response.isSuccessful && response.body() != null) {
            return Result.success(response.body()!!)
        }
        return Result.failure(Exception("Couldn't get jokes :("))
    }
}