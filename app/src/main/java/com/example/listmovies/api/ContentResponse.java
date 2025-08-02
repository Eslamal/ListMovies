package com.example.listmovies.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ContentResponse {

    @SerializedName("results")
    private List<TrendingItem> results;

    public List<TrendingItem> getResults() {
        return results;
    }
}