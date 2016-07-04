package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity implements DetailsFragment.OnMovieFavoritedListener{

    DetailsFragment detailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState == null) {

            detailsFragment = new DetailsFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, detailsFragment, "DetailFrag")
                    .commit();
        } else {
            detailsFragment = (DetailsFragment)getSupportFragmentManager().getFragment(savedInstanceState, "DetailFrag");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "DetailFrag", detailsFragment);
    }

    @Override
    public void movieFavorited() {
        FavoritesFragment mag_favoriteMoviesFragment = (FavoritesFragment) getSupportFragmentManager()
                .findFragmentByTag("FavFrag");

        if (mag_favoriteMoviesFragment != null && mag_favoriteMoviesFragment.isVisible()) {
            FavoritesFragment magFavoriteMoviesFragment = new FavoritesFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, magFavoriteMoviesFragment, "FavFrag")
                    .commit();
        }
    }

    boolean favorited = false;

}
