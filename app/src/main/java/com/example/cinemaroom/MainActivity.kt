package com.example.cinemaroom

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.cinemaroom.ui.theme.CinemaRoomTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    BackgroundWrapper {
        FadeInContent {
            AppLogo(isPortrait)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(if (isPortrait) 330.dp else 80.dp))
                val context = LocalContext.current
                Button(
                    onClick = {
                        onAddMoviesClicked()
                        Toast.makeText(context, "Movies added to Database", Toast.LENGTH_SHORT)
                            .show()
                    }, modifier = Modifier
                        .width(if (isPortrait) 220.dp else 218.dp)
                        .height(if (isPortrait) 55.dp else 48.dp)
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

                Spacer(modifier = Modifier.height(if (isPortrait) 40.dp else 18.dp))

                Button(
                    onClick = onSearchMoviesClicked, modifier = Modifier
                        .width(if (isPortrait) 220.dp else 218.dp)
                        .height(if (isPortrait) 55.dp else 48.dp)
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

                Spacer(modifier = Modifier.height(if (isPortrait) 40.dp else 18.dp))

                Button(
                    onClick = onSearchActorsClicked, modifier = Modifier
                        .width(if (isPortrait) 220.dp else 218.dp)
                        .height(if (isPortrait) 55.dp else 48.dp)
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

                Spacer(modifier = Modifier.height(if (isPortrait) 40.dp else 18.dp))

                Button(
                    onClick = onSearchByTitleClicked, modifier = Modifier
                        .width(if (isPortrait) 220.dp else 218.dp)
                        .height(if (isPortrait) 55.dp else 48.dp)
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
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.height(if (isPortrait) 40.dp else 18.dp))
            }
        }
    }
}

