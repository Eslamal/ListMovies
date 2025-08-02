package com.example.listmovies.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import androidx.preference.PreferenceManager;
import java.util.Locale;

public class LocalHelper {

    private static final String PREF_KEY_LANGUAGE = "language_preference";

    /**
     * يتم استدعاء هذه الدالة من كل شاشة لتطبيق اللغة الصحيحة.
     * هذا هو "العقل" الذي يقرر اللغة التي يجب عرضها.
     */
    public static Context onAttach(Context context) {
        // اقرأ اللغة التي اختارها المستخدم، والقيمة الافتراضية هي "system"
        String lang = getPersistedLanguage(context);
        return setLocale(context, lang);
    }

    /**
     * يقوم بتطبيق اللغة المطلوبة على الـ Context.
     */
    public static Context setLocale(Context context, String languageCode) {
        // إذا كان الاختيار هو "system"، لا تفعل شيئاً واترك النظام يقرر
        if (languageCode.equals("system")) {
            return context;
        }

        // إذا اختار المستخدم لغة معينة، قم بتطبيقها
        return updateResources(context, languageCode);
    }

    /**
     * يقوم بحفظ اختيار المستخدم في SharedPreferences.
     */
    public static void persistLanguage(Context context, String languageCode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(PREF_KEY_LANGUAGE, languageCode).apply();
    }

    /**
     * يقرأ اللغة المحفوظة.
     */
    public static String getPersistedLanguage(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // القيمة الافتراضية "system" مهمة جداً
        return preferences.getString(PREF_KEY_LANGUAGE, "system");
    }

    /**
     * الكود الفعلي لتحديث موارد التطبيق باللغة الجديدة.
     */
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        return context.createConfigurationContext(config);
    }
}