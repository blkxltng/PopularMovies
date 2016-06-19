package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment {

    public static final String LOG_TAG = DetailsFragment.class.getSimpleName();

    private Context mContext;
    private String mTitle;
    private String mPosterUrl;
    private String mDate;
    private String mRating;
    private String mDescription;

    public DetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra("title")) {
            mTitle = intent.getStringExtra("title");
            mPosterUrl = intent.getStringExtra("posterUrl");
            mRating = "Rating: " + intent.getStringExtra("rating");
            mDate = "Release Date: " + intent.getStringExtra("releaseDate");
            mDescription = intent.getStringExtra("description");
            ((TextView) rootView.findViewById(R.id.details_title)).setText(mTitle);
            ImageView posterView = (ImageView) rootView.findViewById(R.id.details_poster);
            Picasso.with(mContext).load(mPosterUrl).into(posterView);
            ((TextView) rootView.findViewById(R.id.details_rating)).setText(mRating);
            ((TextView) rootView.findViewById(R.id.details_releasedate)).setText(mDate);
            ((TextView) rootView.findViewById(R.id.details_description)).setText(mDescription);
        }

        return rootView;
    }
}
