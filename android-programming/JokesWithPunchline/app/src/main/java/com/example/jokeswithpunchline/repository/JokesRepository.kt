package com.example.jokeswithpunchline.repository

import android.util.Log
import com.example.jokeswithpunchline.config.RetrofitInstance
import com.example.jokeswithpunchline.model.Joke
import kotlinx.coroutines.delay

class JokesRepository {
    suspend fun getJokes(page: Int, pageSize: Int): Result<List<Joke>> {
        delay(1000L)
        val response = try {
            RetrofitInstance.api.getRandomJokes()
        } catch (e: Exception) {
            Log.e("ERROR", "caught an exception")
            return Result.failure(e)
        }
        if (response.isSuccessful && response.body() != null) {
            Log.i("INFO", "SUCCESS")
            return Result.success(response.body()!!)
        }
        Log.i("INFO", "FAIL")
        return Result.failure(Exception("HUI"))
    }
}