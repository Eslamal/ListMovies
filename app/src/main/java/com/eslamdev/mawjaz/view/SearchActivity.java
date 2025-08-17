package com.eslamdev.mawjaz.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.eslamdev.mawjaz.R;
import com.eslamdev.mawjaz.adapter.ContentAdapter;
import com.eslamdev.mawjaz.database.SearchViewModel;
import com.eslamdev.mawjaz.database.SearchViewModelFactory;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashSet;
import java.util.Set;

public class SearchActivity extends BaseActivity {

    // --- UI Components ---
    private TextInputEditText searchEditText;
    private RecyclerView recyclerView;
    private ContentAdapter contentAdapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    private View emptyStateLayout;
    private TextView resultsTitleTextView;
    private ChipGroup chipGroupGenres;
    private ChipGroup chipGroupRecent;
    private MaterialButton clearHistoryButton;
    private View recentSearchesHeader;


    // --- Logic Components ---
    private SearchViewModel searchViewModel;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    // --- Constants for Recent Searches ---
    private static final String PREFS_NAME = "SearchHistoryPrefs";
    private static final String KEY_HISTORY = "history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        setupSearchListener();
        setupStaticGenreChips();
        loadAndDisplayRecentSearches();
        setupClearHistoryListener();
    }

    private void initializeViews() {
        searchEditText = findViewById(R.id.searchEditText);
        recyclerView = findViewById(R.id.recyclerView);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        resultsTitleTextView = findViewById(R.id.text_view_results_title);
        chipGroupGenres = findViewById(R.id.chip_group_genres);
        chipGroupRecent = findViewById(R.id.chip_group_recent);
        clearHistoryButton = findViewById(R.id.button_clear_history);
        // --- THIS LINE WAS MISSING FROM YOUR FILE ---
        recentSearchesHeader = findViewById(R.id.recent_searches_header);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.search);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contentAdapter = new ContentAdapter(this);
        recyclerView.setAdapter(contentAdapter);
    }

    private void setupViewModel() {
        String apiKey = getString(R.string.api_key);
        SearchViewModelFactory factory = new SearchViewModelFactory(getApplication(), apiKey);
        searchViewModel = new ViewModelProvider(this, factory).get(SearchViewModel.class);

        searchViewModel.results.observe(this, items -> {
            boolean isSearchActive = searchEditText.getText() != null && searchEditText.getText().length() >= 2;

            if (items != null && !items.isEmpty()) {
                contentAdapter.setItems(items);
                showContent();
                resultsTitleTextView.setText(isSearchActive ? R.string.search_results_title : R.string.trending_now);
                resultsTitleTextView.setVisibility(View.VISIBLE);
            } else {
                // If the search was active but returned no items, show empty state
                if (isSearchActive) {
                    showEmptyState();
                }
            }
        });

        // Trigger the initial load for "Trending"
        searchViewModel.loadInitialData();
    }

    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
            }
            @Override public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                searchRunnable = () -> {
                    searchViewModel.setSearchQuery(query);
                    if (query.length() >= 2) {
                        saveSearchQuery(query);
                    }
                };
                if (query.length() >= 2) {
                    showLoading();
                    searchHandler.postDelayed(searchRunnable, 500);
                } else {
                    searchViewModel.loadInitialData();
                }
            }
        });
    }

    private void saveSearchQuery(String query) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> history = new HashSet<>(prefs.getStringSet(KEY_HISTORY, new HashSet<>()));
        history.add(query);
        prefs.edit().putStringSet(KEY_HISTORY, history).apply();
        loadAndDisplayRecentSearches();
    }

    private void loadAndDisplayRecentSearches() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> history = prefs.getStringSet(KEY_HISTORY, new HashSet<>());

        if (history.isEmpty()) {
            recentSearchesHeader.setVisibility(View.GONE);
            chipGroupRecent.setVisibility(View.GONE);
        } else {
            recentSearchesHeader.setVisibility(View.VISIBLE);
            chipGroupRecent.setVisibility(View.VISIBLE);
        }

        chipGroupRecent.removeAllViews();
        for (String query : history) {
            Chip chip = new Chip(this);
            chip.setText(query);
            chip.setOnClickListener(v -> {
                searchEditText.setText(query);
                searchEditText.setSelection(query.length());
            });
            chipGroupRecent.addView(chip);
        }
    }

    private void setupClearHistoryListener() {
        clearHistoryButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().remove(KEY_HISTORY).apply();
            loadAndDisplayRecentSearches();
        });
    }

    private void setupStaticGenreChips() {
        String[] categories = getResources().getStringArray(R.array.search_chip_categories);
        String arabicMovies = getString(R.string.arabic_movies);
        String arabicTvShows = getString(R.string.arabic_tv_shows);

        chipGroupGenres.removeAllViews();
        for (String category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category);
            chip.setOnClickListener(v -> {
                String chipText = ((Chip) v).getText().toString();

                    searchEditText.setText(chipText);
                    searchEditText.setSelection(chipText.length());

            });
            chipGroupGenres.addView(chip);
        }
    }

    private void showLoading() {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);
        resultsTitleTextView.setVisibility(View.GONE);
    }

    private void showContent() {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
        resultsTitleTextView.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}