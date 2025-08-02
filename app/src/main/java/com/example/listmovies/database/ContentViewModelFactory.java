package com.example.listmovies.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ContentViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final String apiKey;
    private final String contentType;
    private final String category;
    private final String language;
    private final String countryCode; // <-- 1. ADD THIS VARIABLE

    // 2. MODIFY THE CONSTRUCTOR TO ACCEPT countryCode
    public ContentViewModelFactory(Application application, String apiKey, String contentType, String category, String language, String countryCode) {
        this.application = application;
        this.apiKey = apiKey;
        this.contentType = contentType;
        this.category = category;
        this.language = language;
        this.countryCode = countryCode; // <-- ASSIGN THE NEW VARIABLE
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ContentViewModel.class)) {
            // 3. PASS THE new countryCode variable to the ViewModel's constructor
            return (T) new ContentViewModel(application, apiKey, contentType, category, language, countryCode);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}