package com.example.listmovies.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SearchViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final String apiKey;
    // --- MODIFIED: Removed the 'language' variable ---

    // --- MODIFIED: Removed the 'language' parameter from the constructor ---
    public SearchViewModelFactory(Application application, String apiKey) {
        this.application = application;
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            // --- MODIFIED: Call the new constructor without 'language' ---
            return (T) new SearchViewModel(application, apiKey);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}