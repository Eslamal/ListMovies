package com.example.listmovies.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.listmovies.R;
import com.example.listmovies.adapter.MovieAdapter;
import com.example.listmovies.database.MovieViewModel;
import com.example.listmovies.database.MovieViewModelFactory;
import com.google.android.material.button.MaterialButton;

public class TopRatedFragment extends Fragment {

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private MovieViewModel movieViewModel;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View emptyStateLayout;
    private View errorStateLayout;
    private TextView errorTextView;
    private LinearLayoutManager layoutManager; // تم تعريف الـ LayoutManager هنا

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_rated, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupViewModel();
        observeViewModel();
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        errorStateLayout = view.findViewById(R.id.errorStateLayout);
        errorTextView = view.findViewById(R.id.errorTextView);
    }

    private void setupRecyclerView() {
        // --- تعديل: إعداد الـ LayoutManager وإضافة الـ Scroll Listener ---
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        movieAdapter = new MovieAdapter(getContext());
        recyclerView.setAdapter(movieAdapter);

        // إضافة Listener لمراقبة التمرير وتحميل المزيد من الصفحات
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // التحقق من أننا لسنا في حالة تحميل حاليًا
                boolean isLoading = movieViewModel.getIsLoading().getValue() != null && movieViewModel.getIsLoading().getValue();

                // إذا وصلنا إلى ما قبل نهاية القائمة بـ 5 عناصر، نقوم بتحميل الصفحة التالية
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5 && firstVisibleItemPosition >= 0 && totalItemCount > 0) {
                    movieViewModel.loadNextPage();
                }
            }
        });
    }

    private void setupViewModel() {
        showLoading(); // عرض التحميل الأولي
        String apiKey = getString(R.string.api_key);
        String category = "top_rated";
        String language = "en-US";
        MovieViewModelFactory factory = new MovieViewModelFactory(requireActivity().getApplication(), apiKey, category, language);
        movieViewModel = new ViewModelProvider(this, factory).get(MovieViewModel.class);
    }

    private void observeViewModel() {
        // مراقبة قائمة الأفلام
        movieViewModel.getMovies().observe(getViewLifecycleOwner(), movies -> {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);

            if (movies != null && !movies.isEmpty()) {
                movieAdapter.setMovies(movies);
                showContent();
            } else if (movies != null) {
                showEmptyState();
            } else {
                showErrorState(getString(R.string.error_occurred));
            }
        });

        // (اختياري) مراقبة حالة التحميل لإظهار ProgressBar في آخر القائمة
        movieViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // يمكنك هنا تحديث الـ adapter لإظهار أو إخفاء footer التحميل
            // if (isLoading) { adapter.addLoadingFooter(); } else { adapter.removeLoadingFooter(); }
        });
    }

    private void setupListeners() {
        // --- تعديل: عند التحديث، قم بتحميل الصفحة الأولى ---
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (movieViewModel != null) {
                movieViewModel.loadFirstPage();
            }
        });

        // --- تعديل: عند إعادة المحاولة، قم بتحميل الصفحة الأولى ---
        MaterialButton errorRetryButton = errorStateLayout.findViewById(R.id.btnRetryError);
        if (errorRetryButton != null) {
            errorRetryButton.setOnClickListener(v -> {
                if (movieViewModel != null) {
                    movieViewModel.loadFirstPage();
                }
            });
        }
    }

    private void showLoading() {
        // إظهار الـ ProgressBar الرئيسي فقط، وإخفاء كل شيء آخر
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);
        errorStateLayout.setVisibility(View.GONE);
    }

    private void showContent() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
        errorStateLayout.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
        errorStateLayout.setVisibility(View.GONE);
        TextView emptyText = emptyStateLayout.findViewById(R.id.emptyStateText);
        if (emptyText != null) {
            emptyText.setText(R.string.no_top_rated_movies);
        }
    }

    private void showErrorState(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);
        errorStateLayout.setVisibility(View.VISIBLE);
        errorTextView.setText(message);
    }
}