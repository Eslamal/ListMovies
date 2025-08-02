package com.example.listmovies.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TvShowResponse {
    @SerializedName("results")
    private List<TvShow> results;

    public List<TvShow> getResults() {
        return results;
    }
}