package com.example.jokeswithpunchline.model

data class Joke(
    val id: Int,
    val type: String,
    val setup: String,
    val punchline: String
)
