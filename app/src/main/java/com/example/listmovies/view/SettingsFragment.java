package com.example.listmovies.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.listmovies.R;
import com.example.listmovies.util.LocalHelper;

// داخل SettingsFragment.java
public  class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // ابحث عن زر تغيير اللغة
        ListPreference languagePreference = findPreference("language_preference");

        if (languagePreference != null) {
            // أضف مستمع للتغييرات
            languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String selectedLanguage = (String) newValue;

                // اطلب من خبير اللغة حفظ وتطبيق اللغة الجديدة
                LocalHelper.setLocale(requireActivity(), selectedLanguage);

                // أعد تشغيل التطبيق بالكامل لتطبيق اللغة على كل شيء
                Intent intent = new Intent(requireActivity(), SplashScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                // أغلق الـ Activity الحالية
                requireActivity().finish();

                return true;
            });
        }
    }
}