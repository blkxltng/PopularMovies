package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
public class DetailsFragment extends Fragment {

    public static final String LOG_TAG = DetailsFragment.class.getSimpleName();

    ArrayAdapter<String> mReviewAdapter;
    ArrayAdapter<String> mTrailerAdapter;

    DatabaseMaker dbMaker;

    public ArrayList<String> trailerKeys = new ArrayList<>();
    public ArrayList<String> trailerNames = new ArrayList<>();
    public ArrayList<String> reviews = new ArrayList<>();
    //public ArrayList<String> reviewContents = new ArrayList<>();

    private Context mContext;
    private String mTitle;
    private String mPosterUrl;
    private String mDate;
    private String mRating;
    private String mDescription;
    private String mId;

    TextView textTitle, textDate, textRating, textDescription;
    ImageView posterView;

    String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    String API_KEY = "[Insert Key Here]";

    ListView reviewList;
    ListView trailerList;

    OnMovieFavoritedListener mOnMovieFavoritedListener;

    boolean favorited = false;

    public DetailsFragment() {
    }

    public interface OnMovieFavoritedListener {
        public void movieFavorited();
    }

    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mOnMovieFavoritedListener = (OnMovieFavoritedListener) context;
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
        mOnMovieFavoritedListener = null;
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//
//        outState.putString("title", mTitle);
//        outState.putString("url", mPosterUrl);
//        outState.putString("date", mDate);
//        outState.putString("rating", mRating);
//        outState.putString("description", mDescription);
//        outState.putString("id", mId);
//
//        super.onSaveInstanceState(outState);
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dbMaker = new DatabaseMaker(getContext());

        mTrailerAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_trailers,
                R.id.list_item_trailers_textview,
                trailerNames);

        mReviewAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_reviews,
                R.id.list_item_reviews_textview,
                reviews);

        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);

        textTitle = (TextView) rootView.findViewById(R.id.details_title);
        posterView = (ImageView) rootView.findViewById(R.id.details_poster);
        textRating = (TextView) rootView.findViewById(R.id.details_rating);
        textDate = (TextView) rootView.findViewById(R.id.details_releasedate);
        textDescription = (TextView) rootView.findViewById(R.id.details_description);

        trailerList = (ListView) rootView.findViewById(R.id.details_trailer_list);
        reviewList = (ListView) rootView.findViewById(R.id.details_review_list);

        trailerList.setAdapter(mTrailerAdapter);
        reviewList.setAdapter(mReviewAdapter);

        final FloatingActionButton buttonFavorites = (FloatingActionButton)
                rootView.findViewById(R.id.details_favorite_fab);

        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra("title")) {
            mTitle = intent.getStringExtra("title");
            mPosterUrl = intent.getStringExtra("posterUrl");
            mRating = "Rating: " + intent.getStringExtra("rating") + "/10";
            mDate = "Release Date: " + intent.getStringExtra("releaseDate");
            mDescription = intent.getStringExtra("description");
            mId = intent.getStringExtra("id");


            textTitle.setText(mTitle);


            Picasso.with(mContext).load(mPosterUrl).into(posterView);


            textRating.setText(mRating);


            textDate.setText(mDate);


            textDescription.setText(mDescription);

            updateDetails();
        }

        trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailerKeys.get(position))));
            }
        });

        if(!dbMaker.isFavorite(mId)) {
            buttonFavorites.setImageResource(R.drawable.ic_favorite_border_white_48dp);
        } else if (dbMaker.isFavorite(mId)) {
            buttonFavorites.setImageResource(R.drawable.ic_favorite_white_48dp);
        }

        buttonFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dbMaker.isFavorite(mId)){
                    buttonFavorites.setImageResource(R.drawable.ic_favorite_white_48dp);
                    dbMaker.addFavorite(mPosterUrl, mDescription, mDate, mId, mTitle, mRating);
                    Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                    favorited = !favorited;
                } else if (dbMaker.isFavorite(mId)){
                    buttonFavorites.setImageResource(R.drawable.ic_favorite_border_white_48dp);
                    dbMaker.deleteFavorite(mId);
                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                    favorited = !favorited;
                }

            }
        });

        onSaveInstanceState(savedInstanceState);

        return rootView;
    }

    private void updateDetails() {
        FetchReviewsTask reviewsTask = new FetchReviewsTask();
        FetchTrailersTask trailersTask = new FetchTrailersTask();
        reviewsTask.execute();
        trailersTask.execute();
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, ArrayList<String>> {

        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

        public ArrayList<String> getMovieDataFromJson(String movieJsonStr) throws JSONException {

            reviews.clear();

            //Places we need to look
            final String OWM_RESULTS = "results";
            final String OWM_REVIEWAUTHOR = "author";
            final String OWM_REVIEWCONTENT = "content";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);

            for (int i = 0; i < movieArray.length(); i++) {
                String reviewAuthor;
                String reviewContent;

                JSONObject currentMovie = movieArray.getJSONObject(i);

                //Movie name is in child titled "orignal_title". Get it.
                reviewAuthor = currentMovie.getString(OWM_REVIEWAUTHOR);
                reviewContent = currentMovie.getString(OWM_REVIEWCONTENT);

                reviews.add("Author: " + reviewAuthor + "\n" + reviewContent);
            }

            return reviews;
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
                        .appendPath(mId)
                        .appendPath("reviews")
                        .appendQueryParameter(API_KEY_PARAM, API_KEY);
                String myURL = uri.toString();

                URL url = new URL(myURL);

                Log.v("LOG_TAG", "URL: " + myURL);

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
        protected void onPostExecute(ArrayList<String> arrayList) {
            super.onPostExecute(arrayList);

            Log.v("LOG_TAG", "reviews: " + reviews.size());

//            reviews.clear();
//            reviews.addAll(arrayList);

            for(int i = 0; i < reviews.size(); i++) {
                Log.v(LOG_TAG, "Review " + i + ": \n" + reviews.get(i).toString());
            }

            mReviewAdapter.notifyDataSetChanged();
            ListUtils.setDynamicHeight(reviewList);
        }
    }

    public class FetchTrailersTask  extends AsyncTask<String, Void, ArrayList<String>> {

        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        public ArrayList<String> getMovieDataFromJson(String movieJsonStr) throws JSONException {

            trailerKeys.clear();
            trailerNames.clear();

            //Places we need to look
            final String OWM_RESULTS = "results";
            final String OWM_TRAILERKEY = "key";
            final String OWM_TRAILERSOURCE = "source";
            final String OWM_TRAILERNAME = "name";
            //final String OWM_TRAILERSITE = "site";
            final String OWM_YOUTUBE = "youtube";

            JSONArray movieArray;

            JSONObject movieJson = new JSONObject(movieJsonStr);

            //Log.v(LOG_TAG, "empty or no?: " + (movieJsonStr.getJSONArray(OWM_RESULTS) == null));

            if(!movieJson.isNull(OWM_RESULTS)) {
                movieArray = movieJson.getJSONArray(OWM_RESULTS);
            } else {
                movieArray = movieJson.getJSONArray(OWM_YOUTUBE);
            }

            for (int i = 0; i < movieArray.length(); i++) {
                String trailerKey;
                String trailerName;
                //String trailerSite;

                JSONObject currentMovie = movieArray.getJSONObject(i);

                //Movie name is in child titled "orignal_title". Get it.
                if(!currentMovie.isNull(OWM_TRAILERKEY)) {
                    trailerKey = currentMovie.getString(OWM_TRAILERKEY);
                } else {
                    trailerKey = currentMovie.getString(OWM_TRAILERSOURCE);
                }

                trailerName = currentMovie.getString(OWM_TRAILERNAME);
                //trailerSite = currentMovie.getString(OWM_TRAILERSITE);

                trailerKeys.add(YOUTUBE_BASE_URL + trailerKey);
                Log.v(LOG_TAG, "Trailer Links: " + trailerKeys.get(i).toString());
                trailerNames.add(trailerName);
            }

            return trailerNames;
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
                        .appendPath(mId)
                        .appendPath("trailers")
                        .appendQueryParameter(API_KEY_PARAM, API_KEY);
                String myURL = uri.toString();

                URL url = new URL(myURL);

                Log.v("LOG_TAG", "URL: " + myURL);


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
        protected void onPostExecute(ArrayList<String> arrayList) {
            super.onPostExecute(arrayList);

            Log.v("LOG_TAG", "trailers: " + trailerKeys.size());

            for(int i = 0; i < trailerKeys.size(); i++) {
                Log.v(LOG_TAG, trailerNames.get(i).toString() + " " + trailerKeys.get(i).toString());
            }

            mTrailerAdapter.notifyDataSetChanged();
            ListUtils.setDynamicHeight(trailerList);
        }
    }

    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = MeasureSpec.makeMeasureSpec(mListView.getWidth(), MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }

    public void updateMovies(String mTitles, String mPosterUrls, String mDates, String mRatings,
                             String mOverviews, String mIds) {
        mTitle = mTitles;
        mId = mIds;
        mPosterUrl = mPosterUrls;
        mDate = mDates;
        mRating = mRatings;
        mDescription = mOverviews;
        textTitle.setText(mTitle);
        textDate.setText(mDate);
        textRating.setText(mRating);
        textDescription.setText(mDescription);
        Picasso.with(mContext).load(mPosterUrl).into(posterView);
        updateDetails();
    }
}
