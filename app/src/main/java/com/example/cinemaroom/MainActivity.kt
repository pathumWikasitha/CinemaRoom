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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.cinemaroom.ui.theme.CinemaRoomTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : ComponentActivity() {
    private lateinit var db: MovieDatabase
    private lateinit var movieDao: MovieDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(applicationContext, MovieDatabase::class.java, "movie_database")
            .build()
        movieDao = db.movieDao()

        enableEdgeToEdge()
        setContent {
            CinemaRoomTheme {
                MyApp(
                    movieDao = movieDao,
                    onAddMoviesClicked = { addMoviesToDatabase() },
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
fun MyApp(
    onAddMoviesClicked: () -> Unit,
    movieDao: MovieDao
) {
    var currentScreen by rememberSaveable { mutableStateOf("home") }

    when (currentScreen) {
        "home" -> HomeScreen(
            onAddMoviesClicked = onAddMoviesClicked,
            onSearchMoviesClicked = { currentScreen = "search_movies" },
            onSearchActorsClicked = { currentScreen = "search_actors" }
        )

        "search_movies" -> SearchMoviesScreen(
            movieDao = movieDao,
            onBack = { currentScreen = "home" }
        )

        "search_actors" -> SearchActorsScreen(
            onBack = { currentScreen = "home" },
            movieDao = movieDao
        )
    }
}

@Composable
fun HomeScreen(
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


@Composable
fun SearchMoviesScreen(onBack: () -> Unit, movieDao: MovieDao) {
    var movieTitle by rememberSaveable { mutableStateOf("") }
    var movieInfo: Movie? by remember { mutableStateOf(null) }
    // Create a CoroutineScope bound
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = movieTitle,
            onValueChange = { movieTitle = it },
            label = { Text("Enter movie title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                scope.launch {
                    movieInfo = null
                    movieInfo = fetchMovie(movieTitle)
                }
            }) {
                Text("Retrieve Movie")
            }
            Button(onClick = {
                movieInfo?.let {
                    scope.launch {
                        movieDao.insertMovies(listOf(it))
                    }
                }
            }) {
                Text("Save to DB")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        movieInfo?.title?.let { Text(text = it) }
        movieInfo?.director?.let { Text(text = it) }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}

suspend fun fetchMovie(title: String): Movie? {
    val apiKey = "42438be8"
    val urlString = "https://www.omdbapi.com/?t=$title&apikey=$apiKey"
    val url = URL(urlString)
    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

    val stb = StringBuilder()

    withContext(Dispatchers.IO) {
        val br = BufferedReader(InputStreamReader(connection.inputStream))
        var line: String? = br.readLine()
        while (line != null) {
            stb.append(line)
            line = br.readLine()
        }
    }
    val json = JSONObject(stb.toString())
    // Check if movie was found
    if (json.optString("Response") == "False") {
        return null
    }
    return Movie(
        title = json.optString("Title"),
        year = json.optString("Year"),
        id = json.optString("imdbID"),
        rated = json.optString("imdbRating"),
        released = json.optString("Released"),
        runtime = json.optString("Runtime"),
        genre = json.optString("Genre"),
        director = json.optString("Director"),
        writer = json.optString("Writer"),
        actors = json.optString("Actors"),
        plot = json.optString("Plot"),
    )
}


@Composable
fun SearchActorsScreen(onBack: () -> Unit, movieDao: MovieDao) {
    var searchedActor by rememberSaveable { mutableStateOf("") }
    var movieList by remember { mutableStateOf<List<Movie>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = searchedActor,
            onValueChange = { searchedActor = it },
            label = { Text("Enter actor name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            coroutineScope.launch {
                movieList = movieDao.searchMoviesByActor(searchedActor)
            }
        }) { Text(text = "Search") }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Back to Home")
        }
        LazyColumn {
            items(movieList.size){ movie ->
                MovieItem(movie = movieList[movie])
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = movie.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "Actors: ${movie.actors}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
