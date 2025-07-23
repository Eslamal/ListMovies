package com.example.listmovies.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDbApi {

    // --- هذا هو التعديل ---
    // أضفنا باراميتر جديد: page@Query("page") int
    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("page") int page);
    // --- نهاية التعديل ---

    @GET("search/movie")
    Call<MovieResponse> searchMovies(@Query("api_key") String apiKey, @Query("query") String query, @Query("language") String language);

    @GET("movie/{movie_id}/videos")
    Call<VideoResponse> getMovieVideos(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/credits")
    Call<CreditsResponse> getMovieCredits(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("person/{person_id}")
    Call<ActorDetails> getPersonDetails(@Path("person_id") int personId, @Query("api_key") String apiKey);

    @GET("genre/movie/list")
    Call<GenreListResponse> getGenres(@Query("api_key") String apiKey, @Query("language") String language);

    @GET("discover/movie")
    Call<MovieResponse> discoverMoviesByGenre(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("with_genres") int genreId,
            @Query("page") int page
    );

    @GET("movie/{movie_id}/watch/providers")
    Call<WatchProviderResults> getWatchProviders(@Path("movie_id") int movieId, @Query("api_key") String apiKey);
}

