package com.example.listmovies.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.listmovies.api.Movie;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private final MovieRepository movieRepository;
    private final String apiKey;
    private final String language;

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();

    // **السطر ده اتغير:** هنا بنعلن عن المتغير بس
    public final LiveData<List<Movie>> searchResults;

    public SearchViewModel(@NonNull Application application, String apiKey, String language) {
        super(application);

        // هنا بندي للمتغيرات قيمتها الأول
        this.movieRepository = new MovieRepository(application);
        this.apiKey = apiKey;
        this.language = language;

        // **وهنا التغيير الأساسي:** بنعرّف الـ searchResults بعد ما المتغيرات اللي بيعتمد عليها تكون جاهزة
        this.searchResults = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().length() < 2) {
                MutableLiveData<List<Movie>> emptyResult = new MutableLiveData<>();
                emptyResult.setValue(null);
                return emptyResult;
            }
            // دلوقتي استخدام المتغيرات دي آمن ومفيهوش مشاكل
            return movieRepository.searchMovies(apiKey, query, language);
        });
    }

    /**
     * الدالة دي بنستدعيها من الـ Activity عشان نبدأ عملية بحث جديدة
     * @param query كلمة البحث اللي كتبها المستخدم
     */
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }
}