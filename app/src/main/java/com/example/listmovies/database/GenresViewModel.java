package com.example.listmovies.database;

import android.app.Application;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.listmovies.api.Genre;
import com.example.listmovies.util.LocalHelper;

import java.util.List;

public class GenresViewModel extends AndroidViewModel {

    private final GenresRepository repository;
    private final MutableLiveData<List<Genre>> genres = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public GenresViewModel(@NonNull Application application) {
        super(application);
        repository = new GenresRepository(application);
        loadGenres();
    }

    public LiveData<List<Genre>> getGenres() {
        return genres;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    private void loadGenres() {
        isLoading.setValue(true);
        // استبدل "YOUR_API_KEY" بمفتاح الـ API الفعلي من strings.xml
        String apiKey = "6f02d05e6bdd3ccc3c5856f543ed736e";
        String currentLanguageCode = LocalHelper.getPersistedLanguage(getApplication());

        // 2. If the setting is "system", get the actual device language
        if (currentLanguageCode.equals("system")) {
            currentLanguageCode = Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
        }

        // 3. Use the dynamic language code for the API call
        repository.fetchGenres(apiKey, currentLanguageCode, new GenresRepository.OnGenresFetchedListener() {
            @Override
            public void onSuccess(List<Genre> genreList) {
                genres.setValue(genreList);
                isLoading.setValue(false);
            }

            @Override
            public void onFailure() {
                genres.setValue(null);
                isLoading.setValue(false);
            }
        });
    }
}