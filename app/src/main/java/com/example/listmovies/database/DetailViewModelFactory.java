package com.example.listmovies.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DetailViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final int movieId;
    private final String apiKey;

    public DetailViewModelFactory(Application application, int movieId, String apiKey) {
        this.application = application;
        this.movieId = movieId;
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DetailViewModel.class)) {
            return (T) new DetailViewModel(application, movieId, apiKey);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}