package com.example.listmovies.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listmovies.R;
import com.example.listmovies.adapter.MovieAdapter;
import com.example.listmovies.api.Movie;
import com.example.listmovies.database.AppDatabase;
import com.example.listmovies.database.FavoriteMovieDao;
import com.example.listmovies.database.FavoriteMovieEntity;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesActivity extends BaseActivity implements MovieAdapter.OnMovieActionListener {

    private RecyclerView favoritesRecyclerView;
    private TextView emptyFavoritesText;
    private MovieAdapter movieAdapter;
    // --- تمت إضافة هذه المتغيرات كمتغيرات عامة في الكلاس ---
    private FavoriteMovieDao favoriteMovieDao;
    private ExecutorService databaseExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        MaterialToolbar toolbar = findViewById(R.id.favorites_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.favorites));
        }

        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        emptyFavoritesText = findViewById(R.id.emptyFavoritesText);

        // --- تهيئة الـ DAO والـ Executor هنا مرة واحدة ---
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        favoriteMovieDao = db.favoriteMovieDao();
        databaseExecutor = Executors.newSingleThreadExecutor();

        movieAdapter = new MovieAdapter(this, true, R.layout.item_movie_grid);
        movieAdapter.setOnMovieActionListener(this);
        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        favoritesRecyclerView.setAdapter(movieAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteMovies();
    }

    private void loadFavoriteMovies() {
        databaseExecutor.execute(() -> {
            List<FavoriteMovieEntity> favoriteEntities = favoriteMovieDao.getAllFavoriteMovies();
            final List<Movie> moviesToDisplay = new ArrayList<>();
            for (FavoriteMovieEntity entity : favoriteEntities) {
                moviesToDisplay.add(new Movie(
                        entity.getId(), entity.getTitle(), entity.getVoteAverage(),
                        entity.getOverview(), entity.getPosterPath(), entity.getReleaseDate()
                ));
            }

            runOnUiThread(() -> {
                if (moviesToDisplay.isEmpty()) {
                    emptyFavoritesText.setVisibility(View.VISIBLE);
                    favoritesRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyFavoritesText.setVisibility(View.GONE);
                    favoritesRecyclerView.setVisibility(View.VISIBLE);
                    movieAdapter.setMovies(moviesToDisplay);
                }
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --- هذا هو التعديل الأساسي ---
    @Override
    public void onMovieRemovedFromFavorites(Movie movie) {
        // 1. إنشاء Entity للفيلم المراد حذفه
        FavoriteMovieEntity movieToDelete = new FavoriteMovieEntity(
                movie.getId(), movie.getTitle(), movie.getVoteAverage(),
                movie.getOverview(), movie.getPosterPath(), movie.getReleaseDate()
        );

        // 2. تنفيذ عملية الحذف في الخلفية
        databaseExecutor.execute(() -> {
            favoriteMovieDao.deleteFavoriteMovie(movieToDelete);
            // 3. بعد الحذف، نطلب إعادة تحميل القائمة على الواجهة الرئيسية
            runOnUiThread(this::loadFavoriteMovies);
        });

        // 4. إظهار رسالة للمستخدم فورًا
        Toast.makeText(this, movie.getTitle() + R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavoriteStatusChanged(Movie movie, boolean isFavorite) {
        // لا نحتاج لعمل أي شيء هنا
    }
}