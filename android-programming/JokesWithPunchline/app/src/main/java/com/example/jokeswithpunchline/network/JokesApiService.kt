package com.example.jokeswithpunchline.network

import com.example.jokeswithpunchline.config.Constants.BASE_URL
import com.example.jokeswithpunchline.model.Joke
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface JokesApiService {
    @GET("jokes/ten")
    suspend fun getRandomJokes() : List<Joke>


    companion object {
        private var apiService: JokesApiService? = null
        fun getInstance(): JokesApiService {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(JokesApiService::class.java)
            }
            return apiService!!
        }
    }
}