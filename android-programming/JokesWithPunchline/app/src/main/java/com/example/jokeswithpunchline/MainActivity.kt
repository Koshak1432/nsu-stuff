package com.example.jokeswithpunchline

//import com.example.jokeswithpunchline.ui.theme.JokesWithPunchlineTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jokeswithpunchline.view.JokeItem
import com.example.jokeswithpunchline.viewmodel.JokesViewModel

class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val jokesViewModel by viewModels<JokesViewModel>()
            val state = jokesViewModel.state
            val pullRefreshState = rememberPullRefreshState(
                refreshing = jokesViewModel.state.isRefreshing,
                onRefresh = jokesViewModel::reset
            )
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xff4ea8de))
                ) {
                    items(state.items.size) { i ->
                        if (i >= state.items.size - 1 && !state.isLoading) {
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
                PullRefreshIndicator(
                    refreshing = state.isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}
