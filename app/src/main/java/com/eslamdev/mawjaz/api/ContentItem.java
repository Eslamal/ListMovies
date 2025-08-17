package com.eslamdev.mawjaz.api;

public class ContentItem {
    private final int id;
    private final String title;
    private final double voteAverage;
    private final String overview;
    private final String posterPath;
    private final String releaseDate;
    private final String type;
    private final String originalLanguage;

    public ContentItem(int id, String title, double voteAverage, String overview, String posterPath, String releaseDate, String type, String originalLanguage) {
        this.id = id;
        this.title = title;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.type = type;
        this.originalLanguage = originalLanguage;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getVoteAverage() { return voteAverage; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
    public String getReleaseDate() { return releaseDate; }
    public String getType() { return type; }

    // --- 1. أضف هذا الـ Getter ---
    public String getOriginalLanguage() { return originalLanguage; }

    // دوال التحويل: تحول الفيلم أو المسلسل إلى هذا العنصر الموحد
    public static ContentItem fromMovie(Movie movie) {
        return new ContentItem(
                movie.getId(),
                movie.getTitle(),
                movie.getVoteAverage(),
                movie.getOverview(),
                movie.getPosterPath(),
                movie.getReleaseDate(),
                "movie",
                movie.getOriginalLanguage() // --- 2. مرر اللغة الأصلية هنا ---
        );
    }

    public static ContentItem fromTvShow(TvShow tvShow) {
        return new ContentItem(
                tvShow.getId(),
                tvShow.getName(),
                tvShow.getVoteAverage(),
                tvShow.getOverview(),
                tvShow.getPosterPath(),
                tvShow.getFirstAirDate(),
                "tv",
                tvShow.getOriginalLanguage() // --- 3. مرر اللغة الأصلية هنا ---
        );
    }


    public static ContentItem fromTrendingItem(TrendingItem item) {
        if (item.getMediaType() == null) return null;

        // Check the media_type to decide how to create the ContentItem
        if ("movie".equals(item.getMediaType())) {
            return new ContentItem(
                    item.getId(),
                    item.getTitle(),       // Use movie title
                    item.getVoteAverage(),
                    item.getOverview(),
                    item.getPosterPath(),
                    item.getReleaseDate(), // Use movie release date
                    "movie",
                    item.getOriginalLanguage()
            );
        } else if ("tv".equals(item.getMediaType())) {
            return new ContentItem(
                    item.getId(),
                    item.getName(),          // Use TV show name
                    item.getVoteAverage(),
                    item.getOverview(),
                    item.getPosterPath(),
                    item.getFirstAirDate(),  // Use TV show air date
                    "tv",
                    item.getOriginalLanguage()
            );
        }
        return null; // In case the type is something else, like "person"
    }
}