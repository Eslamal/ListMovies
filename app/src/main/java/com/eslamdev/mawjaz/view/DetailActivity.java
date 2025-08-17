package com.eslamdev.mawjaz.view;

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
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eslamdev.mawjaz.R;
import com.eslamdev.mawjaz.adapter.CastAdapter;
import com.eslamdev.mawjaz.adapter.WatchProviderAdapter;
import com.eslamdev.mawjaz.api.ActorDetails;
import com.eslamdev.mawjaz.database.DetailViewModel;
import com.eslamdev.mawjaz.database.DetailViewModelFactory;
import com.eslamdev.mawjaz.database.FavoriteMovieEntity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.FullScreenContentCallback;

public class DetailActivity extends BaseActivity implements CastAdapter.OnCastMemberClickListener, WatchProviderAdapter.OnProviderClickListener {

    private DetailViewModel viewModel;
    private InterstitialAd mInterstitialAd;
    // UI Components
    private ImageView movieImageView;
    private TextView titleTextView, voteTextView, overviewTextView, releaseDateTextView;
    private FloatingActionButton fabShare;
    private MaterialButton btnWatchTrailer, btnToggleFavorite, btnToggleWatchlist;
    private RecyclerView castRecyclerView, watchProvidersRecyclerView;
    private View castTitle, providersTitle;

