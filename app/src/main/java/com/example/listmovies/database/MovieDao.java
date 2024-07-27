package com.example.listmovies.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.listmovies.api.Movie;

import java.util.List;

@Dao
public interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<MovieEntity> movies);

    @Query("SELECT * FROM movies")
    List<MovieEntity> getAllMovies();

    @Query("SELECT timestamp FROM movies LIMIT 1")
    long getTimestamp();

    @Query("DELETE FROM movies")
    void deleteAllMovies();
}

