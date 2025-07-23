package com.example.listmovies.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listmovies.R;
import com.example.listmovies.adapter.CastAdapter;
import com.example.listmovies.adapter.WatchProviderAdapter;
import com.example.listmovies.api.ActorDetails;
import com.example.listmovies.database.DetailViewModel;
import com.example.listmovies.database.DetailViewModelFactory;
import com.example.listmovies.database.FavoriteMovieEntity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity implements CastAdapter.OnCastMemberClickListener, WatchProviderAdapter.OnProviderClickListener {

    private ImageView movieImageView;
    private TextView titleTextView, voteTextView, overviewTextView, releaseDateTextView;
    private FloatingActionButton fabShare;
    private MaterialButton btnWatchTrailer, btnToggleFavorite;

    // --- المتغيرات الجديدة ---
    private RecyclerView castRecyclerView;
    private CastAdapter castAdapter;

    private RecyclerView watchProvidersRecyclerView;
    private WatchProviderAdapter watchProviderAdapter;
    private String watchProviderLink = null;

    private DetailViewModel viewModel;
    private FavoriteMovieEntity currentMovieEntity;
    private String trailerUrl = null;
    private AlertDialog actorDetailsDialog;
    private Boolean wasFavorite = null;
    private Boolean wasInWatchlist = null;
    private MaterialButton btnToggleWatchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportPostponeEnterTransition();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeViews();
        setupToolbar();
        setupCastRecyclerView(); // --- استدعاء الدالة الجديدة ---
        getIntentData();
        setupWatchProvidersRecyclerView();
        setupViewModel();
        setupObservers();
        setupClickListeners();
    }

    private void initializeViews() {
        movieImageView = findViewById(R.id.movieImage);
        titleTextView = findViewById(R.id.movieTitle);
        voteTextView = findViewById(R.id.movieVote);
        overviewTextView = findViewById(R.id.movieOverview);
        releaseDateTextView = findViewById(R.id.movieReleaseDate);
        fabShare = findViewById(R.id.fabShare);
        btnWatchTrailer = findViewById(R.id.btnWatchTrailer);
        btnToggleFavorite = findViewById(R.id.btnToggleFavorite);
        // --- السطر الجديد ---
        castRecyclerView = findViewById(R.id.cast_recycler_view);
        btnToggleWatchlist = findViewById(R.id.btnToggleWatchlist);
        watchProvidersRecyclerView = findViewById(R.id.watch_providers_recycler_view);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Details");
        }
    }
    private void setupWatchProvidersRecyclerView() {
        watchProviderAdapter = new WatchProviderAdapter(this);
        watchProviderAdapter.setOnProviderClickListener(this);
        watchProvidersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        watchProvidersRecyclerView.setAdapter(watchProviderAdapter);
    }
    // --- الدالة الجديدة ---
    private void setupCastRecyclerView() {
        castAdapter = new CastAdapter(this);
        // جعل الـ RecyclerView أفقيًا
        castAdapter.setOnCastMemberClickListener(this);
        castRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        castRecyclerView.setAdapter(castAdapter);
    }
    // --- نهاية الدالة الجديدة ---

    private void getIntentData() {
        int movieId = getIntent().getIntExtra("id", -1);
        String movieTitle = getIntent().getStringExtra("title");
        String voteString = getIntent().getStringExtra("vote");
        double movieVoteAverage = (voteString != null && !voteString.isEmpty()) ? Double.parseDouble(voteString) : 0.0;
        String movieOverview = getIntent().getStringExtra("overview");
        String moviePosterPath = getIntent().getStringExtra("image_url");
        String movieReleaseDate = getIntent().getStringExtra("release_date");

        currentMovieEntity = new FavoriteMovieEntity(movieId, movieTitle, movieVoteAverage, movieOverview, moviePosterPath, movieReleaseDate);
        ViewCompat.setTransitionName(movieImageView, "poster_" + movieId);
        titleTextView.setText(movieTitle);
        voteTextView.setText(String.format("%.1f", movieVoteAverage));
        overviewTextView.setText(movieOverview);
        releaseDateTextView.setText(movieReleaseDate != null && movieReleaseDate.length() >= 4 ? movieReleaseDate.substring(0, 4) : "N/A");
        Picasso.get()
                .load(moviePosterPath)
                .noFade() // مهم لإلغاء أي تداخل في الأنيميشن
                .into(movieImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // عند نجاح تحميل الصورة، نبدأ الحركة الانتقالية المؤجلة
                        supportStartPostponedEnterTransition();
                    }

                    @Override
                    public void onError(Exception e) {
                        // حتى لو فشل تحميل الصورة، يجب بدء الحركة لمنع تعليق الشاشة
                        supportStartPostponedEnterTransition();
                    }
                });
    }

    private void setupViewModel() {
        int movieId = currentMovieEntity.getId();
        if (movieId == -1) {
            Toast.makeText(this, "Error: Movie ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String apiKey = getString(R.string.api_key);
        DetailViewModelFactory factory = new DetailViewModelFactory(getApplication(), movieId, apiKey);
        viewModel = new ViewModelProvider(this, factory).get(DetailViewModel.class);
    }

    private void setupObservers() {
        // مراقبة حالة المفضلة
        viewModel.isFavorite.observe(this, isFavorite -> {
            if (isFavorite != null) {
                if (isFavorite) {
                    btnToggleFavorite.setIconResource(R.drawable.ic_favorite_filled);
                    // تغيير لون الأيقونة للون "المفضلة"
                    btnToggleFavorite.setIconTint(ContextCompat.getColorStateList(this, R.color.favorite_icon_tint_active));
                } else {
                    btnToggleFavorite.setIconResource(R.drawable.ic_favorite_border);
                    // تغيير لون الأيقونة للون "غير المفضلة"
                    btnToggleFavorite.setIconTint(ContextCompat.getColorStateList(this, R.color.favorite_icon_tint_inactive));
                }

                // منطق إظهار الـ Toast
                // نتأكد أن هذه ليست المرة الأولى التي يتم فيها تحميل الحالة
                if (wasFavorite != null && wasFavorite != isFavorite) {
                    if (isFavorite) {
                        Toast.makeText(this, currentMovieEntity.getTitle() + " added to favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, currentMovieEntity.getTitle() + " removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                }
                // تحديث الحالة السابقة
                wasFavorite = isFavorite;
            }
        });
        viewModel.isInWatchlist.observe(this, isInWatchlist -> {
            if (isInWatchlist != null) {
                if (isInWatchlist) {
                    btnToggleWatchlist.setIconResource(R.drawable.ic_bookmark); // أيقونة ممتلئة
                    btnToggleWatchlist.setIconTint(ContextCompat.getColorStateList(this, R.color.favorite_icon_tint_active));
                } else {
                    btnToggleWatchlist.setIconResource(R.drawable.ic_bookmark); // تعديل: استخدام الأيقونة الفارغة هنا
                    btnToggleWatchlist.setIconTint(ContextCompat.getColorStateList(this, R.color.favorite_icon_tint_inactive));
                }
                if (wasInWatchlist != null && wasInWatchlist != isInWatchlist) {
                    Toast.makeText(this, isInWatchlist ? "Added to watchlist" : "Removed from watchlist", Toast.LENGTH_SHORT).show();
                }
                wasInWatchlist = isInWatchlist;
            }
        });
        viewModel.actorDetails.observe(this, actorDetails -> {
            if (actorDetails != null && actorDetailsDialog != null && actorDetailsDialog.isShowing()) {
                // إذا وصلت البيانات بنجاح، قم بتحديث الـ Dialog
                updateActorDetailsDialog(actorDetails);
            } else if (actorDetailsDialog != null && actorDetailsDialog.isShowing()) {
                // إذا فشل جلب البيانات (null)، أغلق الـ Dialog وأظهر رسالة
                actorDetailsDialog.dismiss();
                Toast.makeText(this, "Failed to load actor details.", Toast.LENGTH_SHORT).show();
            }
        });

        // مراقبة رابط التريلر
        viewModel.trailerUrl.observe(this, url -> {
            this.trailerUrl = url;
            btnWatchTrailer.setEnabled(url != null);
        });

        // --- الجزء المضاف لمراقبة طاقم العمل ---
        viewModel.movieCast.observe(this, cast -> {
            View castTitle = findViewById(R.id.cast_title);
            if (cast != null && !cast.isEmpty()) {
                castAdapter.setCastList(cast);
                castTitle.setVisibility(View.VISIBLE);
                castRecyclerView.setVisibility(View.VISIBLE);
            } else {
                // إخفاء القسم بالكامل إذا لم يكن هناك طاقم عمل
                castTitle.setVisibility(View.GONE);
                castRecyclerView.setVisibility(View.GONE);
            }
        });
        viewModel.watchProviders.observe(this, providersResult -> {
            View providersTitle = findViewById(R.id.watch_providers_title);
            if (providersResult != null && providersResult.getProviders() != null && !providersResult.getProviders().isEmpty()) {
                watchProviderAdapter.setProviderList(providersResult.getProviders());
                this.watchProviderLink = providersResult.getLink(); // حفظ الرابط
                providersTitle.setVisibility(View.VISIBLE);
                watchProvidersRecyclerView.setVisibility(View.VISIBLE);
            } else {
                providersTitle.setVisibility(View.GONE);
                watchProvidersRecyclerView.setVisibility(View.GONE);
            }
        });
        // --- نهاية الجزء المضاف ---
    }

    @Override
    public void onProviderClick() {
        if (watchProviderLink != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(watchProviderLink));
            startActivity(intent);
        }
    }

    private void setupClickListeners() {
        btnToggleFavorite.setOnClickListener(v -> viewModel.toggleFavoriteStatus(currentMovieEntity));
        btnToggleWatchlist.setOnClickListener(v -> viewModel.toggleWatchlistStatus(currentMovieEntity));
        btnWatchTrailer.setOnClickListener(v -> {
            if (trailerUrl != null) {
                showTrailerInWebViewDialog(trailerUrl);
            } else {
                Toast.makeText(this, "Trailer not available.", Toast.LENGTH_SHORT).show();
            }
        });

        fabShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this movie: " + currentMovieEntity.getTitle());
            startActivity(Intent.createChooser(shareIntent, "Share movie via"));
        });
    }

    private void showTrailerInWebViewDialog(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CardBackground);
        View view = getLayoutInflater().inflate(R.layout.dialog_webview_trailer, null);
        WebView webView = view.findViewById(R.id.webViewTrailer);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(url);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(dialogInterface -> webView.destroy());
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCastMemberClick(int actorId) {
        showActorDetailsDialog();
        // 2. اطلب من الـ ViewModel جلب بيانات هذا الممثل
        viewModel.fetchActorDetails(actorId);
    }

    private void showActorDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_actor_details, null);
        builder.setView(view);
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());

        actorDetailsDialog = builder.create();
        actorDetailsDialog.show();
    }

    private void updateActorDetailsDialog(ActorDetails details) {
        // هذا يضمن أننا نصل إلى الـ views الموجودة داخل الـ Dialog المعروض حاليًا
        View dialogView = actorDetailsDialog.getWindow().getDecorView();

        ProgressBar progressBar = dialogView.findViewById(R.id.actor_details_progress);
        View contentLayout = dialogView.findViewById(R.id.actor_details_layout);

        // إخفاء التحميل وإظهار المحتوى
        progressBar.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);

        ImageView actorImage = dialogView.findViewById(R.id.actor_dialog_image);
        TextView actorName = dialogView.findViewById(R.id.actor_dialog_name);
        TextView actorBirthday = dialogView.findViewById(R.id.actor_dialog_birthday);
        TextView actorBiography = dialogView.findViewById(R.id.actor_dialog_biography);

        actorName.setText(details.getName());
        String birthInfo = (details.getBirthday() != null ? "Born: " + details.getBirthday() : "") +
                (details.getPlaceOfBirth() != null ? "\nIn: " + details.getPlaceOfBirth() : "");
        actorBirthday.setText(birthInfo);
        actorBiography.setText(details.getBiography().isEmpty() ? "No biography available." : details.getBiography());

        String imageUrl = "https://image.tmdb.org/t/p/h632" + details.getProfilePath();
        Picasso.get().load(imageUrl).into(actorImage);
    }
}