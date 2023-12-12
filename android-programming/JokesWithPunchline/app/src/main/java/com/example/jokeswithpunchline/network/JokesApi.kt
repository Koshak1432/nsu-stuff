package com.example.jokeswithpunchline.network

import com.example.jokeswithpunchline.model.Joke
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface JokesApi {
    @GET("random_ten")
    suspend fun getRandomJokes() : Response<List<Joke>>
}