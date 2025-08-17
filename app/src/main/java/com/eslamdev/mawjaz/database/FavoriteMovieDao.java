package com.eslamdev.mawjaz.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteMovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // إذا كان الفيلم موجودًا، قم باستبداله (مفيد عند تحديث بيانات الفيلم)
    void insertFavoriteMovie(FavoriteMovieEntity movie);

    @Delete
    void deleteFavoriteMovie(FavoriteMovieEntity movie);

    @Query("SELECT * FROM favorite_movies") // جلب جميع الأفلام المفضلة
    List<FavoriteMovieEntity> getAllFavoriteMovies();

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId LIMIT 1)") // التحقق مما إذا كان الفيلم مفضلاً
    boolean isMovieFavorite(int movieId);

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId LIMIT 1)")
    LiveData<Boolean> isFavoriteLiveData(int movieId);

    @Query("SELECT * FROM favorite_movies WHERE id = :movieId LIMIT 1") // جلب فيلم مفضل بواسطة الـ ID
    FavoriteMovieEntity getFavoriteMovieById(int movieId);
}