package com.eslamdev.mawjaz.database;

import com.eslamdev.mawjaz.api.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieMapper {

    public static MovieEntity mapToEntity(Movie movie, String category) {
        return new MovieEntity(
                movie.getId(),
                movie.getTitle(),
                movie.getVoteAverage(),
                movie.getOverview(),
                movie.getPosterPath(),
                movie.getReleaseDate(),
                category
        );
    }

    public static List<MovieEntity> mapToEntityList(List<Movie> movies, String category) {
        List<MovieEntity> movieEntities = new ArrayList<>();
        for (Movie movie : movies) {
            movieEntities.add(mapToEntity(movie, category));
        }
        return movieEntities;
    }

    public static Movie mapToModel(MovieEntity movieEntity) {
        return new Movie(
                movieEntity.getId(),
                movieEntity.getTitle(),
                movieEntity.getVoteAverage(),
                movieEntity.getOverview(),
                movieEntity.getPosterPath(),
                movieEntity.getReleaseDate()
        );
    }

    public static List<Movie> mapToModelList(List<MovieEntity> movieEntities) {
        List<Movie> movies = new ArrayList<>();
        for (MovieEntity movieEntity : movieEntities) {
            movies.add(mapToModel(movieEntity));
        }
        return movies;
    }
}


