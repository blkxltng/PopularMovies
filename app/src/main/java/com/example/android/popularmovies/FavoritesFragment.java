package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by firej on 6/29/2016.
 */
public class FavoritesFragment extends Fragment {

    ArrayList<Movies> mMovies = new ArrayList<>();
    DatabaseMaker mDatabaseMaker;
    FavoritesAdapter mFavoritesAdapter;

    String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w500";
    GridView gridView;

    OnMovieFavoriteSelectedListener mOnMovieFavoriteSelectedListener;

    //unchanging credentials
    String API_KEY = "[Insert Key Here]";

    public FavoritesFragment() {
    }

    public interface OnMovieFavoriteSelectedListener {
        public void movieFavoriteSelected(String mTitle, String mPosterUrl, String mDate, String mRating,
                                  String mOverview, String mId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mOnMovieFavoriteSelectedListener = (OnMovieFavoriteSelectedListener) context;
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
        mOnMovieFavoriteSelectedListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabaseMaker = new DatabaseMaker(getContext());
        mMovies = mDatabaseMaker.retrieveFavoriteMovies();
        mFavoritesAdapter = new FavoritesAdapter(getContext(), mMovies);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("Your Favorites");
        gridView = (GridView) rootView.findViewById(R.id.gridView_poster);

        //Log.v("LOG_TAG", "Size: " + mMovies.size());

        gridView.setAdapter(mFavoritesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movies currentMovie = mMovies.get(position);

                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("title", currentMovie.getTitle())
                        .putExtra("posterUrl", currentMovie.getPosterUrl())
                        .putExtra("releaseDate", currentMovie.getDate())
                        .putExtra("rating", currentMovie.getRatings())
                        .putExtra("description", currentMovie.getOverview())
                        .putExtra("id", currentMovie.getId());
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

}
