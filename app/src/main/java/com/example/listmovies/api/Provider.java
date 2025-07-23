package com.example.listmovies.api;

import com.google.gson.annotations.SerializedName;

public class Provider {
    @SerializedName("logo_path")
    private String logoPath;

    @SerializedName("provider_name")
    private String providerName;

    public String getLogoPath() {
        return logoPath;
    }

    public String getProviderName() {
        return providerName;
    }
}