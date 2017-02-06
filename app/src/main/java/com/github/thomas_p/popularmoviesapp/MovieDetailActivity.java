package com.github.thomas_p.popularmoviesapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.thomas_p.popularmoviesapp.models.Movie;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView mDetailTitle;
    private ImageView mDetailImage;
    private TextView mDetailDescription;

    private TextView mDetailRelease;
    private TextView mDetailVote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent = getIntent();
        Movie movie = (Movie) intent.getSerializableExtra("movie");

        mDetailTitle = (TextView) findViewById(R.id.md_title);
        mDetailTitle.setText(movie.getTitle());
        setTitle("Details - " +movie.getTitle());

        mDetailDescription = (TextView) findViewById(R.id.md_description);
        mDetailDescription.setText(movie.getOverview());

        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy");
        mDetailRelease = (TextView) findViewById(R.id.md_release_date);
        mDetailRelease.setText(formatter.format(movie.getReleaseDate()));


        mDetailVote = (TextView) findViewById(R.id.md_vote_average);
        mDetailVote.setText(String.format("%.2f", movie.getVoteAverage()));



        mDetailImage = (ImageView) findViewById(R.id.md_image);
        mDetailImage.setContentDescription(movie.getTitle());
        Picasso.with(this).load(movie.getPoster()).into(mDetailImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
