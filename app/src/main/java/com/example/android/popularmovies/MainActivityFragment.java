package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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

//    ArrayList<Movies> mMovies = new ArrayList<>();
//    FavoritesAdapter mImageAdapter;

    public ArrayList<String> posterImages = new ArrayList<>();
    public ArrayList<String> movieTitles = new ArrayList<>();
    public ArrayList<String> movieDates = new ArrayList<>();
    public ArrayList<String> movieRatings = new ArrayList<>();
    public ArrayList<String> movieDescriptions = new ArrayList<>();
    public ArrayList<String> movieIds = new ArrayList<>();

    OnMovieSelectedListener mOnMovieSelectedListener;

    String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w500";
    String popOrTop = "popular";
    GridView gridView;

    //unchanging credentials
    String API_KEY = "[Insert Key Here]";

    public MainActivityFragment() {
    }

    public interface OnMovieSelectedListener {
        public void movieSelected(String mTitle, String mPosterUrl, String mDate, String mRating,
                                  String mOverview, String mId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mOnMovieSelectedListener = (OnMovieSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnMovieSelectedListener");
        }
    }

    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after {@link #onDestroy()}.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mOnMovieSelectedListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isNetworkAvailable()) {
            updateMovies();
        } else {
            Toast.makeText(getContext(), R.string.no_internet,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridView_poster);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("Popular Movies");

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mOnMovieSelectedListener.movieSelected(movieTitles.get(position).toString(),
                        posterImages.get(position).toString(), movieDates.get(position).toString(),
                        movieRatings.get(position).toString(), movieDescriptions.get(position).toString(),
                        movieIds.get(position).toString());
            }
        });


        return rootView;
    }

    //Check for an internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void updateMovies() {
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();
    }

    public class FetchMovieTask  extends AsyncTask<String, Void, ArrayList> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private String sortType() {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortType  = sharedPrefs.getString(
                    getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_popular));

            //Check if the user wants the movie sorted by popularity or rating
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
            movieIds.clear();

            //Places we need to look
            final String OWM_RESULTS = "results";
            final String OWM_SYNOPSIS = "overview";
            final String OWM_RELEASEDATE = "release_date";
            final String OWM_POSTER = "poster_path";
            final String OWM_TITLE = "original_title";
            final String OWM_RATING = "vote_average";
            final String OWM_MOVIEID = "id";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);

            for(int i = 0; i < movieArray.length(); i++) {
                String movieTitle;
                String movieReleaseDate;
                String movieRating;
                String movieDescription;
                String moviePoster;
                String movieId;

                JSONObject currentMovie = movieArray.getJSONObject(i);

                Movies movies = new Movies();

                //Movie name is in child titled "orignal_title". Get it.
                movieTitle = currentMovie.getString(OWM_TITLE);
                movieReleaseDate = currentMovie.getString(OWM_RELEASEDATE);
                movieRating = currentMovie.getString(OWM_RATING);
                movieDescription = currentMovie.getString(OWM_SYNOPSIS);
                moviePoster = currentMovie.getString(OWM_POSTER);
                movieId = currentMovie.getString(OWM_MOVIEID);

//                movies.setId(currentMovie.getString(OWM_TITLE));
//                mMovies.add(movies);

                posterImages.add(POSTER_BASE_URL + moviePoster);
                movieTitles.add(movieTitle);
                movieDates.add(movieReleaseDate);
                movieRatings.add(movieRating);
                movieDescriptions.add(movieDescription);
                movieIds.add(movieId);

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

                // Create the request to OpenMovieDB, and open the connection
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
