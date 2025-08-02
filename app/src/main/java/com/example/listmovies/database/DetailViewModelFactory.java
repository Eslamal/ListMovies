package com.example.listmovies.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DetailViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final int movieId;
    private final String apiKey;
    // 1. أضف هذا المتغير
    private final String originalLanguage;

    // 2. عدّل الـ constructor ليقبل المتغير الجديد
    public DetailViewModelFactory(Application application, int movieId, String apiKey, String originalLanguage) {
        this.application = application;
        this.movieId = movieId;
        this.apiKey = apiKey;
        this.originalLanguage = originalLanguage; // قم بتعيين المتغير الجديد
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DetailViewModel.class)) {
            // 3. مرر المتغير الجديد إلى الـ ViewModel
            return (T) new DetailViewModel(application, movieId, apiKey, originalLanguage);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}