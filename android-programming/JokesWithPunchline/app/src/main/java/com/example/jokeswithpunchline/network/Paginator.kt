package com.example.jokeswithpunchline.network

interface Paginator<Key, Item> {
    suspend fun loadNextItems()
    fun reset()
}