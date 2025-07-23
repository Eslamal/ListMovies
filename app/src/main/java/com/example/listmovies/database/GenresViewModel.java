package com.example.listmovies.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.listmovies.api.Genre;
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
        String language = "en-US";

        repository.fetchGenres(apiKey, language, new GenresRepository.OnGenresFetchedListener() {
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