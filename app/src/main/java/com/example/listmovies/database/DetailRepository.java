package com.example.listmovies.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.listmovies.R;
import com.example.listmovies.api.ActorDetails;
import com.example.listmovies.api.CastMember;
import com.example.listmovies.api.CountrySpecificProviders;
import com.example.listmovies.api.CreditsResponse;
import com.example.listmovies.api.Provider;
import com.example.listmovies.api.TMDbApi;
import com.example.listmovies.api.Video;
import com.example.listmovies.api.VideoResponse;
import com.example.listmovies.api.WatchProviderResults;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class DetailRepository {
    private final FavoriteMovieDao favoriteMovieDao;
    private final WatchlistMovieDao watchlistMovieDao;
    private final TMDbApi tmDbApi;
    private final ExecutorService databaseExecutor;
    private final Application application;

    public static class WatchProvidersResult {
        private final List<Provider> providers;
        private final String link;

        public WatchProvidersResult(List<Provider> providers, String link) {
            this.providers = providers;
            this.link = link;
        }

        public List<Provider> getProviders() { return providers; }
        public String getLink() { return link; }
    }


    public DetailRepository(Application application) {
        this.application = application;
        AppDatabase db = AppDatabase.getInstance(application);
        this.favoriteMovieDao = db.favoriteMovieDao();
        this.watchlistMovieDao = db.watchlistMovieDao();
        this.databaseExecutor = Executors.newSingleThreadExecutor();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.tmDbApi = retrofit.create(TMDbApi.class);
    }

    public LiveData<Boolean> isFavorite(int movieId) {
        return favoriteMovieDao.isFavoriteLiveData(movieId);
    }

    public LiveData<String> getTrailerUrl(int movieId, String apiKey) {
        MutableLiveData<String> trailerUrlLiveData = new MutableLiveData<>();
        tmDbApi.getMovieVideos(movieId, apiKey).enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(@NonNull Call<VideoResponse> call, @NonNull Response<VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String youtubeKey = findBestTrailerKey(response.body().getResults());
                    if (youtubeKey != null) {
                        // الرابط الصحيح لتشغيل الفيديو في WebView
                        trailerUrlLiveData.postValue("https://www.youtube.com/embed/" + youtubeKey);
                    } else {
                        trailerUrlLiveData.postValue(null);
                    }
                } else {
                    trailerUrlLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<VideoResponse> call, @NonNull Throwable t) {
                trailerUrlLiveData.postValue(null);
            }
        });
        return trailerUrlLiveData;
    }

    private String findBestTrailerKey(List<Video> videos) {
        String key = null;
        for (Video video : videos) {
            if ("Trailer".equalsIgnoreCase(video.getType()) && "YouTube".equalsIgnoreCase(video.getSite())) {
                return video.getKey(); // أفضل اختيار هو التريلر الرسمي
            }
            if (key == null && "Teaser".equalsIgnoreCase(video.getType()) && "YouTube".equalsIgnoreCase(video.getSite())) {
                key = video.getKey(); // اختيار بديل لو مفيش تريلر
            }
        }
        return key;
    }

    public void addToFavorites(FavoriteMovieEntity movie) {
        databaseExecutor.execute(() -> favoriteMovieDao.insertFavoriteMovie(movie));
    }

    public void removeFromFavorites(FavoriteMovieEntity movie) {
        databaseExecutor.execute(() -> favoriteMovieDao.deleteFavoriteMovie(movie));
    }


    public LiveData<List<CastMember>> getMovieCast(int movieId, String apiKey) {
        MutableLiveData<List<CastMember>> castLiveData = new MutableLiveData<>();
        tmDbApi.getMovieCredits(movieId, apiKey).enqueue(new Callback<CreditsResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreditsResponse> call, @NonNull Response<CreditsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    castLiveData.postValue(response.body().getCast());
                } else {
                    castLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreditsResponse> call, @NonNull Throwable t) {
                castLiveData.postValue(null);
            }
        });
        return castLiveData;
    }

    public LiveData<ActorDetails> getActorDetails(int actorId, String apiKey) {
        MutableLiveData<ActorDetails> actorDetailsLiveData = new MutableLiveData<>();
        tmDbApi.getPersonDetails(actorId, apiKey).enqueue(new Callback<ActorDetails>() {
            @Override
            public void onResponse(@NonNull Call<ActorDetails> call, @NonNull Response<ActorDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    actorDetailsLiveData.postValue(response.body());
                } else {
                    actorDetailsLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ActorDetails> call, @NonNull Throwable t) {
                actorDetailsLiveData.postValue(null);
            }
        });
        return actorDetailsLiveData;
    }

    public LiveData<Boolean> isMovieInWatchlist(int movieId) {
        return watchlistMovieDao.isMovieInWatchlistLiveData(movieId);
    }

    public void toggleWatchlistStatus(FavoriteMovieEntity movie) {
        // نقوم بتحويل الـ Entity للنوع الصحيح
        WatchlistMovieEntity watchlistMovie = new WatchlistMovieEntity(
                movie.getId(), movie.getTitle(), movie.getVoteAverage(),
                movie.getOverview(), movie.getPosterPath(), movie.getReleaseDate()
        );
        databaseExecutor.execute(() -> {
            if (watchlistMovieDao.isMovieInWatchlist(movie.getId())) {
                watchlistMovieDao.deleteWatchlistMovie(watchlistMovie);
            } else {
                watchlistMovieDao.insertWatchlistMovie(watchlistMovie);
            }
        });
    }


    public LiveData<WatchProvidersResult> getWatchProviders(int movieId, String apiKey) {
        MutableLiveData<WatchProvidersResult> resultLiveData = new MutableLiveData<>();

        tmDbApi.getWatchProviders(movieId, apiKey).enqueue(new Callback<WatchProviderResults>() {
            @Override
            public void onResponse(@NonNull Call<WatchProviderResults> call, @NonNull Response<WatchProviderResults> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                    // سنقوم بجلب المنصات المتاحة في مصر (EG) كمثال
                    String countryCode = Locale.getDefault().getCountry(); // مثلًا: "EG" لمصر، "SA" للسعودية، "US" لأمريكا
                    CountrySpecificProviders providersForCountry = response.body().getResults().get(countryCode);

                    if (providersForCountry != null) {
                        List<Provider> flatrateProviders = providersForCountry.getFlatrate() != null ? providersForCountry.getFlatrate() : new ArrayList<>();
                        resultLiveData.postValue(new WatchProvidersResult(flatrateProviders, providersForCountry.getLink()));
                    } else {
                        // إذا لم نجد بيانات للبلد المحدد، نرسل نتيجة فارغة
                        resultLiveData.postValue(new WatchProvidersResult(new ArrayList<>(), null));
                    }
                } else {
                    resultLiveData.postValue(null); // في حالة حدوث خطأ
                }
            }

            @Override
            public void onFailure(@NonNull Call<WatchProviderResults> call, @NonNull Throwable t) {
                resultLiveData.postValue(null); // في حالة فشل الاتصال
            }
        });

        return resultLiveData;
    }
}

