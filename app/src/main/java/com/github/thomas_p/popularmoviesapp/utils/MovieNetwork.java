package com.github.thomas_p.popularmoviesapp.utils;

import android.net.Uri;
import android.util.Log;

import com.github.thomas_p.popularmoviesapp.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;


public class MovieNetwork {
    public static enum LIST_MODES {POPULAR, TOP_RATED};

    private static final String TAG = MovieNetwork.class.getSimpleName();

    private static final String STATIC_MOVIE_URL =
            "https://api.themoviedb.org/3/movie";

    private static final String POPULAR_MOVIES_LINK = "popular";

    private static final String TOP_RATED_URL_LINK = "top_rated";

    private final String API_KEY;

    public MovieNetwork(String apiKey) {
        API_KEY = apiKey;
    }

    /**
     * Helper method which create a url from the path and the page
     * @param path string of the query path
     * @param page page number
     * @return an url object
     */
    private URL buildUrl(String path, int page) {
        if (page < 1) {
            page = 1;
        }

        Uri builtUri = Uri.parse(STATIC_MOVIE_URL)
                .buildUpon()
                .appendPath(path)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("page", Integer.toString(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
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
     * Extract the values of a json object and create a movie object from this
     * @param json json object for a movie
     * @return a Movie object
     * @throws JSONException
     */
    private Movie fromJson(JSONObject json) throws JSONException, ParseException {
        String title = json.getString("title");
        int id = json.getInt("id");
        String poster_path = json.getString("poster_path");

        // create movie object
        Movie resultMovie = new Movie(id, title, poster_path);
        // @todo extract other json
        resultMovie.setOverview(json.getString("overview"));
        String date = json.getString("release_date");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        resultMovie.setReleaseDate(dateFormat.parse(date));
        resultMovie.setVoteAverage(json.getDouble("vote_average"));
        return resultMovie;
    }


    /**
     * extract the movie list from the json object
     * @param json json object
     * @return movie list
     * @throws JSONException
     */
    private Movie[] getResultList(JSONObject json) throws JSONException {
        JSONArray jResultArray = json.getJSONArray("results");

        Movie[] resultMovies = new Movie[jResultArray.length()];
        for (int i = 0; i < jResultArray.length(); i++) {
            //
            JSONObject jsonMovieObject = jResultArray.getJSONObject(i);
            try {
                resultMovies[i] = fromJson(jsonMovieObject);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return resultMovies;
    }

    /**
     * extract the meta data and store them into the class object
     * @param json root json object
     * @throws JSONException
     */
    private void extractMetaData(JSONObject json) throws JSONException {
        //actPage = json.getInt("page");
        //maxItems = json.getInt("total_results");
        //maxPages = json.getInt("total_pages");
    }

    /**
     *
     * @param mode
     * @param page
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public Movie[] loadPage(LIST_MODES mode, int page) throws IOException, JSONException {
        URL url = null;
        // two modes are possible
        switch (mode) {
            case POPULAR:
                url = buildUrl(POPULAR_MOVIES_LINK, page);
                break;
            case TOP_RATED:
                url = buildUrl(TOP_RATED_URL_LINK, page);
        }
        // if no url given return null
        if (url == null) {
            return null;
        }
        // call from the server
        String response = getResponseFromHttpUrl(url);
        JSONObject jsonObject = new JSONObject(response);
        // extract meta data
        extractMetaData(jsonObject);
        // extract result

        return getResultList(jsonObject);
    }

}
