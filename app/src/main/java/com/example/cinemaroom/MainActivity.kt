package com.example.cinemaroom

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
            movieDao.insertMovies(movies)
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
            onSearchActorsClicked = { currentScreen = "search_actors" },
            onSearchByTitleClicked = { currentScreen = "search_movies_by_title" }
        )

        "search_movies" -> SearchMoviesScreen(
            movieDao = movieDao,
            onBack = { currentScreen = "home" }
        )

        "search_actors" -> SearchActorsScreen(
            onBack = { currentScreen = "home" },
            movieDao = movieDao
        )

        "search_movies_by_title" -> SearchMoviesByTitleScreen(
            onBack = { currentScreen = "home" }
        )
    }
}


@Composable
fun HomeScreen(
    onAddMoviesClicked: () -> Unit,
    onSearchMoviesClicked: () -> Unit,
    onSearchActorsClicked: () -> Unit,
    onSearchByTitleClicked: () -> Unit
) {
    BackgroundWrapper {
        FadeInContent {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Image(
                    modifier = Modifier.size(150.dp),
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Logo"
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(330.dp))
                val context = LocalContext.current
                Button(
                    onClick = {
                        onAddMoviesClicked()
                        Toast.makeText(context, "Movies added to Database", Toast.LENGTH_SHORT)
                            .show()
                    }, modifier = Modifier
                        .width(220.dp)
                        .height(55.dp)
                        .border(
                            2.dp,
                            Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(25.dp)
                        )
                        .padding(0.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.40f)
                    )
                ) {
                    Text(
                        text = "Add Movies to DB",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = onSearchMoviesClicked, modifier = Modifier
                        .width(220.dp)
                        .height(55.dp)
                        .border(
                            2.dp,
                            Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(25.dp)
                        )
                        .padding(0.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.40f)
                    )
                ) {
                    Text(
                        text = "Search for Movies",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = onSearchActorsClicked, modifier = Modifier
                        .width(220.dp)
                        .height(55.dp)
                        .border(
                            2.dp,
                            Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(25.dp)
                        )
                        .padding(0.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.40f)
                    )
                ) {
                    Text(
                        text = "Search for Actors",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = onSearchByTitleClicked, modifier = Modifier
                        .width(220.dp)
                        .height(55.dp)
                        .border(
                            2.dp,
                            Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(25.dp)
                        )
                        .padding(0.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.40f)
                    )
                ) {
                    Text(
                        text = "Search Movies by Title",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMoviesScreen(onBack: () -> Unit, movieDao: MovieDao) {
    var movieTitle by rememberSaveable { mutableStateOf("") }
    var movieInfo by rememberSaveable { mutableStateOf<List<Movie>>(emptyList()) }

    // Create a CoroutineScope bound
    val scope = rememberCoroutineScope()

    BackgroundWrapper {
        FadeInContent {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Image(
                    modifier = Modifier.size(150.dp),
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Logo"
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(25.dp))

                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(38.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back button",
                        modifier = Modifier.size(32.dp),
                    )
                }


                Spacer(modifier = Modifier.height(90.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White.copy(alpha = 0.15f),
                        unfocusedBorderColor = Color.White,
                        focusedBorderColor = Color.White,
                        focusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    value = movieTitle,
                    onValueChange = { movieTitle = it },
                    label = { Text("Enter movie title") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                movieInfo = fetchMovie(movieTitle)
                            }
                        },
                        modifier = Modifier
                            .height(50.dp)
                            .border(
                                2.dp,
                                Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(35.dp)
                            )
                            .padding(0.dp),
                        shape = RoundedCornerShape(35.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.45f)
                        )
                    ) {
                        Text("Retrieve Movie")
                    }
                    Button(
                        onClick = {
                            movieInfo.let {
                                scope.launch {
                                    movieDao.insertMovies(it)
                                }
                            }
                        }, modifier = Modifier
                            .height(50.dp)
                            .border(
                                2.dp,
                                Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(35.dp)
                            )
                            .padding(0.dp),
                        shape = RoundedCornerShape(35.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.45f)
                        )
                    ) {
                        Text("Save to DB")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Movie info")

                LazyColumn {
                    items(movieInfo.size) { movie ->
                        MovieItem(movie = movieInfo[movie])
                    }
                }

            }
        }
    }
}

suspend fun fetchMovie(title: String): MutableList<Movie> {
    val apiKey = "42438be8"
    val urlString = "https://www.omdbapi.com/?t=$title&apikey=$apiKey"
    val url = URL(urlString)
    val connection: HttpURLConnection =
        withContext(Dispatchers.IO) {
            url.openConnection()
        } as HttpURLConnection

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
    val movies = mutableListOf<Movie>()

    // Check if movie was found
    if (json.optString("Response") == "True") {
        movies.add(
            Movie(
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
                poster = json.optString("Poster")
            )
        )
    }
    return movies
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchActorsScreen(onBack: () -> Unit, movieDao: MovieDao) {
    var searchedActor by rememberSaveable { mutableStateOf("") }
    var movieList by rememberSaveable { mutableStateOf<List<Movie>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    BackgroundWrapper {
        FadeInContent {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Image(
                    modifier = Modifier.size(150.dp),
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Logo"
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(25.dp))
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(38.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back button",
                        modifier = Modifier.size(32.dp),
                    )
                }

                Spacer(modifier = Modifier.height(85.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        modifier = Modifier.width(285.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            unfocusedBorderColor = Color.White,
                            focusedBorderColor = Color.White,
                            focusedTextColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        value = searchedActor,
                        onValueChange = { searchedActor = it },
                        label = { Text("Enter actor name") },
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                movieList = movieDao.searchMoviesByActor(searchedActor)
                            }
                        },
                        modifier = Modifier
                            .size(55.dp)
                            .padding(0.dp)
                            .border(
                                2.dp,
                                Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(45.dp),
                            ),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(movieList.size) { movie ->
                        MovieItem(movie = movieList[movie])
                    }
                }
            }
        }
    }
}

//@Composable
//fun MovieItem(movie: Movie) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Column(modifier = Modifier.padding(8.dp)) {
//            Text(text = movie.title, style = MaterialTheme.typography.titleMedium)
//            Text(text = "Year: ${movie.year}", style = MaterialTheme.typography.bodySmall)
//            Text(text = "Released: ${movie.released}", style = MaterialTheme.typography.bodySmall)
//            Text(text = "Genre: ${movie.genre}", style = MaterialTheme.typography.bodySmall)
//            Text(text = "Director: ${movie.director}", style = MaterialTheme.typography.bodySmall)
//            Text(text = "Writer: ${movie.writer}", style = MaterialTheme.typography.bodySmall)
//            Text(text = "Actors: ${movie.actors}", style = MaterialTheme.typography.bodySmall)
//            Text(text = "Plot: ${movie.plot}", style = MaterialTheme.typography.bodySmall)
//
//        }
//    }
//}

@Composable
fun MovieItem(movie: Movie) {
    var imageBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    var showDetails by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Load image
    LaunchedEffect(movie.poster) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL(movie.poster)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                imageBitmap = bmp?.asImageBitmap()
                isLoading = false
            } catch (e: Exception) {
                e.printStackTrace()
                isLoading = false
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showDetails = !showDetails },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.25f)
        ),
        border = BorderStroke(2.dp, Color.White.copy(alpha = 0.35f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {
            // Poster
            androidx.compose.animation.AnimatedVisibility(
                visible = !showDetails && !isLoading,
                exit = slideOutVertically { fullWidth -> -fullWidth },
                modifier = Modifier.fillMaxWidth()
            ) {
                imageBitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "Movie Poster",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Loading animation
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .background(
                            Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            // Fallback text if no image found
            if (imageBitmap == null && !isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .background(
                            Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Text details
            androidx.compose.animation.AnimatedVisibility(
                visible = showDetails,
                enter = slideInVertically { fullWidth -> fullWidth },
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Year: ${movie.year}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                        Text(
                            text = "Released: ${movie.released}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                        Text(
                            text = "Genre: ${movie.genre}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                        Text(
                            text = "Director: ${movie.director}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                        Text(
                            text = "Writer: ${movie.writer}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                        Text(
                            text = "Actors: ${movie.actors}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                        Text(
                            text = "Plot: ${movie.plot}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMoviesByTitleScreen(onBack: () -> Unit) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var movieList by rememberSaveable { mutableStateOf<List<Movie>>(emptyList()) }
    val scope = rememberCoroutineScope()

    BackgroundWrapper {
        FadeInContent {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Image(
                    modifier = Modifier.size(150.dp),
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Logo"
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(25.dp))
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(38.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back button",
                        modifier = Modifier.size(32.dp),
                    )
                }

                Spacer(modifier = Modifier.height(85.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        modifier = Modifier.width(285.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            unfocusedBorderColor = Color.White,
                            focusedBorderColor = Color.White,
                            focusedTextColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Enter movie title keyword") },
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            if (searchQuery.isNotEmpty()) {
                                scope.launch {
                                    movieList = fetchMoviesByTitle(searchQuery)
                                }
                            }
                        },

                        modifier = Modifier
                            .size(55.dp)
                            .padding(0.dp)
                            .border(
                                2.dp,
                                Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(45.dp),
                            ),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search Button",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(movieList.size) { index ->
                        MovieItem(movie = movieList[index])
                    }
                }

            }
        }
    }
}

suspend fun fetchMoviesByTitle(searchQuery: String): List<Movie> {

    val apiKey = "42438be8"
    val urlString = "https://www.omdbapi.com/?s=$searchQuery&apikey=$apiKey"
    val url = URL(urlString)

    val connection: HttpURLConnection =
        withContext(Dispatchers.IO) {
            url.openConnection()
        } as HttpURLConnection

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
    val movies = mutableListOf<Movie>()

    // Check if the response is valid
    if (json.optString("Response") == "True") {
        val searchResults = json.optJSONArray("Search")
        if (searchResults != null) {
            for (i in 0 until searchResults.length()) {
                val movieJson = searchResults.getJSONObject(i)
                movies.add(
                    Movie(
                        title = movieJson.optString("Title"),
                        year = movieJson.optString("Year"),
                        id = movieJson.optString("imdbID"),
                        rated = movieJson.optString("imdbRating"),
                        released = movieJson.optString("Released"),
                        runtime = movieJson.optString("Runtime"),
                        genre = movieJson.optString("Genre"),
                        director = movieJson.optString("Director"),
                        writer = movieJson.optString("Writer"),
                        actors = movieJson.optString("Actors"),
                        plot = movieJson.optString("Plot"),
                        poster = movieJson.optString("Poster")
                    )
                )
            }
        }
    }

    return movies
}

@Composable
fun BackgroundWrapper(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        content()
    }
}

@Composable
fun FadeInContent(content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn()
    ) {
        content()
    }
}

