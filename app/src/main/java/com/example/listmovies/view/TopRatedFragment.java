package com.example.listmovies.view;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.listmovies.api.Movie;
import com.example.listmovies.adapter.MovieAdapter;
import com.example.listmovies.api.MovieResponse;
import com.example.listmovies.R;
import com.example.listmovies.api.TMDbApi;
import com.example.listmovies.database.MovieViewModel;
import com.example.listmovies.database.MovieViewModelFactory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TopRatedFragment extends Fragment {
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private MovieViewModel movieViewModel;
    private ProgressBar progressBar;

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_rated, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(R.color.colorPrimaryDark));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        movieAdapter = new MovieAdapter(getContext());
        recyclerView.setAdapter(movieAdapter);

        fetchTopRatedMovies();
        String apiKey = getString(R.string.api_key);
        String category = "top_rated";
        MovieViewModelFactory factory = new MovieViewModelFactory(getActivity().getApplication(), apiKey, category);

        movieViewModel = new ViewModelProvider(this, factory).get(MovieViewModel.class);
        movieViewModel.getMovies().observe(getViewLifecycleOwner(), new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                movieAdapter.setMovies(movies);
            }
        });


        return view;
    }

    private void fetchTopRatedMovies() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TMDbApi api = retrofit.create(TMDbApi.class);
        String api_key=getString(R.string.api_key);
        Call<MovieResponse> call = api.getTopRatedMovies(api_key);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);


        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    movieAdapter.setMovies(movies);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: "+t.getMessage() , Toast.LENGTH_LONG).show();
            }
        });
    }
}

