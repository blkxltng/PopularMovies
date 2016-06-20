package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public ArrayList<String> posterImages = new ArrayList<>();
    public ArrayList<String> movieTitles = new ArrayList<>();
    public ArrayList<String> movieDates = new ArrayList<>();
    public ArrayList<String> movieRatings = new ArrayList<>();
    public ArrayList<String> movieDescriptions = new ArrayList<>();

    String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w500";
    String popOrTop = "popular";
    GridView gridView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridView_poster);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("LOG_TAG", "Title: " + movieTitles.get(position).toString());
                Log.v("LOG_TAG", "Release Date: " + movieDates.get(position).toString());
                Log.v("LOG_TAG", "Rating: " + movieRatings.get(position).toString());
                Log.v("LOG_TAG", "Description: " + movieDescriptions.get(position).toString());
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("title", movieTitles.get(position).toString())
                        .putExtra("posterUrl", posterImages.get(position).toString())
                        .putExtra("releaseDate", movieDates.get(position).toString())
                        .putExtra("rating", movieRatings.get(position).toString())
                        .putExtra("description", movieDescriptions.get(position).toString());
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    private void updateMovies() {
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }


    public class FetchMovieTask  extends AsyncTask<String, Void, ArrayList> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private String sortType() {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortType  = sharedPrefs.getString(
                    getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_popular));

            //Check if the user wants the temperature in celsius or fahrenheit
            if(sortType.equals(getString(R.string.pref_sort_topRated))) {
                popOrTop = "top_rated";
            } else {
                popOrTop = "popular";
            }

            return popOrTop;
        }

        public ArrayList<String> getMovieDataFromJson(String movieJsonStr) throws JSONException {

            posterImages.clear();
            movieTitles.clear();
            movieDates.clear();
            movieRatings.clear();
            movieDescriptions.clear();

            //Places we need to look
            final String OWM_RESULTS = "results";
            final String OWM_SYNOPSIS = "overview";
            final String OWM_RELEASEDATE = "release_date";
            final String OWM_POSTER = "poster_path";
            final String OWM_TITLE = "original_title";
            final String OWM_RATING = "vote_average";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);

            for(int i = 0; i < movieArray.length(); i++) {
                String movieTitle;
                String movieReleaseDate;
                String movieRating;
                String movieDescription;
                String moviePoster;

                JSONObject currentMovie = movieArray.getJSONObject(i);

                //Movie name is in child titled "orignal_title". Get it.
                movieTitle = currentMovie.getString(OWM_TITLE);
                movieReleaseDate = currentMovie.getString(OWM_RELEASEDATE);
                movieRating = currentMovie.getString(OWM_RATING);
                movieDescription = currentMovie.getString(OWM_SYNOPSIS);
                moviePoster = currentMovie.getString(OWM_POSTER);

                posterImages.add(POSTER_BASE_URL + moviePoster);
                movieTitles.add(movieTitle);
                movieDates.add(movieReleaseDate);
                movieRatings.add(movieRating);
                movieDescriptions.add(movieDescription);

            }

            return posterImages;
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Will contain the raw JSON as a string
            String movieJsonStr = null;

            //unchanging credentials
            String API_KEY = "[Insert Key Here]";

            try {
                final String API_KEY_PARAM = "api_key";
                //Testing
                Uri.Builder uri = new Uri.Builder();
                uri.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(sortType())
                        .appendQueryParameter(API_KEY_PARAM, API_KEY);
                String myURL = uri.toString();

                URL url = new URL(myURL);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("LOG_TAG", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                movieJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("LOG_TAG", "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList strings) {
            super.onPostExecute(strings);
            gridView.setAdapter(new ImageAdapter(getContext(), posterImages));
        }
    }



}
