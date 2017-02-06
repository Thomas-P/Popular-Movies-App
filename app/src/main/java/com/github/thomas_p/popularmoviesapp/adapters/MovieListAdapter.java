package com.github.thomas_p.popularmoviesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.github.thomas_p.popularmoviesapp.R;
import com.github.thomas_p.popularmoviesapp.models.Movie;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;


public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListAdapterViewHolder> {

    public interface MovieListItemCLickListener {
        void onClick(Movie movie);
    }

    private ArrayList<Movie> movieListData = new ArrayList<>();
    private MovieListItemCLickListener movieListItemCLickListener;

    public MovieListAdapter(MovieListItemCLickListener cLickListener) {
        movieListItemCLickListener = cLickListener;
    }


    @Override
    public MovieListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contentView = inflater.inflate(R.layout.movie_list_item, parent, false);
        return new MovieListAdapterViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(MovieListAdapterViewHolder holder, int position) {
        holder.itemView.setOnClickListener(holder);
        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return movieListData.size();
    }

    /**
     * set up movie data
     * @param movies an array of movies
     */
    public void setMovieData(Movie[] movies) {
        movieListData.clear();
        Collections.addAll(movieListData, movies);
        notifyDataSetChanged();
    }

    /**
     * append movies
     * @param movies an array of movies, which will be appended
     */
    public void appendMovieData(Movie[] movies) {
        Collections.addAll(movieListData, movies);
        notifyItemRangeInserted(getItemCount()-movies.length, movies.length);
    }

    /**
     * Holder class
     */
    class MovieListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mMovieImage;
        View mItemView;

        MovieListAdapterViewHolder(View itemView) {
            super(itemView);
            mMovieImage = (ImageView) itemView.findViewById(R.id.movie_list_image);
            mItemView = itemView;
        }

        /**
         * put the image into the context
         * @param movieIndex index of the context
         */
        void bind(int movieIndex) {
            if (movieListData != null) {
                String path = movieListData.get(movieIndex).getPoster();
                Picasso.with(mItemView.getContext()).load(path).into(mMovieImage);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            movieListItemCLickListener.onClick(movieListData.get(position));
        }
    }
}
