package com.example.listmovies.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.listmovies.api.ActorDetails;
import com.example.listmovies.api.CastMember;
import com.example.listmovies.api.Provider;

import java.util.List;

public class DetailViewModel extends AndroidViewModel {

    private final DetailRepository repository;
    private final String apiKey;

    // LiveData الخاصة بتفاصيل الفيلم الرئيسية
    public final LiveData<Boolean> isFavorite;
    public final LiveData<String> trailerUrl;
    public final LiveData<List<CastMember>> movieCast;
    public final LiveData<Boolean> isInWatchlist;
    public final LiveData<DetailRepository.WatchProvidersResult> watchProviders;

    // LiveData ومتغير التشغيل (Trigger) الخاص بتفاصيل الممثل
    private final MutableLiveData<Integer> actorIdTrigger = new MutableLiveData<>();
    public final LiveData<ActorDetails> actorDetails; // **الخطوة 1: نعلن عنه هنا فقط**

    public DetailViewModel(@NonNull Application application, int movieId, String apiKey) {
        super(application);
        // **الخطوة 2: نقوم بتهيئة المتغيرات الأساسية أولاً**
        this.repository = new DetailRepository(application);
        this.apiKey = apiKey; // يتم الآن تهيئته قبل استخدامه

        // تهيئة الـ LiveData الخاصة بتفاصيل الفيلم
        this.isFavorite = repository.isFavorite(movieId);
        this.trailerUrl = repository.getTrailerUrl(movieId, apiKey);
        this.movieCast = repository.getMovieCast(movieId, apiKey);
        this.isInWatchlist = repository.isMovieInWatchlist(movieId);
        this.watchProviders = repository.getWatchProviders(movieId, apiKey);

        this.actorDetails = Transformations.switchMap(actorIdTrigger, id ->
                repository.getActorDetails(id, this.apiKey)
        );
    }

    public void toggleFavoriteStatus(FavoriteMovieEntity movie) {
        boolean currentlyIsFavorite = isFavorite.getValue() != null && isFavorite.getValue();
        if (currentlyIsFavorite) {
            repository.removeFromFavorites(movie);
        } else {
            repository.addToFavorites(movie);
        }
    }
    public void toggleWatchlistStatus(FavoriteMovieEntity movie) {
        repository.toggleWatchlistStatus(movie);
    }



    public void fetchActorDetails(int actorId) {
        actorIdTrigger.setValue(actorId);
    }
}