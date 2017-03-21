package com.github.thomas_p.popularmoviesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.thomas_p.popularmoviesapp.R;
import com.github.thomas_p.popularmoviesapp.models.MovieTrailer;

import java.util.ArrayList;

/**
 *
 */
public class MovieTrailerListAdapter extends RecyclerView.Adapter<MovieTrailerListAdapter.MovieTrailerListAdapterViewHolder> {
    private final MovieTrailerListAdapter.onClickListener clickListener;
    private ArrayList<MovieTrailer> movieTrailers = null;

    public MovieTrailerListAdapter(MovieTrailerListAdapter.onClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public MovieTrailerListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View contentView = inflater.inflate(R.layout.item_trailer, parent, false);
        return new MovieTrailerListAdapterViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(MovieTrailerListAdapterViewHolder holder, int position) {
        holder.itemView.setOnClickListener(holder);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return movieTrailers != null ? movieTrailers.size() : 0;
    }

    public void setMovieTrailerList(ArrayList<MovieTrailer> movieTrailerList) {
        this.movieTrailers = movieTrailerList;
        notifyDataSetChanged();
    }

    public interface onClickListener {
        void onTrailerClick(MovieTrailer movieTrailer);
    }

    /**
     *
     */
    class MovieTrailerListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mTrailerText;


        public MovieTrailerListAdapterViewHolder(View itemView) {
            super(itemView);
            mTrailerText = (TextView) itemView.findViewById(R.id.review_author_label);
        }

        void bind(final int trailerIndex) {
            if (movieTrailers != null) {
                final MovieTrailer movieTrailer = movieTrailers.get(trailerIndex);
                mTrailerText.setText(movieTrailer != null ? movieTrailer.getName() : "");
            }
        }

        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            // @// DONE: 27.02.2017 handle click events
            MovieTrailer movieTrailer = movieTrailers.get(position);
            clickListener.onTrailerClick(movieTrailer);
        }
    }
}
