package com.example.jokeswithpunchline.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jokeswithpunchline.model.Joke

@Composable
fun JokeItem(joke: Joke) {
    Card(
        modifier = Modifier
            .padding(8.dp, 4.dp)
            .fillMaxWidth()
            .background(Color.Green)
    ) {
        Surface {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.Magenta)
            ) {
                Surface(shape = MaterialTheme.shapes.medium) {
                    Text(
                        text = joke.type,
                        modifier = Modifier.padding(all = 4.dp),
                        style = MaterialTheme.typography.body2
                    )
                }
                Column {
                    Text(text = joke.setup)
                    Column {
                        Text(text = joke.punchline)
                    }
                }
            }

        }
    }
}

@Composable
@Preview
fun JokePreview() {
    JokeItem(
        joke = Joke(
            1,
            "general",
            "How many kids with ADD does it take to change a lightbulb?",
            "Let's go ride bikes!"
        )
    )
}