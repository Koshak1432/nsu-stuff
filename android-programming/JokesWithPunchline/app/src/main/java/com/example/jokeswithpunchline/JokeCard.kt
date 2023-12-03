package com.example.jokeswithpunchline

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun JokeCard(joke: Joke) {
    Row(modifier = Modifier.padding(all = 10.dp)) {
        Column {
            Text(
                text = joke.punchline,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = { }) {
                Text(
                    text = "Show punchline",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                text = joke.punchline
            )
        }
    }
}

@Preview
@Composable
fun JokeCardPreview() {
    JokeCard(joke = Joke("haha setup", "haha punch", "general", 14))
}