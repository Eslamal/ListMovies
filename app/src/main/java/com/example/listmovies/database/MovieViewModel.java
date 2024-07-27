package com.example.listmovies.database;


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.listmovies.api.Movie;

import java.util.List;


public class MovieViewModel extends AndroidViewModel {
    private MovieRepository movieRepository;
    private LiveData<List<Movie>> movies;

    public MovieViewModel(@NonNull Application application, String apiKey, String category) {
        super(application);
        movieRepository = new MovieRepository(application);
        movies = movieRepository.getMovies(apiKey, category);
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
}
