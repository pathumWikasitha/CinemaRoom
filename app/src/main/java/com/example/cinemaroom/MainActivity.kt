package com.example.cinemaroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cinemaroom.ui.theme.CinemaRoomTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.fillMaxWidth


class MainActivity : ComponentActivity() {
    private lateinit var db: MovieDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = MovieDatabase.getDatabase(this)
        enableEdgeToEdge()
        setContent {
            CinemaRoomTheme {
                MainScreen(
                    onAddMoviesClicked = { addMoviesToDatabase() },
                    onSearchMoviesClicked = { /* TODO */ },
                    onSearchActorsClicked = { /* TODO */ }
                )
            }
        }
    }
    private fun addMoviesToDatabase() {
        val movies = MovieRepository().getHardcodedMovies()
        CoroutineScope(Dispatchers.IO).launch {
            db.movieDao().insertMovies(movies)
        }
    }
}

@Composable
fun MainScreen(
    onAddMoviesClicked: () -> Unit,
    onSearchMoviesClicked: () -> Unit,
    onSearchActorsClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onAddMoviesClicked, modifier = Modifier.fillMaxWidth()) {
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

//@Composable
//fun MainScreen() {
//    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Button(onClick = { }) {
//                Text("Add Movies to DB")
//            }
//            Button(onClick = { /* TODO: Implement Search for Movies */ }) {
//                Text("Search for Movies")
//            }
//            Button(onClick = { /* TODO: Implement Search for Actors */ }) {
//                Text("Search for Actors")
//            }
//        }
//
//    }
//
//}