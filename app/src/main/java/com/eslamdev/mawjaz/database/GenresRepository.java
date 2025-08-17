package com.eslamdev.mawjaz.database;

import android.content.Context;
import androidx.annotation.NonNull;
import com.eslamdev.mawjaz.api.Genre;
import com.eslamdev.mawjaz.api.GenreListResponse;
import com.eslamdev.mawjaz.api.TMDbApi;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GenresRepository {
    private final TMDbApi api;

    public interface OnGenresFetchedListener {
        void onSuccess(List<Genre> genres);
        void onFailure();
    }

    public GenresRepository(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(TMDbApi.class);
    }

    public void fetchGenres(String apiKey, String language, OnGenresFetchedListener listener) {
        api.getGenres(apiKey, language).enqueue(new Callback<GenreListResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenreListResponse> call, @NonNull Response<GenreListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body().getGenres());
                } else {
                    listener.onFailure();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenreListResponse> call, @NonNull Throwable t) {
                listener.onFailure();
            }
        });
    }
}