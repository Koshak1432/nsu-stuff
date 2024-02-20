package com.example.jokeswithpunchline.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jokeswithpunchline.model.Joke

@Composable
fun JokeItem(joke: Joke) {
    var isPunchlineVisible by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(8.dp, 4.dp)
            .fillMaxWidth(),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row {
                Text(
                    text = joke.type,
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color(0xffcbf3f0)),
                    style = MaterialTheme.typography.body2
                )
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { isPunchlineVisible = !isPunchlineVisible },
                    modifier = Modifier
                        .height(30.dp)
                        .padding(3.dp),
                    contentPadding = PaddingValues(3.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xff2ec4b6),
                        contentColor = Color(0xffcbf3f0)
                    )
                ) {
                    Text(
                        text = if (isPunchlineVisible) "Hide punch" else "Show punch"
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = joke.setup,
                    style = MaterialTheme.typography.body1
                )
                if (isPunchlineVisible) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = joke.punchline,
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold
                    )
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