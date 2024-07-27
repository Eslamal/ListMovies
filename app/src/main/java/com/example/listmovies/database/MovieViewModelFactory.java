package com.example.listmovies.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MovieViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private String apiKey;
    private String category;

    public MovieViewModelFactory(Application application, String apiKey, String category) {
        this.application = application;
        this.apiKey = apiKey;
        this.category = category;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MovieViewModel.class)) {
            return (T) new MovieViewModel(application, apiKey, category);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

