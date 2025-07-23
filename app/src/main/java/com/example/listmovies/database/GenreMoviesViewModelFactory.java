package com.example.listmovies.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GenreMoviesViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final String apiKey;
    private final int genreId;

    public GenreMoviesViewModelFactory(Application application, String apiKey, int genreId) {
        this.application = application;
        this.apiKey = apiKey;
        this.genreId = genreId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GenreMoviesViewModel(application, apiKey, genreId);
    }
}