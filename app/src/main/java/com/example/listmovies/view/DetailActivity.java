package com.example.listmovies.view;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.listmovies.R;
import com.squareup.picasso.Picasso;


public class DetailActivity extends AppCompatActivity {

    private ImageView movieImageView;
    private TextView titleTextView;
    private TextView voteTextView;
    private TextView overviewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        movieImageView = findViewById(R.id.movieImage);
        titleTextView = findViewById(R.id.movieTitle);
        voteTextView = findViewById(R.id.movieVote);
        overviewTextView = findViewById(R.id.movieOverview);
        
        String title = getIntent().getStringExtra("title");
        String vote = getIntent().getStringExtra("vote");
        String overview = getIntent().getStringExtra("overview");
        String imageUrl = getIntent().getStringExtra("image_url");

        titleTextView.setText(title);
        voteTextView.setText(vote);
        overviewTextView.setText(overview);
        Picasso.get().load(imageUrl).into(movieImageView);
    }
}