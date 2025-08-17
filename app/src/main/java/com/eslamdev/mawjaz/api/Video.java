// File: com/example/listmovies/api/Video.java
package com.eslamdev.mawjaz.api;

import com.google.gson.annotations.SerializedName;

public class Video {
    @SerializedName("id")
    private String id; // The ID of the video itself, not the movie
    private String iso_639_1;
    private String iso_3166_1;
    private String key; // This is the YouTube video key
    private String name;
    private String site; // e.g., "YouTube"
    private int size;
    private String type; // e.g., "Trailer", "Teaser", "Clip"

    // Constructor (Optional, if you only deserialize)
    public Video(String id, String iso_639_1, String iso_3166_1, String key, String name, String site, int size, String type) {
        this.id = id;
        this.iso_639_1 = iso_639_1;
        this.iso_3166_1 = iso_3166_1;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getIso_639_1() {
        return iso_639_1;
    }

    public String getIso_3166_1() {
        return iso_3166_1;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public int getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    // Setters (Optional, if you only deserialize)
    public void setId(String id) {
        this.id = id;
    }

    public void setIso_639_1(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
    }

    public void setIso_3166_1(String iso_3166_1) {
        this.iso_3166_1 = iso_3166_1;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setType(String type) {
        this.type = type;
    }
}