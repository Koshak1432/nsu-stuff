package com.example.jokeswithpunchline

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jokeswithpunchline.model.Joke
import com.example.jokeswithpunchline.ui.theme.JokesWithPunchlineTheme
import com.example.jokeswithpunchline.view.JokeItem
import com.example.jokeswithpunchline.viewmodel.JokesViewModel

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JokesWithPunchlineTheme {
//                Log.i("main activity", "onCreate: getting joke list")
//                JokeList(jokeList = jokesViewModel.jokeListResponse, jokesViewModel)
//                jokesViewModel.getJokeList()
                val jokesViewModel by viewModels<JokesViewModel>()
                val state = jokesViewModel.state
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.items.size) { i ->
                        if (i >= state.items.size -1 && !state.isLoading) {
                            jokesViewModel.loadNextJokes()
                        }
                        JokeItem(joke = state.items[i])
                    }
                    item {
                        if (state.isLoading) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()

                            }
                        }
                    }
                }
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
