package com.eslamdev.mawjaz.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreditsResponse {
    @SerializedName("cast")
    private List<CastMember> cast;

    // Getter
    public List<CastMember> getCast() { return cast; }
}