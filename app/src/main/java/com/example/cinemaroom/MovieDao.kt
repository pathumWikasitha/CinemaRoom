package com.example.cinemaroom

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movies: Movie)

    @Query("SELECT * FROM movies where LOWER(actors) LIKE '%' || LOWER(:actorName) || '%'")
    suspend fun searchMoviesByActor(actorName: String): List<Movie>

    @Query("SELECT * FROM Movies WHERE title = :title LIMIT 1")
    suspend fun getMovieByTitle(title: String): Movie?
}