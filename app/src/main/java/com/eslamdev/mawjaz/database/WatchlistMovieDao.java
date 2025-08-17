package com.eslamdev.mawjaz.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface WatchlistMovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWatchlistMovie(WatchlistMovieEntity movie);

    @Delete
    void deleteWatchlistMovie(WatchlistMovieEntity movie);

    @Query("SELECT * FROM watchlist_movies")
    List<WatchlistMovieEntity> getAllWatchlistMovies();

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_movies WHERE id = :movieId LIMIT 1)")
    boolean isMovieInWatchlist(int movieId);

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_movies WHERE id = :movieId LIMIT 1)")
    LiveData<Boolean> isMovieInWatchlistLiveData(int movieId);
}