@Composable
fun AppLogo(isPortrait: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isPortrait) 50.dp else 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            modifier = Modifier.size(size = if (isPortrait) 150.dp else 120.dp),
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo"
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMoviesScreen(onBack: () -> Unit, movieDao: MovieDao) {
    var movieTitle by rememberSaveable { mutableStateOf("") }
    var movieInfo by rememberSaveable { mutableStateOf<List<Movie>>(emptyList()) }

    // Create a CoroutineScope bound
    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    BackgroundWrapper {
        FadeInContent {
            AppLogo(isPortrait)
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


                Spacer(modifier = Modifier.height(if (isPortrait) 90.dp else 15.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        modifier = Modifier.width(if (isPortrait) 250.dp else 650.dp),
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
                    val context = LocalContext.current
                    Button(

                        onClick = {
                            if (movieTitle.isNotBlank()) {
                                scope.launch {
                                    movieInfo = fetchMovie(movieTitle)
                                    if (movieInfo.isEmpty()) {
                                        Toast.makeText(
                                            context,
                                            "$movieTitle Movie Not Found",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .height(60.dp)
                            .border(
                                2.dp,
                                Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(35.dp)
                            )
                            .padding(0.dp),
                        shape = RoundedCornerShape(35.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        )
                    ) {
                        Text("Retrieve")
                    }
                }

                Spacer(modifier = Modifier.height(if (isPortrait) 15.dp else 10.dp))

                LazyColumn {
                    items(movieInfo.size) { movie ->
                        MovieItem(movie = movieInfo[movie], isPortrait = isPortrait)
                        val context = LocalContext.current
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(if (isPortrait) 20.dp else 10.dp))

                            Button(
                                onClick = {
                                    movieInfo.let {
                                        scope.launch {
                                            movieDao.insertMovies(it)
                                            Toast.makeText(
                                                context,
                                                "Movies added to DB",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
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
                                    containerColor = Color.White.copy(alpha = 0.15f)
                                )
                            ) {
                                Text("Save to DB")
                            }
                        }
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
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    BackgroundWrapper {
        FadeInContent {
            AppLogo(isPortrait)
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

                Spacer(modifier = Modifier.height(if (isPortrait) 90.dp else 20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        modifier = Modifier.width(if (isPortrait) 250.dp else 650.dp),
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
                    val context = LocalContext.current
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                movieList = movieDao.searchMoviesByActor(searchedActor)
                                if (movieList.isEmpty()) {
                                    Toast.makeText(
                                        context,
                                        "$searchedActor Actor Not Found",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
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
                            contentDescription = "Search",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(if (isPortrait) 15.dp else 10.dp))

                LazyColumn {
                    items(movieList.size) { movie ->
                        MovieItem(movie = movieList[movie],isPortrait)
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, isPortrait: Boolean) {
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
        if (isPortrait) {
            // PORTRAIT MODE (Vertical Layout)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                PosterContent(imageBitmap, isLoading, showDetails, movie)
            }
        } else {
            // LANDSCAPE MODE (Horizontal Layout)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                // Left: Poster
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap!!,
                            contentDescription = "Movie Poster",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Right: Details
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                ) {
                    MovieDetails(movie)
                }
            }
        }
    }

}
@Composable
fun PosterContent(
    imageBitmap: ImageBitmap?,
    isLoading: Boolean,
    showDetails: Boolean,
    movie: Movie
) {
   AnimatedVisibility(
        visible = !showDetails && !isLoading,
        exit = slideOutVertically { fullWidth -> -fullWidth },
        modifier = Modifier.fillMaxSize()
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

    // Loading
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

    // Details
    AnimatedVisibility(
        visible = showDetails,
        enter = slideInVertically { it },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(12.dp)
        ) {
            MovieDetails(movie)
        }
    }
}

@Composable
fun MovieDetails(movie: Movie) {
    Column(modifier = Modifier.padding(12.dp)) {
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        InfoRow("Year: ", movie.year)
        InfoRow("Released: ", movie.released)
        InfoRow("Genre: ", movie.genre)
        InfoRow("Director: ", movie.director)
        InfoRow("Writer: ", movie.writer)
        InfoRow("Actors: ", movie.actors)
        InfoRow("Plot: ", movie.plot)
    }
}



@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
        )
    }
    Spacer(modifier = Modifier.height(2.dp))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMoviesByTitleScreen(onBack: () -> Unit) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var movieList by rememberSaveable { mutableStateOf<List<Movie>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    BackgroundWrapper {
        FadeInContent {
            AppLogo(isPortrait)
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

                Spacer(modifier = Modifier.height(if (isPortrait) 90.dp else 15.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        modifier = Modifier.width(if (isPortrait) 290.dp else 650.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            unfocusedBorderColor = Color.White,
                            focusedBorderColor = Color.White,
                            focusedTextColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(25.dp),
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Enter movie title keyword") },
                        singleLine = true
                    )
                    val context = LocalContext.current
                    IconButton(
                        onClick = {
                            if (searchQuery.isNotEmpty()) {
                                scope.launch {
                                    movieList = fetchMoviesByTitle(searchQuery)
                                    if (movieList.isEmpty()) {
                                        Toast.makeText(
                                            context,
                                            "$searchQuery Movie Not Found",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
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

                Spacer(modifier = Modifier.height(if (isPortrait) 15.dp else 10.dp))

                LazyColumn {
                    items(movieList.size) { index ->
                        MovieItem(movie = movieList[index], isPortrait = isPortrait)
                    }
                }

            }
        }
    }
}

suspend fun fetchMoviesByTitle(searchQuery: String): List<Movie> = coroutineScope {
    val apiKey = "42438be8"
    val searchUrl = "https://www.omdbapi.com/?s=$searchQuery&apikey=$apiKey"
    val movies = mutableListOf<Deferred<Movie?>>()

    val searchJson = withContext(Dispatchers.IO) {
        val url = URL(searchUrl)
        val connection = url.openConnection() as HttpURLConnection
        val response = connection.inputStream.bufferedReader().readText()
        JSONObject(response)
    }

    if (searchJson.optString("Response") == "True") {
        val searchResults = searchJson.optJSONArray("Search")
        if (searchResults != null) {
            for (i in 0 until searchResults.length()) {
                val movieSummary = searchResults.getJSONObject(i)
                val imdbID = movieSummary.optString("imdbID")

                // Fetch full details in parallel
                val deferred = async(Dispatchers.IO) {
                    try {
                        val detailUrl = "https://www.omdbapi.com/?i=$imdbID&apikey=$apiKey"
                        val detailConnection = URL(detailUrl).openConnection() as HttpURLConnection
                        val detailResponse =
                            detailConnection.inputStream.bufferedReader().readText()
                        val movieJson = JSONObject(detailResponse)

                        Movie(
                            title = movieJson.optString("Title"),
                            year = movieJson.optString("Year"),
                            id = movieJson.optString("imdbID"),
                            rated = movieJson.optString("Rated"),
                            released = movieJson.optString("Released"),
                            runtime = movieJson.optString("Runtime"),
                            genre = movieJson.optString("Genre"),
                            director = movieJson.optString("Director"),
                            writer = movieJson.optString("Writer"),
                            actors = movieJson.optString("Actors"),
                            plot = movieJson.optString("Plot"),
                            poster = movieJson.optString("Poster")
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }

                movies.add(deferred)
            }
        }
    }

    movies.awaitAll().filterNotNull()
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

