package com.eslamdev.mawjaz.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eslamdev.mawjaz.R;
import com.eslamdev.mawjaz.adapter.CastAdapter;
import com.eslamdev.mawjaz.adapter.WatchProviderAdapter;
import com.eslamdev.mawjaz.api.ActorDetails;
import com.eslamdev.mawjaz.database.FavoriteMovieEntity;
import com.eslamdev.mawjaz.database.TvShowDetailViewModel;
import com.eslamdev.mawjaz.database.TvShowDetailViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class TvShowDetailActivity extends BaseActivity implements CastAdapter.OnCastMemberClickListener, WatchProviderAdapter.OnProviderClickListener {

    private TvShowDetailViewModel viewModel;

    private InterstitialAd mInterstitialAd;

    private ImageView movieImageView;
    private TextView titleTextView, voteTextView, overviewTextView, releaseDateTextView, seasonsTextView;
    private MaterialButton btnWatchTrailer, btnToggleFavorite, btnToggleWatchlist;
    private RecyclerView castRecyclerView, watchProvidersRecyclerView;
    private View castTitle, providersTitle;

    private CastAdapter castAdapter;
    private WatchProviderAdapter watchProviderAdapter;

    private FavoriteMovieEntity currentContentEntity;
    private String watchProviderLink = null;
    private String trailerUrl = null;
    private Boolean wasFavorite = null;
    private Boolean wasInWatchlist = null;
    private AlertDialog actorDetailsDialog;
    private FloatingActionButton fabShare;
    private String originalLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvshow_detail);

        initializeViews();
        setupToolbar();
        setupRecyclerViews();
        getIntentData();
        setupViewModel();
        observeViewModel();
        setupClickListeners();
        loadInterstitialAd();
    }

    private void initializeViews() {
        movieImageView = findViewById(R.id.movieImage);
        titleTextView = findViewById(R.id.movieTitle);
        voteTextView = findViewById(R.id.movieVote);
        overviewTextView = findViewById(R.id.movieOverview);
        releaseDateTextView = findViewById(R.id.movieReleaseDate);
        seasonsTextView = findViewById(R.id.tv_seasons_count);
        btnWatchTrailer = findViewById(R.id.btnWatchTrailer);
        btnToggleFavorite = findViewById(R.id.btnToggleFavorite);
        btnToggleWatchlist = findViewById(R.id.btnToggleWatchlist);
        castRecyclerView = findViewById(R.id.cast_recycler_view);
        watchProvidersRecyclerView = findViewById(R.id.watch_providers_recycler_view);
        castTitle = findViewById(R.id.cast_title);
        providersTitle = findViewById(R.id.watch_providers_title);
        fabShare = findViewById(R.id.fabShare);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        int contentId = getIntent().getIntExtra("id", -1);
        String contentTitle = getIntent().getStringExtra("title");
        String posterPath = getIntent().getStringExtra("image_url");

        this.originalLanguage = getIntent().getStringExtra("original_language");

        currentContentEntity = new FavoriteMovieEntity(contentId, contentTitle, 0, "", posterPath, "");
        titleTextView.setText(contentTitle);
        Picasso.get().load(posterPath).into(movieImageView);
    }

    private void setupViewModel() {
        if (currentContentEntity.getId() == -1) return;
        String apiKey = getString(R.string.api_key);
        TvShowDetailViewModelFactory factory = new TvShowDetailViewModelFactory(getApplication(), currentContentEntity.getId(), apiKey, this.originalLanguage);
        viewModel = new ViewModelProvider(this, factory).get(TvShowDetailViewModel.class);
    }

    private void observeViewModel() {
        viewModel.tvShowDetails.observe(this, details -> {
            if (details != null) {
                getSupportActionBar().setTitle(details.getName());
                titleTextView.setText(details.getName());
                overviewTextView.setText(details.getOverview());
                voteTextView.setText(String.format("%.1f", details.getVoteAverage()));
                releaseDateTextView.setText(details.getFirstAirDate() != null && details.getFirstAirDate().length() >= 4 ? details.getFirstAirDate().substring(0, 4) : "N/A");

                seasonsTextView.setVisibility(View.VISIBLE);
                int seasons = details.getNumberOfSeasons();
                seasonsTextView.setText(getResources().getQuantityString(R.plurals.seasons_count, seasons, seasons));

                Picasso.get().load("https://image.tmdb.org/t/p/w780" + details.getBackdropPath()).into(movieImageView);
                currentContentEntity = new FavoriteMovieEntity(details.getId(), details.getName(), details.getVoteAverage(), details.getOverview(), details.getPosterPath(), details.getFirstAirDate());
            }
        });

        viewModel.isFavorite.observe(this, isFavorite -> {
            if (isFavorite != null) {
                updateButtonState(btnToggleFavorite, isFavorite, R.drawable.ic_favorite_filled, R.drawable.ic_favorite_border);
                if (wasFavorite != null && !wasFavorite.equals(isFavorite)) {
                    Toast.makeText(this, isFavorite ? getString(R.string.added_to_favorites) : getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show();
                }
                wasFavorite = isFavorite;
            }
        });

        viewModel.isInWatchlist.observe(this, isInWatchlist -> {
            if (isInWatchlist != null) {
                updateButtonState(btnToggleWatchlist, isInWatchlist, R.drawable.ic_bookmark, R.drawable.ic_bookmark);
                if (wasInWatchlist != null && !wasInWatchlist.equals(isInWatchlist)) {
                    Toast.makeText(this, isInWatchlist ? "Added to watchlist" : "Removed from watchlist", Toast.LENGTH_SHORT).show();
                }
                wasInWatchlist = isInWatchlist;
            }
        });

        viewModel.tvShowCast.observe(this, cast -> {
            if (cast != null && !cast.isEmpty()) {
                castAdapter.setCastList(cast);
                castTitle.setVisibility(View.VISIBLE);
                castRecyclerView.setVisibility(View.VISIBLE);
            } else {
                castTitle.setVisibility(View.GONE);
                castRecyclerView.setVisibility(View.GONE);
            }
        });

        viewModel.watchProviders.observe(this, result -> {
            if (result != null && result.getProviders() != null && !result.getProviders().isEmpty()) {
                watchProviderAdapter.setProviderList(result.getProviders());
                this.watchProviderLink = result.getLink();
                providersTitle.setVisibility(View.VISIBLE);
                watchProvidersRecyclerView.setVisibility(View.VISIBLE);
            } else {
                providersTitle.setVisibility(View.GONE);
                watchProvidersRecyclerView.setVisibility(View.GONE);
            }
        });

        viewModel.trailerUrl.observe(this, url -> {
            this.trailerUrl = url;
            btnWatchTrailer.setEnabled(url != null);
        });
        viewModel.actorDetails.observe(this, actorDetails -> {
            if (actorDetails != null && actorDetailsDialog != null && actorDetailsDialog.isShowing()) {
                updateActorDetailsDialog(actorDetails);
            } else if (actorDetailsDialog != null && actorDetailsDialog.isShowing()) {
                actorDetailsDialog.dismiss();
                Toast.makeText(this, R.string.actor_details_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        btnToggleFavorite.setOnClickListener(v -> viewModel.toggleFavoriteStatus(currentContentEntity));
        btnToggleWatchlist.setOnClickListener(v -> viewModel.toggleWatchlistStatus(currentContentEntity));
        btnWatchTrailer.setOnClickListener(v -> {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(TvShowDetailActivity.this);
                // Set a listener to know when the ad is closed.
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // After the ad is dismissed, play the trailer.
                        playTrailer();
                    }
                });
            } else {
                // If the ad isn't loaded for any reason, play the trailer directly.
                playTrailer();
            }
        });
        fabShare.setOnClickListener(v -> {
            if (currentContentEntity != null && currentContentEntity.getTitle() != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareText = "Check out this TV Show: " + currentContentEntity.getTitle();
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(Intent.createChooser(shareIntent, "Share TV Show via"));
            } else {
                Toast.makeText(this, "Details not loaded yet.", Toast.LENGTH_SHORT).show();
            }
        });
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



    private void updateButtonState(MaterialButton button, boolean isActive, int activeIcon, int inactiveIcon) {
        button.setIconResource(isActive ? activeIcon : inactiveIcon);
        button.setIconTint(ContextCompat.getColorStateList(this, isActive ? R.color.favorite_icon_tint_active : R.color.favorite_icon_tint_inactive));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
        String birthInfo = (details.getBirthday() != null ? "Born: " + details.getBirthday() : "") +
                (details.getPlaceOfBirth() != null ? "\nIn: " + details.getPlaceOfBirth() : "");
        actorBirthday.setText(birthInfo);
        actorBiography.setText(details.getBiography() != null && !details.getBiography().isEmpty() ? details.getBiography() : "No biography available.");
        String imageUrl = "https://image.tmdb.org/t/p/h632" + details.getProfilePath();
        Picasso.get().load(imageUrl).into(actorImage);
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        // REMEMBER to replace this with your real Interstitial Ad Unit ID before publishing
        String adUnitId = "ca-app-pub-6321231117080513/2496825263"; // This is a test ID

        InterstitialAd.load(this, adUnitId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        // The ad is loaded. Now, decide if we should show it.
                        maybeShowInterstitialAd();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }
    private void maybeShowInterstitialAd() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int detailPageOpenCount = prefs.getInt("detail_page_count", 0);

        // Show the ad every 4th time
        if ((detailPageOpenCount + 1) % 4 == 0) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(this);
            }
        }

        // Increment and save the counter for the next time
        prefs.edit().putInt("detail_page_count", detailPageOpenCount + 1).apply();
    }

    private void playTrailer() {
        if (trailerUrl != null) {
            showTrailerInWebViewDialog(trailerUrl);
        } else {
            Toast.makeText(this, R.string.trailer_not_available, Toast.LENGTH_SHORT).show();
        }
    }
}