    // Adapters & State
    private CastAdapter castAdapter;
    private WatchProviderAdapter watchProviderAdapter;
    private FavoriteMovieEntity currentMovieEntity;
    private String watchProviderLink = null;
    private String trailerUrl = null;
    private AlertDialog actorDetailsDialog;
    private Boolean wasFavorite = null;
    private Boolean wasInWatchlist = null;
    private String originalLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportPostponeEnterTransition();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeViews();
        setupToolbar();
        setupRecyclerViews();
        getIntentData();
        setupViewModel();
        setupObservers();
        setupClickListeners();
        loadInterstitialAd();
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
        btnToggleWatchlist = findViewById(R.id.btnToggleWatchlist);
        castRecyclerView = findViewById(R.id.cast_recycler_view);
        watchProvidersRecyclerView = findViewById(R.id.watch_providers_recycler_view);
        castTitle = findViewById(R.id.cast_title);
        providersTitle = findViewById(R.id.watch_providers_title);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.details));
        }
    }

    private void setupRecyclerViews() {
        castAdapter = new CastAdapter(this);
        castAdapter.setOnCastMemberClickListener(this);
        castRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        castRecyclerView.setAdapter(castAdapter);

        watchProviderAdapter = new WatchProviderAdapter(this);
        watchProviderAdapter.setOnProviderClickListener(this);
        watchProvidersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        watchProvidersRecyclerView.setAdapter(watchProviderAdapter);
    }

    private void getIntentData() {
        int movieId = getIntent().getIntExtra("id", -1);
        String movieTitle = getIntent().getStringExtra("title");
        String moviePosterPath = getIntent().getStringExtra("image_url");
        this.originalLanguage = getIntent().getStringExtra("original_language");

        currentMovieEntity = new FavoriteMovieEntity(movieId, movieTitle, 0, "", moviePosterPath, "");

        ViewCompat.setTransitionName(movieImageView, "poster_" + movieId);
        titleTextView.setText(movieTitle);

        Picasso.get()
                .load(moviePosterPath)
                .noFade()
                .into(movieImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        supportStartPostponedEnterTransition();
                    }
                    @Override
                    public void onError(Exception e) {
                        supportStartPostponedEnterTransition();
                    }
                });
    }

    private void setupViewModel() {
        int movieId = currentMovieEntity.getId();
        if (movieId == -1) {
            Toast.makeText(this, R.string.error_no_content, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String apiKey = getString(R.string.api_key);
        DetailViewModelFactory factory = new DetailViewModelFactory(getApplication(), movieId, apiKey, this.originalLanguage);
        viewModel = new ViewModelProvider(this, factory).get(DetailViewModel.class);
    }

    private void setupObservers() {
        viewModel.isFavorite.observe(this, isFavorite -> {
            if (isFavorite != null) {
                updateButtonState(btnToggleFavorite, isFavorite, R.drawable.ic_favorite_filled, R.drawable.ic_favorite_border);
                if (wasFavorite != null && !wasFavorite.equals(isFavorite)) {
                    String message = currentMovieEntity.getTitle() + " " + (isFavorite ? getString(R.string.added_to_favorites) : getString(R.string.removed_from_favorites));
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
                wasFavorite = isFavorite;
            }
        });

        viewModel.movieDetails.observe(this, details -> {
            if (details != null) {
                // Update the UI with the full, correct data from the API
                overviewTextView.setText(details.getOverview());
                voteTextView.setText(String.format("%.1f", details.getVoteAverage()));

                // Also update the toolbar title and release date if you want
                getSupportActionBar().setTitle(details.getTitle());
                String releaseDate = details.getReleaseDate();
                if (releaseDate != null && releaseDate.length() >= 4) {
                    releaseDateTextView.setText(releaseDate.substring(0, 4));
                } else {
                    releaseDateTextView.setText("N/A");
                }

                // Important: Update currentMovieEntity with the full details
                // This ensures that adding to favorites/watchlist saves the correct data
                currentMovieEntity = new FavoriteMovieEntity(
                        details.getId(),
                        details.getTitle(),
                        details.getVoteAverage(),
                        details.getOverview(),
                        details.getPosterPath(),
                        details.getReleaseDate()
                );
            }
        });

        viewModel.isInWatchlist.observe(this, isInWatchlist -> {
            if (isInWatchlist != null) {
                updateButtonState(btnToggleWatchlist, isInWatchlist, R.drawable.ic_bookmark, R.drawable.ic_bookmark);
                if (wasInWatchlist != null && !wasInWatchlist.equals(isInWatchlist)) {
                    Toast.makeText(this, isInWatchlist ? getString(R.string.added_to_watchlist) : getString(R.string.removed_from_watchlist), Toast.LENGTH_SHORT).show();
                }
                wasInWatchlist = isInWatchlist;
            }
        });

        viewModel.actorDetails.observe(this, actorDetails -> {
            if (actorDetails != null && actorDetailsDialog != null && actorDetailsDialog.isShowing()) {
                updateActorDetailsDialog(actorDetails);
            } else if (actorDetailsDialog != null && actorDetailsDialog.isShowing()) {
                actorDetailsDialog.dismiss();
                Toast.makeText(this, R.string.actor_details_failed, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.trailerUrl.observe(this, url -> {
            this.trailerUrl = url;
            btnWatchTrailer.setEnabled(url != null);
        });

        viewModel.movieCast.observe(this, cast -> {
            if (cast != null && !cast.isEmpty()) {
                castAdapter.setCastList(cast);
                castTitle.setVisibility(View.VISIBLE);
                castRecyclerView.setVisibility(View.VISIBLE);
            } else {
                castTitle.setVisibility(View.GONE);
                castRecyclerView.setVisibility(View.GONE);
            }
        });

        viewModel.watchProviders.observe(this, providersResult -> {
            if (providersResult != null && providersResult.getProviders() != null && !providersResult.getProviders().isEmpty()) {
                watchProviderAdapter.setProviderList(providersResult.getProviders());
                this.watchProviderLink = providersResult.getLink();
                providersTitle.setVisibility(View.VISIBLE);
                watchProvidersRecyclerView.setVisibility(View.VISIBLE);
            } else {
                providersTitle.setVisibility(View.GONE);
                watchProvidersRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void setupClickListeners() {
        btnToggleFavorite.setOnClickListener(v -> viewModel.toggleFavoriteStatus(currentMovieEntity));
        btnToggleWatchlist.setOnClickListener(v -> viewModel.toggleWatchlistStatus(currentMovieEntity));

        btnWatchTrailer.setOnClickListener(v -> {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(DetailActivity.this);
                // Set a listener to know when the user closes the ad
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // After the ad is closed, play the trailer
                        playTrailer();
                    }
                });
            } else {
                // If the ad is not ready, just play the trailer directly
                playTrailer();
            }
        });

        fabShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_movie_prefix) + currentMovieEntity.getTitle());
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
        });
    }

    private void showTrailerInWebViewDialog(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_webview_trailer, null);
        WebView webView = view.findViewById(R.id.webViewTrailer);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(url);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(dialogInterface -> webView.destroy());
        dialog.show();
    }

    @Override
    public void onCastMemberClick(int actorId) {
        showActorDetailsDialog();
        viewModel.fetchActorDetails(actorId);
    }

    @Override
    public void onProviderClick() {
        if (watchProviderLink != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(watchProviderLink));
            startActivity(intent);
        }
    }

    private void showActorDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_actor_details, null);
        builder.setView(view);
        builder.setNegativeButton(R.string.dialog_close, (dialog, which) -> dialog.dismiss());
        actorDetailsDialog = builder.create();
        actorDetailsDialog.show();
    }

    private void updateActorDetailsDialog(ActorDetails details) {
        View dialogView = actorDetailsDialog.getWindow().getDecorView();
        ProgressBar progressBar = dialogView.findViewById(R.id.actor_details_progress);
        View contentLayout = dialogView.findViewById(R.id.actor_details_layout);
        progressBar.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);

        ImageView actorImage = dialogView.findViewById(R.id.actor_dialog_image);
        TextView actorName = dialogView.findViewById(R.id.actor_dialog_name);
        TextView actorBirthday = dialogView.findViewById(R.id.actor_dialog_birthday);
        TextView actorBiography = dialogView.findViewById(R.id.actor_dialog_biography);

        actorName.setText(details.getName());
        String birthInfo = (details.getBirthday() != null ? getString(R.string.actor_birth_date) + details.getBirthday() : "") +
                (details.getPlaceOfBirth() != null ? getString(R.string.actor_birth_place) + details.getPlaceOfBirth() : "");
        actorBirthday.setText(birthInfo);
        actorBiography.setText(details.getBiography().isEmpty() ? getString(R.string.actor_no_biography) : details.getBiography());
        String imageUrl = "https://image.tmdb.org/t/p/h632" + details.getProfilePath();
        Picasso.get().load(imageUrl).into(actorImage);
    }

    private void updateButtonState(MaterialButton button, boolean isActive, int activeIcon, int inactiveIcon) {
        button.setIconResource(isActive ? activeIcon : inactiveIcon);
        button.setIconTint(ContextCompat.getColorStateList(this, isActive ? R.color.favorite_icon_tint_active : R.color.favorite_icon_tint_inactive));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        // استبدل هذا بالـ ID الحقيقي الخاص بك
        String adUnitId = "ca-app-pub-6321231117080513/2496825263";

        InterstitialAd.load(this, adUnitId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }

private void playTrailer() {
    if (trailerUrl != null) {
        showTrailerInWebViewDialog(trailerUrl);
    } else {
        Toast.makeText(this, R.string.trailer_not_available, Toast.LENGTH_SHORT).show();
    }
}

}