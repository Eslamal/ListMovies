package com.example.listmovies.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.listmovies.R;
import com.example.listmovies.adapter.GenreAdapter;
import com.example.listmovies.api.Genre;
import com.example.listmovies.database.GenresViewModel;

public class GenresFragment extends Fragment implements GenreAdapter.OnGenreClickListener {

    private GenresViewModel viewModel;
    private RecyclerView recyclerView;
    private GenreAdapter adapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genres, container, false);

        progressBar = view.findViewById(R.id.genres_progress_bar);
        recyclerView = view.findViewById(R.id.genres_recycler_view);

        setupRecyclerView();

        viewModel = new ViewModelProvider(this).get(GenresViewModel.class);

        observeViewModel();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new GenreAdapter();
        adapter.setOnGenreClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getGenres().observe(getViewLifecycleOwner(), genres -> {
            if (genres != null && !genres.isEmpty()) {
                adapter.setGenres(genres);
                recyclerView.setVisibility(View.VISIBLE);
            } else if (genres == null) {
                Toast.makeText(getContext(), "Failed to load genres", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onGenreClick(Genre genre) {

         Intent intent = new Intent(getActivity(), GenreMoviesActivity.class);
         intent.putExtra("GENRE_ID", genre.getId());
         intent.putExtra("GENRE_NAME", genre.getName());
         startActivity(intent);
    }
}