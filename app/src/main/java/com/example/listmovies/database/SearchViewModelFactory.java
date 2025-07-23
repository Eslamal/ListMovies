package com.example.listmovies.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SearchViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final String apiKey;
    private final String language;

    public SearchViewModelFactory(Application application, String apiKey, String language) {
        this.application = application;
        this.apiKey = apiKey;
        this.language = language;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(application, apiKey, language);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}