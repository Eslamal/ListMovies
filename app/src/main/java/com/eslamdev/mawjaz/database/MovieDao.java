package com.eslamdev.mawjaz.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<MovieEntity> movies);

    @Query("SELECT * FROM movies WHERE category = :category")
    List<MovieEntity> getMoviesByCategory(String category);

    @Query("DELETE FROM movies WHERE category = :category")
    void deleteMoviesByCategory(String category);
}


