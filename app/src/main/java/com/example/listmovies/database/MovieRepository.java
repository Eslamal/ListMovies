package com.example.listmovies.database;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
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
    private final TMDbApi api;

    /**
     * واجهة Callback للتواصل بين الـ Repository والـ ViewModel.
     */
    public interface OnMoviesFetchedListener {
        void onSuccess(List<Movie> movies);
        void onFailure();
    }

    public MovieRepository(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(TMDbApi.class);
        // تم حذف كود قاعدة البيانات مؤقتًا للتركيز على التحميل التدريجي
    }

    /**
     * الدالة الجديدة لجلب الأفلام التي تقبل رقم الصفحة و Callback.
     * @param page رقم الصفحة المطلوب تحميلها.
     * @param listener الـ Callback الذي سيتم استدعاؤه عند اكتمال الطلب.
     */
    public void fetchMovies(String apiKey, String category, String language, int page, OnMoviesFetchedListener listener) {
        Call<MovieResponse> call;
        if ("popular".equals(category)) {
            // استدعاء دالة الـ API الجديدة التي تقبل رقم الصفحة
            call = api.getPopularMovies(apiKey, language, page);
        } else if ("top_rated".equals(category)) {
            // استدعاء دالة الـ API الجديدة التي تقبل رقم الصفحة
            call = api.getTopRatedMovies(apiKey, language, page);
        } else {
            Log.e(TAG, "Unknown movie category: " + category);
            listener.onFailure();
            return;
        }

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                    // في حالة النجاح، نرسل قائمة الأفلام الجديدة إلى الـ ViewModel
                    listener.onSuccess(response.body().getResults());
                } else {
                    Log.e(TAG, "API response was not successful: " + response.message());
                    listener.onFailure();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network request failed: " + t.getMessage());
                listener.onFailure();
            }
        });
    }

    /**
     * دالة البحث تبقى كما هي في الوقت الحالي.
     */
    public LiveData<List<Movie>> searchMovies(String apiKey, String query, String language) {
        MutableLiveData<List<Movie>> searchResultsLiveData = new MutableLiveData<>();
        api.searchMovies(apiKey, query, language).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    searchResultsLiveData.postValue(response.body().getResults());
                } else {
                    searchResultsLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                searchResultsLiveData.postValue(null);
            }
        });
        return searchResultsLiveData;
    }


    public void fetchMoviesByGenre(String apiKey, String language, int genreId, int page, OnMoviesFetchedListener listener) {
        api.discoverMoviesByGenre(apiKey, language, genreId, page).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body().getResults());
                } else {
                    listener.onFailure();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                listener.onFailure();
            }
        });
    }
}