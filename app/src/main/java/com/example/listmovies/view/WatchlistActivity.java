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
import com.example.listmovies.database.WatchlistMovieDao;
import com.example.listmovies.database.WatchlistMovieEntity;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WatchlistActivity extends BaseActivity implements MovieAdapter.OnMovieActionListener {

    private RecyclerView watchlistRecyclerView;
    private TextView emptyWatchlistText;
    private MovieAdapter movieAdapter;
    // --- تمت إضافة هذه المتغيرات كمتغيرات عامة في الكلاس ---
    private WatchlistMovieDao watchlistMovieDao;
    private ExecutorService databaseExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        MaterialToolbar toolbar = findViewById(R.id.watchlist_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle("My Watchlist"); // يمكنك تغيير العنوان هنا
        }

        watchlistRecyclerView = findViewById(R.id.watchlistRecyclerView);
        emptyWatchlistText = findViewById(R.id.emptyWatchlistText);

        // --- تهيئة الـ DAO والـ Executor هنا مرة واحدة ---
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        watchlistMovieDao = db.watchlistMovieDao();
        databaseExecutor = Executors.newSingleThreadExecutor();

        movieAdapter = new MovieAdapter(this, true, R.layout.item_movie_grid);
        movieAdapter.setOnMovieActionListener(this);
        watchlistRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        watchlistRecyclerView.setAdapter(movieAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWatchlistMovies();
    }

    private void loadWatchlistMovies() {
        databaseExecutor.execute(() -> {
            List<WatchlistMovieEntity> watchlistEntities = watchlistMovieDao.getAllWatchlistMovies();
            final List<Movie> moviesToDisplay = new ArrayList<>();
            for (WatchlistMovieEntity entity : watchlistEntities) {
                moviesToDisplay.add(new Movie(
                        entity.getId(), entity.getTitle(), entity.getVoteAverage(),
                        entity.getOverview(), entity.getPosterPath(), entity.getReleaseDate()
                ));
            }

            runOnUiThread(() -> {
                if (moviesToDisplay.isEmpty()) {
                    emptyWatchlistText.setVisibility(View.VISIBLE);
                    watchlistRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyWatchlistText.setVisibility(View.GONE);
                    watchlistRecyclerView.setVisibility(View.VISIBLE);
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
        WatchlistMovieEntity movieToDelete = new WatchlistMovieEntity(
                movie.getId(), movie.getTitle(), movie.getVoteAverage(),
                movie.getOverview(), movie.getPosterPath(), movie.getReleaseDate()
        );

        // 2. تنفيذ عملية الحذف في الخلفية
        databaseExecutor.execute(() -> {
            watchlistMovieDao.deleteWatchlistMovie(movieToDelete);
            // 3. بعد الحذف، نطلب إعادة تحميل القائمة على الواجهة الرئيسية
            runOnUiThread(this::loadWatchlistMovies);
        });

        // 4. إظهار رسالة للمستخدم فورًا
        Toast.makeText(this, movie.getTitle() + " removed from watchlist", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavoriteStatusChanged(Movie movie, boolean isFavorite) {
        // لا نحتاج لعمل أي شيء هنا
    }
}