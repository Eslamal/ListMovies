package com.example.listmovies.database;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.listmovies.api.Movie;
import com.example.listmovies.api.MovieResponse;
import com.example.listmovies.api.TMDbApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieRepository {
    private static final String TAG = "MovieRepository";
    private static final long CACHE_EXPIRY = 4 * 60 * 60 * 1000;

    private final TMDbApi api;
    private final MovieDao movieDao;

    public MovieRepository(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(TMDbApi.class);
        movieDao = AppDatabase.getInstance(context).movieDao();
    }

    public LiveData<List<Movie>> getMovies(String apiKey, String category) {
        MutableLiveData<List<Movie>> moviesLiveData = new MutableLiveData<>();

        new Thread(() -> {
            long lastUpdated = movieDao.getTimestamp();
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastUpdated < CACHE_EXPIRY) {

                List<MovieEntity> movieEntities = movieDao.getAllMovies();
                moviesLiveData.postValue(MovieMapper.mapToModelList(movieEntities));
            } else {
                Call<MovieResponse> call;
                if (category.equals("popular")) {
                    call = api.getPopularMovies(apiKey);
                } else {
                    call = api.getTopRatedMovies(apiKey);
                }

                call.enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            moviesLiveData.postValue(movies);


                            new Thread(() -> {
                                movieDao.deleteAllMovies();
                                movieDao.insertMovies(MovieMapper.mapToEntityList(movies));
                            }).start();
                        } else {
                            Log.e(TAG, "Response not successful: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Log.e(TAG, "Network request failed: " + t.getMessage());
                    }
                });
            }
        }).start();

        return moviesLiveData;
    }

}

