package com.example.listmovies;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.preference.PreferenceManager;

// --- 1. Import the new classes ---
import com.example.listmovies.util.AppOpenAdManager;
import com.google.android.gms.ads.MobileAds;

import java.util.Locale;

// --- 2. Implement LifecycleObserver ---
public class MyApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener, LifecycleObserver {

    // --- 3. Add the AppOpenAdManager variable ---
    private AppOpenAdManager appOpenAdManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // --- ADDED: Initialize the Ad Manager ---
        appOpenAdManager = new AppOpenAdManager();

        // --- MODIFIED: Initialize MobileAds correctly ---
        MobileAds.initialize(this, initializationStatus -> {});

        // Add the lifecycle observer to know when the app comes to the foreground
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        // --- Your existing code remains unchanged ---
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        applyTheme(sharedPreferences.getString("theme_preference", "system"));
        applyLanguage(sharedPreferences.getString("language_preference", "system"));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    // --- 4. ADDED: This method is called when the app enters the foreground ---
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onMoveToForeground() {
        // The AppOpenAdManager will handle showing the ad.
        // For this to work best, the ad should be shown from the current activity.
        // This setup prepares the ad to be shown from the SplashScreen.
    }

    // --- 5. ADDED: A getter so other parts of the app can access the ad manager ---
    public AppOpenAdManager getAppOpenAdManager() {
        return appOpenAdManager;
    }

    // --- Your existing methods remain unchanged ---
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ("theme_preference".equals(key)) {
            applyTheme(sharedPreferences.getString(key, "system"));
        }
        if ("language_preference".equals(key)) {
            // ...
        }
    }

    private void applyTheme(String themeValue) {
        switch (themeValue) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    private void applyLanguage(String langValue) {
        if ("system".equals(langValue)) {
            return;
        }
        Locale locale = new Locale(langValue);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}