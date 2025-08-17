package com.eslamdev.mawjaz.api;

import com.google.gson.annotations.SerializedName;

public class CastMember {

    @SerializedName("id") // --- أضف هذا السطر ---
    private int id; // --- أضف هذا المتغير ---

    @SerializedName("name")
    private String name;

    @SerializedName("character")
    private String character;

    @SerializedName("profile_path")
    private String profilePath;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCharacter() { return character; }
    public String getProfilePath() { return profilePath; }
}