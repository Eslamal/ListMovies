package com.example.listmovies.database;

import com.example.listmovies.api.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieMapper {

    public static MovieEntity mapToEntity(Movie movie) {
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setTitle(movie.getTitle());
        movieEntity.setVote_average(movie.getVoteAverage());
        movieEntity.setOverview(movie.getOverview());
        movieEntity.setPoster_path(movie.getPosterPath());
        movieEntity.setTimestamp(System.currentTimeMillis());
        return movieEntity;
    }

    public static List<MovieEntity> mapToEntityList(List<Movie> movies) {
        List<MovieEntity> movieEntities = new ArrayList<>();
        for (Movie movie : movies) {
            movieEntities.add(mapToEntity(movie));
        }
        return movieEntities;
    }

    public static Movie mapToModel(MovieEntity movieEntity) {
        return new Movie(
                movieEntity.getTitle(),
                movieEntity.getVote_average(),
                movieEntity.getOverview(),
                movieEntity.getPoster_path()
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

