package com.example.cinemaroom

import androidx.compose.runtime.saveable.Saver
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Movies")
data class Movie (
    @PrimaryKey val id: String,
    val title: String,
    val year: String,
    val rated: String,
    val released: String,
    val runtime: String,
    val genre: String,
    val director: String,
    val writer: String,
    val actors: String,
    val plot: String
)
val MovieSaver = Saver<Movie, Map<String, String>>(
    save = { movie ->
        mapOf(
            "id" to movie.id,
            "title" to movie.title,
            "year" to movie.year,
            "rated" to movie.rated,
            "released" to movie.released,
            "runtime" to movie.runtime,
            "genre" to movie.genre,
            "director" to movie.director,
            "writer" to movie.writer,
            "actors" to movie.actors,
            "plot" to movie.plot
        )
    },
    restore = { map ->
        Movie(
            id = map["id"] ?: "",
            title = map["title"] ?: "",
            year = map["year"] ?: "",
            rated = map["rated"] ?: "",
            released = map["released"] ?: "",
            runtime = map["runtime"] ?: "",
            genre = map["genre"] ?: "",
            director = map["director"] ?: "",
            writer = map["writer"] ?: "",
            actors = map["actors"] ?: "",
            plot = map["plot"] ?: ""
        )
    }
)
