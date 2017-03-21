package com.github.thomas_p.popularmoviesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.thomas_p.popularmoviesapp.R;
import com.github.thomas_p.popularmoviesapp.models.MovieReview;

import java.util.ArrayList;

public class MovieReviewListAdapter extends RecyclerView.Adapter<MovieReviewListAdapter.MovieReviewListAdapterViewHolder> {

    private ArrayList<MovieReview> movieReviews = null;

    @Override
    public MovieReviewListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View itemReview = inflater.inflate(R.layout.item_reviews, parent, false);
        return new MovieReviewListAdapterViewHolder(itemReview);
    }

    @Override
    public void onBindViewHolder(MovieReviewListAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return movieReviews != null ? movieReviews.size() : 0;
    }


    public void setMovieReviews(ArrayList<MovieReview> movieReviews) {
        this.movieReviews = movieReviews;
        notifyDataSetChanged();
    }



    class MovieReviewListAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView author;
        private final TextView review;

        public MovieReviewListAdapterViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.review_author);
            review = (TextView) itemView.findViewById(R.id.review_text);
        }

        void bind(final int position) {
            if (movieReviews != null) {
                final MovieReview movieReview = movieReviews.get(position);
                author.setText(movieReview != null ? movieReview.getAuthor() : null);
                review.setText(movieReview != null ? movieReview.getReview() : null);
            }
        }
    }
}
