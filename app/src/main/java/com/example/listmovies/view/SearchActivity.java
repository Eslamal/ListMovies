package com.example.listmovies.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listmovies.R;
import com.example.listmovies.adapter.MovieAdapter;
import com.example.listmovies.database.SearchViewModel;
import com.example.listmovies.database.SearchViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

public class SearchActivity extends AppCompatActivity {

    private TextInputEditText searchEditText;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private ProgressBar progressBar;
    private View emptyStateLayout;
    private View errorStateLayout;
    private TextView errorTextView;
    private ChipGroup chipGroupCategories;

    private SearchViewModel searchViewModel;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        setupSearchListener();
        addCategoryChips();

        showEmptyState();
    }

    private void initializeViews() {
        searchEditText = findViewById(R.id.searchEditText);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        errorStateLayout = findViewById(R.id.errorStateLayout);
        errorTextView = findViewById(R.id.errorTextView);
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.search_movies);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(this);
        recyclerView.setAdapter(movieAdapter);
    }

    private void setupViewModel() {
        String apiKey = getString(R.string.api_key);
        String language = "en-US";
        SearchViewModelFactory factory = new SearchViewModelFactory(getApplication(), apiKey, language);
        searchViewModel = new ViewModelProvider(this, factory).get(SearchViewModel.class);

        searchViewModel.searchResults.observe(this, movies -> {
            progressBar.setVisibility(View.GONE);
            if (movies != null && !movies.isEmpty()) {
                movieAdapter.setMovies(movies);
                showContent();
            } else if (movies != null) {
                showNoSearchResults();
            }
        });
    }

    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.length() >= 2) {
                    showLoading();
                    searchRunnable = () -> searchViewModel.setSearchQuery(query);
                    searchHandler.postDelayed(searchRunnable, 500);
                } else {
                    movieAdapter.setMovies(null);
                    showEmptyState();
                }
            }
        });
    }

    private void addCategoryChips() {
        String[] categories = {"Action", "Comedy", "Drama", "Sci-Fi", "Horror", "Thriller", "Animation"};
        for (String category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category);

            // --- هذا هو التعديل الأساسي ---
            chip.setOnClickListener(v -> {
                String chipText = ((Chip) v).getText().toString();

                // 1. تحديث مربع البحث عشان المستخدم يشوف هو اختار إيه
                searchEditText.setText(chipText);

                // 2. إظهار الـ ProgressBar فورًا (هذا يحل المشكلة)
                showLoading();

                // 3. إلغاء أي بحث مؤجل من الكتابة
                searchHandler.removeCallbacks(searchRunnable);

                // 4. بدء البحث مباشرةً بدون تأخير
                searchViewModel.setSearchQuery(chipText);
            });
            chipGroupCategories.addView(chip);
        }
    }

    // --- دوال عرض الحالات المختلفة (تبقى كما هي) ---
    private void showLoading() { /* ... */ }
    private void showContent() { /* ... */ }
    private void showEmptyState() { /* ... */ }
    private void showNoSearchResults() { /* ... */ }
    private void showErrorState(String message) { /* ... */ }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}