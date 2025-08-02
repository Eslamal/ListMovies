package com.example.listmovies.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.listmovies.api.ActorDetails;
import com.example.listmovies.api.CastMember;
import com.example.listmovies.api.TvShowDetails;
import com.example.listmovies.database.DetailRepository.WatchProvidersResult;
import java.util.List;

public class TvShowDetailViewModel extends AndroidViewModel {

    private final DetailRepository repository;
    private final String apiKey;
    private final String originalLanguage;

    public final LiveData<TvShowDetails> tvShowDetails;
    public final LiveData<String> trailerUrl;
    public final LiveData<List<CastMember>> tvShowCast;
    public final LiveData<WatchProvidersResult> watchProviders;

    // --- تمت إضافة هذه الأسطر ---
    public final LiveData<Boolean> isFavorite;
    public final LiveData<Boolean> isInWatchlist;

    private final MutableLiveData<Integer> actorIdTrigger = new MutableLiveData<>();
    public final LiveData<ActorDetails> actorDetails;


    public TvShowDetailViewModel(@NonNull Application application, int tvId, String apiKey, String originalLanguage) {
        super(application);
        this.repository = new DetailRepository(application);
        this.apiKey = apiKey;
        this.originalLanguage = originalLanguage;

        this.tvShowDetails = repository.getTvShowDetails(tvId, apiKey, originalLanguage);
        this.tvShowCast = repository.getTvShowCast(tvId, apiKey, originalLanguage);
        this.trailerUrl = repository.getTvShowTrailerUrl(tvId, apiKey);
        this.watchProviders = repository.getTvShowWatchProviders(tvId, apiKey);

        // --- تمت إضافة هذه الأسطر ---
        this.isFavorite = repository.isFavorite(tvId);
        this.isInWatchlist = repository.isMovieInWatchlist(tvId);
        this.actorDetails = Transformations.switchMap(actorIdTrigger, id ->
                repository.getActorDetails(id, this.apiKey)
        );
    }

    // --- تمت إضافة هذه الدوال ---
    public void toggleFavoriteStatus(FavoriteMovieEntity movie) {
        repository.toggleFavoriteStatus(movie);
    }

    public void toggleWatchlistStatus(FavoriteMovieEntity movie) {
        repository.toggleWatchlistStatus(movie);
    }

    public void fetchActorDetails(int actorId) {
        actorIdTrigger.setValue(actorId);
    }
}