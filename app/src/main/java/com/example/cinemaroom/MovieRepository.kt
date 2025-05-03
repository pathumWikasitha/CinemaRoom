package com.example.cinemaroom

class MovieRepository {
    fun getHardcodedMovies(): List<Movie> {
        return listOf(
            Movie(
                id = "tt0111161",
                title = "The Shawshank Redemption",
                year = "1994",
                rated = "R",
                released = "14 Oct 1994",
                runtime = "142 min",
                genre = "Drama",
                director = "Frank Darabont",
                writer = "Stephen King, Frank Darabont",
                actors = "Tim Robbins, Morgan Freeman, Bob Gunton",
                plot = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                poster = "https://m.media-amazon.com/images/M/MV5BMDAyY2FhYjctNDc5OS00MDNlLThiMGUtY2UxYWVkNGY2ZjljXkEyXkFqcGc@._V1_SX300.jpg"
            ),
            Movie(
                id = "tt2313197",
                title = "Batman: The Dark Knight Returns, Part 1",
                year = "2012",
                rated = "PG-13",
                released = "25 Sep 2012",
                runtime = "76 min",
                genre = "Animation, Action, Crime, Drama, Thriller",
                director = "Jay Oliva",
                writer = "Bob Kane, Frank Miller, Klaus Janson, Bob Goodman",
                actors = "Peter Weller, Ariel Winter, David Selby, Wade Williams",
                plot = "Batman has not been seen for ten years. A new breed of criminal ravages Gotham City, forcing 55-year-old Bruce Wayne back into the cape and cowl.",
                poster = "https://m.media-amazon.com/images/M/MV5BMzIxMDkxNDM2M15BMl5BanBnXkFtZTcwMDA5ODY1OQ@@._V1_SX300.jpg"
            ),
            Movie(
                id = "tt0167260",
                title = "The Lord of the Rings: The Return of the King",
                year = "2003",
                rated = "PG-13",
                released = "17 Dec 2003",
                runtime = "201 min",
                genre = "Action, Adventure, Drama",
                director = "Peter Jackson",
                writer = "J.R.R. Tolkien, Fran Walsh, Philippa Boyens",
                actors = "Elijah Wood, Viggo Mortensen, Ian McKellen",
                plot = "Gandalf and Aragorn lead the World of Men against Sauron's army to draw his gaze from Frodo and Sam as they approach Mount Doom with the One Ring.",
                poster = "https://m.media-amazon.com/images/M/MV5BMTZkMjBjNWMtZGI5OC00MGU0LTk4ZTItODg2NWM3NTVmNWQ4XkEyXkFqcGc@._V1_SX300.jpg"
            ),
            Movie(
                id = "tt1375666",
                title = "Inception",
                year = "2010",
                rated = "PG-13",
                released = "16 Jul 2010",
                runtime = "148 min",
                genre = "Action, Adventure, Sci-Fi",
                director = "Christopher Nolan",
                writer = "Christopher Nolan",
                actors = "Leonardo DiCaprio, Joseph Gordon-Levitt, Elliot Page",
                plot = "A thief who steals corporate secrets through dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
                poster = "https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_SX300.jpg"
            ),
            Movie(
                id = "tt0133093",
                title = "The Matrix",
                year = "1999",
                rated = "R",
                released = "31 Mar 1999",
                runtime = "136 min",
                genre = "Action, Sci-Fi",
                director = "Lana Wachowski, Lilly Wachowski",
                writer = "Lilly Wachowski, Lana Wachowski",
                actors = "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss",
                plot = "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth: life as he knows it is an elaborate deception.",
                poster = "https://m.media-amazon.com/images/M/MV5BN2NmN2VhMTQtMDNiOS00NDlhLTliMjgtODE2ZTY0ODQyNDRhXkEyXkFqcGc@._V1_SX300.jpg"
            )
        )
    }
}