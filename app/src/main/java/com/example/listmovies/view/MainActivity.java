package com.example.listmovies.view;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.listmovies.R;
import com.example.listmovies.adapter.MovieAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabSearch;
    private AdView mAdView;

    // Preference key for dark mode
    private static final String PREF_DARK_MODE_ENABLED = "dark_mode_enabled";
    private static final String PREF_DARK_MODE_SET_MANUALLY = "dark_mode_set_manually"; // New preference to track manual setting

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load and apply theme preference BEFORE calling super.onCreate()
        loadThemePreference();
        super.onCreate(savedInstanceState);

        // Language preference should still be set here if needed before setContentView
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = preferences.getString("language", "en");
        setLocale(language);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        // Set up ViewPager2 with a custom adapter
        MoviePagerAdapter moviePagerAdapter = new MoviePagerAdapter(this);
        viewPager.setAdapter(moviePagerAdapter);

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.top_rated);
                    break;
                case 1:
                    tab.setText(R.string.popular);
                    break;
                case 2: // --- الجزء الجديد ---
                    tab.setText(R.string.genres);
                    break;
            }
        }).attach();

        // Set OnClickListener for fabSearch
        if (fabSearch != null) {
            fabSearch.setOnClickListener(v -> navigateToSearchActivity());
        }
    }

    /**
     * Loads the saved theme preference and applies it.
     * This method must be called BEFORE super.onCreate(savedInstanceState).
     */
    private void loadThemePreference() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isManuallySet = preferences.getBoolean(PREF_DARK_MODE_SET_MANUALLY, false); // Check if theme was set manually

        if (isManuallySet) {
            boolean isDarkModeEnabled = preferences.getBoolean(PREF_DARK_MODE_ENABLED, false);
            if (isDarkModeEnabled) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        } else {
            // If not manually set, follow system default
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        updateThemeMenuItem(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateThemeMenuItem(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    private void updateThemeMenuItem(Menu menu) {
        MenuItem themeItem = menu.findItem(R.id.action_theme);
        if (themeItem != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isManuallySet = preferences.getBoolean(PREF_DARK_MODE_SET_MANUALLY, false);

            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            boolean isDeviceInDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES;

            // Determine the current effective theme
            boolean currentThemeIsDark;
            if (isManuallySet) {
                currentThemeIsDark = preferences.getBoolean(PREF_DARK_MODE_ENABLED, false);
            } else {
                currentThemeIsDark = isDeviceInDarkMode;
            }


            if (currentThemeIsDark) {
                themeItem.setIcon(R.drawable.ic_light_mode);
                themeItem.setTitle(R.string.action_light_mode_text);
                themeItem.setContentDescription(getString(R.string.dark_mode_on_description));
            } else {
                themeItem.setIcon(R.drawable.ic_dark_mode);
                themeItem.setTitle(R.string.action_dark_mode_text);
                themeItem.setContentDescription(getString(R.string.light_mode_on_description));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            navigateToSearchActivity();
            return true;
        }
        else if (id == R.id.action_favorites) {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_watchlist) { // --- أضف هذا الشرط ---
            Intent intent = new Intent(MainActivity.this, WatchlistActivity.class);
            startActivity(intent);
            return true;}
        else if (id == R.id.action_theme) {
            toggleTheme();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateToSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
    private void toggleTheme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isManuallySet = preferences.getBoolean(PREF_DARK_MODE_SET_MANUALLY, false);
        boolean isDarkModeEnabled = preferences.getBoolean(PREF_DARK_MODE_ENABLED, false);

        boolean newDarkModeState;

        if (isManuallySet) {
            // If it was manually set, just toggle the saved state
            newDarkModeState = !isDarkModeEnabled;
        } else {
            // If it was following the system, now we're manually overriding it
            // Determine the current system mode to decide what the first manual toggle should be
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            boolean isCurrentlyInSystemDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES;
            // If system is dark, the first toggle makes it light. If system is light, the first toggle makes it dark.
            newDarkModeState = !isCurrentlyInSystemDarkMode;
        }

        // Save the new state and mark as manually set
        preferences.edit()
                .putBoolean(PREF_DARK_MODE_ENABLED, newDarkModeState)
                .putBoolean(PREF_DARK_MODE_SET_MANUALLY, true) // Mark as manually set
                .apply();

        // Apply the new theme mode
        if (newDarkModeState) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        recreate();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.exit)
                .setMessage(R.string.exit_confirmation)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> MainActivity.super.onBackPressed())
                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> dialogInterface.cancel())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    // Adapter for ViewPager2
    private static class MoviePagerAdapter extends FragmentStateAdapter {
        public MoviePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new TopRatedFragment();
                case 1:
                    return new PopularFragment();
                case 2: // --- الجزء الجديد ---
                    return new GenresFragment();
                default:
                    return new TopRatedFragment(); // Fallback
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}