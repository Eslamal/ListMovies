package com.example.listmovies.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
@Database(entities = {MovieEntity.class, FavoriteMovieEntity.class, WatchlistMovieEntity.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract MovieDao movieDao();
    public abstract FavoriteMovieDao favoriteMovieDao();
    // --- 2. أضف الدالة الجديدة هنا ---
    public abstract WatchlistMovieDao watchlistMovieDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "movie_database")
                    // قمنا برفع الإصدار، لذا نحتاج إلى ترحيل البيانات أو حذفها
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}