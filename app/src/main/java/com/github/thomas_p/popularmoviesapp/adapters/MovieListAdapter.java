package com.github.thomas_p.popularmoviesapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.thomas_p.popularmoviesapp.R;
import com.github.thomas_p.popularmoviesapp.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;



public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListAdapterViewHolder> {

    private ArrayList<Movie> movieListData = new ArrayList<>();
    private MovieListItemCLickListener movieListItemCLickListener;
    public MovieListAdapter(MovieListItemCLickListener cLickListener) {
        movieListItemCLickListener = cLickListener;
    }

    @Override
    public MovieListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contentView = inflater.inflate(R.layout.item_movie_list, parent, false);
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

    public void clear() {
        movieListData.clear();
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getMovieListData() {
        return movieListData;
    }

    public void setMovieListData(ArrayList<Movie> data) {
        movieListData = data;
        notifyDataSetChanged();
    }

    public void setMovieListData(Movie[] movies) {
        movieListData = new ArrayList<>();
        Collections.addAll(movieListData, movies);
        notifyDataSetChanged();
    }

    /**
     * append movies
     * @param movies an array of movies, which will be appended
     */
    public void appendMovieData(@NonNull Movie[] movies) {
        if (movieListData == null) {
            setMovieListData(movies);
            return;
        }
        final int size = getItemCount();
        Collections.addAll(movieListData, movies);
        //notifyItemRangeInserted(size-movies.length, movies.length);
        notifyDataSetChanged();

    }

    public interface MovieListItemCLickListener {
        void onClick(Movie movie);
    }

    /**
     * Holder class
     */
    class MovieListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mMovieImage;
        TextView mMovieTitle;
        View mItemView;

        MovieListAdapterViewHolder(View itemView) {
            super(itemView);
            mMovieImage = (ImageView) itemView.findViewById(R.id.movie_list_image);
            mMovieTitle = (TextView) itemView.findViewById(R.id.movie_list_film_title);
            mItemView = itemView;
        }

        /**
         * put the image into the context
         * @param movieIndex index of the context
         */
        void bind(int movieIndex) {
            if (movieListData != null) {
                final String path = movieListData.get(movieIndex).getPathPosterImage();
                final String title = movieListData.get(movieIndex).getTitle();
                Picasso
                        .with(mItemView.getContext())
                        .load(path)
                        .error(R.drawable.no_internet)
                        .into(mMovieImage);
                mMovieTitle.setText(title);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            movieListItemCLickListener.onClick(movieListData.get(position));
        }
    }
}
