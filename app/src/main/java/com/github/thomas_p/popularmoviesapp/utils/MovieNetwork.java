package com.github.thomas_p.popularmoviesapp.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;



public class MovieNetwork {
    private static final String TAG = MovieNetwork.class.getSimpleName();
    ;
    private static final String STATIC_MOVIE_URL =
            "https://api.themoviedb.org/3/movie";
    private static final String POPULAR_MOVIES_LINK = "popular";
    private static final String TOP_RATED_URL_LINK = "top_rated";

    private static final String REVIEW_SUB_DIR = "reviews";
    private static final String TRAILER_SUB_DIR = "videos";

    private final String API_KEY;



    public MovieNetwork(String apiKey) {
        API_KEY = apiKey;
    }

    /**
     * This method returns the entire results from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Helper method which create a url from the path and the page
     *
     * @param path string of the query path
     * @param page page number
     * @return an url object
     */
    public URL buildListUrl(String path, int page) {
        if (page < 1) {
            page = 1;
        }

        Uri buildUri = Uri.parse(STATIC_MOVIE_URL)
                .buildUpon()
                .appendPath(path)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("page", Integer.toString(page))
                .build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public URL buildDetailUrl(final int id, final String subDir) {
        Uri buildUri = Uri.parse(STATIC_MOVIE_URL)
                .buildUpon()
                .appendPath(String.valueOf(id))
                .appendQueryParameter("api_key", API_KEY)
                .build();
        if (subDir != null) {
            buildUri = buildUri
                    .buildUpon()
                    .appendPath(subDir)
                    .build();
        }

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    /**
     * load a movie list
     *
     * @param mode
     * @param page
     * @return
     */
    public String getMovieListResult(final MovieListMode.LIST_MODES mode, final int page) {
        String path;
        switch (mode) {
            case POPULAR:
                path = POPULAR_MOVIES_LINK;
                break;
            case TOP_RATED:
                path = TOP_RATED_URL_LINK;
                break;
            default:
                throw new RuntimeException("Is not implemented.");
        }
        final URL requestUrl = buildListUrl(path, page);
        try {
            return getResponseFromHttpUrl(requestUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getDetailResult(final int id, final String subDir) {
        final URL requestUrl = buildDetailUrl(id, subDir);
        try {
            return getResponseFromHttpUrl(requestUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public String getMovieDetailResult(final int id) {
        return getDetailResult(id, null);
    }

    public String getTrailerResult(final int id) {
        return getDetailResult(id, TRAILER_SUB_DIR);
    }

    public String getReviewResult(final int id) {
        return getDetailResult(id, REVIEW_SUB_DIR);
    }

}
