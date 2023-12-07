package com.example.jokeswithpunchline.network

import com.example.jokeswithpunchline.model.Joke
import retrofit2.http.GET

interface JokesApi {
    @GET("jokes/ten")
    suspend fun getRandomJokes() : List<Joke>
}