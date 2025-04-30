package com.example.cinemaroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cinemaroom.ui.theme.CinemaRoomTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CinemaRoomTheme {
                val viewModel: MovieViewModel = viewModel()
                MainScreen(
                    viewModel,
                    onSearchMoviesClicked = { /* TODO */ },
                    onSearchActorsClicked = { /* TODO */ }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MovieViewModel,
    onSearchMoviesClicked: () -> Unit,
    onSearchActorsClicked: () -> Unit
) {
    var searchTittle by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchTittle,
            onValueChange = { searchTittle = it },
            label = { Text("Enter movie title") })

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {}) { Text(text = "Retrieve Movie") }
            Button(onClick = {}) { Text(text = "Save movie to Database") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.addMoviesToDb() }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Add Movies to DB")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSearchMoviesClicked, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Search for Movies")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSearchActorsClicked, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Search for Actors")
        }

    }
}

@Composable
fun MovieDetails(movie: Movie) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text("Title: ${movie.title}")
        Text("Year: ${movie.year}")
        Text("Rated: ${movie.rated}")
        Text("Released: ${movie.released}")
        Text("Runtime: ${movie.runtime}")
        Text("Genre: ${movie.genre}")
        Text("Director: ${movie.director}")
        Text("Writer: ${movie.writer}")
        Text("Actors: ${movie.actors}")
        Text("Plot: ${movie.plot}")
    }
}
