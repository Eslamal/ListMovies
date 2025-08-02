package com.example.listmovies.view;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import com.example.listmovies.util.LocalHelper;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        // قبل أن يتم بناء أي شاشة، نقوم بتمريرها إلى خبير اللغة ليقوم بتعديلها
        super.attachBaseContext(LocalHelper.onAttach(newBase));
    }
}