package com.eslamdev.mawjaz.database;

import android.app.Application;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.eslamdev.mawjaz.api.ContentItem;
import com.eslamdev.mawjaz.util.LocalHelper;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private final MovieRepository movieRepository;
    private final String apiKey;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();

    // --- 1. RENAMED: Changed from searchResults to results for clarity ---
    public final LiveData<List<ContentItem>> results;

    public SearchViewModel(@NonNull Application application, String apiKey) {
        super(application);
        this.movieRepository = new MovieRepository(application);
        this.apiKey = apiKey;

        // The switchMap now correctly decides what data to fetch
        this.results = Transformations.switchMap(searchQuery, query -> {
            String currentLanguageCode = LocalHelper.getPersistedLanguage(getApplication());
            if (currentLanguageCode.equals("system")) {
                currentLanguageCode = Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
            }

            // If the search query is empty or too short, fetch trending content
            if (query == null || query.trim().isEmpty()) {
                return movieRepository.fetchTrending(apiKey, currentLanguageCode);
            } else {
                // Otherwise, perform a search
                return movieRepository.searchAllContent(apiKey, query, currentLanguageCode);
            }
        });
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    // --- 2. ADDED: The missing loadInitialData() method ---
    // This method is called to trigger the initial load of trending items.
    public void loadInitialData() {
        // By setting the query to an empty string, we trigger the switchMap's "trending" logic.
        if (searchQuery.getValue() == null || !searchQuery.getValue().isEmpty()) {
            searchQuery.setValue("");
        }
    }
}