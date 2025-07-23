package com.example.listmovies.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.listmovies.api.Movie;
import java.util.ArrayList;
import java.util.List;

public class MovieViewModel extends AndroidViewModel {
    private final MovieRepository movieRepository;
    private final String apiKey;
    private final String category;
    private final String language;

    // LiveData الأساسية التي تحتوي على القائمة الكاملة للأفلام وتتحدث باستمرار
    private final MutableLiveData<List<Movie>> movies = new MutableLiveData<>();

    // LiveData لمراقبة حالة التحميل (لإظهار وإخفاء ProgressBar)
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private int currentPage = 1;
    private boolean isLastPage = false; // لمعرفة هل وصلنا لآخر صفحة

    public MovieViewModel(@NonNull Application application, String apiKey, String category, String language) {
        super(application);
        // تهيئة المتغيرات الأساسية
        this.movieRepository = new MovieRepository(application);
        this.apiKey = apiKey;
        this.category = category;
        this.language = language;
        // نبدأ بتحميل أول صفحة بمجرد إنشاء الـ ViewModel
        loadFirstPage();
    }

    // الـ Fragment سيراقب هذه الـ LiveData لعرض الأفلام
    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    // الـ Fragment سيراقب هذه الـ LiveData لعرض وإخفاء علامة التحميل
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * تستخدم لتحميل الصفحة الأولى فقط، أو عند عمل "Pull-to-refresh".
     * تقوم بمسح القائمة القديمة والبدء من جديد.
     */
    public void loadFirstPage() {
        // إعادة تعيين كل شيء للبداية
        currentPage = 1;
        isLastPage = false;
        isLoading.setValue(true);

        movieRepository.fetchMovies(apiKey, category, language, currentPage, new MovieRepository.OnMoviesFetchedListener() {
            @Override
            public void onSuccess(List<Movie> newMovies) {
                // عند نجاح تحميل أول صفحة، يتم وضعها في الـ LiveData
                movies.setValue(newMovies);
                isLoading.setValue(false);
            }

            @Override
            public void onFailure() {
                // في حالة الفشل، يمكن إرسال رسالة خطأ للـ UI
                movies.setValue(null); // إرسال null للإشارة إلى وجود خطأ
                isLoading.setValue(false);
            }
        });
    }

    /**
     * تستخدم لتحميل الصفحة التالية عند التمرير (Scrolling).
     * تقوم بإضافة الأفلام الجديدة إلى القائمة الحالية.
     */
    public void loadNextPage() {
        // منع إرسال طلب جديد إذا كان هناك طلب قيد التنفيذ أو لو وصلنا لآخر صفحة
        if (Boolean.TRUE.equals(isLoading.getValue()) || isLastPage) {
            return;
        }

        isLoading.setValue(true);
        currentPage++;

        movieRepository.fetchMovies(apiKey, category, language, currentPage, new MovieRepository.OnMoviesFetchedListener() {
            @Override
            public void onSuccess(List<Movie> newMovies) {
                if (newMovies == null || newMovies.isEmpty()) {
                    isLastPage = true; // لو الصفحة الجديدة فارغة، فهذا يعني أننا وصلنا للنهاية
                } else {
                    // جلب القائمة الحالية
                    List<Movie> currentList = movies.getValue();
                    if (currentList == null) {
                        currentList = new ArrayList<>();
                    }
                    // إضافة الأفلام الجديدة على القائمة الحالية
                    currentList.addAll(newMovies);
                    // تحديث الـ LiveData بالقائمة المجمعة الجديدة
                    movies.setValue(currentList);
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure() {
                // في حالة الفشل، نتوقف عن محاولة تحميل المزيد حاليًا
                isLoading.setValue(false);
            }
        });
    }
}