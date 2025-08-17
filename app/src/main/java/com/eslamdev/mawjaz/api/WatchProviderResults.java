package com.eslamdev.mawjaz.api;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

// هذا الكلاس هو الـ "Wrapper" الخارجي للـ Response
public class WatchProviderResults {
    @SerializedName("results")
    private Map<String, CountrySpecificProviders> results;

    public Map<String, CountrySpecificProviders> getResults() {
        return results;
    }
}