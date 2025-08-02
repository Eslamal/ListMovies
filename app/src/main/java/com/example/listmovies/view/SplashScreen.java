package com.example.listmovies.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.listmovies.MyApplication;
import com.example.listmovies.util.AppOpenAdManager;

// Make sure it extends AppCompatActivity, not BaseActivity, to avoid context issues on launch
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // This must be called before super.onCreate()
        androidx.core.splashscreen.SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // No need to set a content view if you're just showing the splash icon

        // 1. Get the instance of the Ad Manager from your Application class
        MyApplication myApplication = (MyApplication) getApplication();
        AppOpenAdManager appOpenAdManager = myApplication.getAppOpenAdManager();

        // 2. Show the ad. The code to navigate to MainActivity will run
        //    inside the listener, AFTER the ad is dismissed.
        appOpenAdManager.showAdIfAvailable(
                this,
                () -> {
                    // This is the "OnShowAdCompleteListener"
                    // This code runs when the ad is closed or if no ad was available.
                    navigateToMainActivity();
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}