package com.example.jokeswithpunchline

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.jokeswithpunchline.model.Joke
import com.example.jokeswithpunchline.ui.theme.JokesWithPunchlineTheme
import com.example.jokeswithpunchline.view.JokeItem
import com.example.jokeswithpunchline.viewmodel.JokesViewModel

class MainActivity : ComponentActivity() {
    private val jokesViewModel by viewModels<JokesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JokesWithPunchlineTheme {
                Log.i("main activity", "onCreate: getting joke list")
                JokeList(jokeList = jokesViewModel.jokeListResponse, jokesViewModel)
//                jokesViewModel.getJokeList()
            }
        }
    }
}

@Composable
fun JokeList(jokeList: List<Joke>, jokesViewModel: JokesViewModel) {
    LaunchedEffect(true) {
        jokesViewModel.getJokeList()
    }
    LazyColumn {
        items(items = jokeList) {item ->
            JokeItem(joke = item)
        }
    }
}
