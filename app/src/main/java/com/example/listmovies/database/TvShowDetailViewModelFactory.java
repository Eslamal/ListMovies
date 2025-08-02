package com.example.listmovies.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TvShowDetailViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final int tvId;
    private final String apiKey;
    private final String originalLanguage;

    public TvShowDetailViewModelFactory(Application application, int tvId, String apiKey, String originalLanguage) {
        this.application = application;
        this.tvId = tvId;
        this.apiKey = apiKey;
        this.originalLanguage = originalLanguage;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TvShowDetailViewModel.class)) {
            // Pass the new parameter to the ViewModel
            return (T) new TvShowDetailViewModel(application, tvId, apiKey, originalLanguage);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}