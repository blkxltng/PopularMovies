package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.OnMovieSelectedListener,
                                                                DetailsFragment.OnMovieFavoritedListener,
                                                                FavoritesFragment.OnMovieFavoriteSelectedListener {

    MainActivityFragment mainfragment;
    DetailsFragment detailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {

            mainfragment = new MainActivityFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mainfragment, "MainFrag")
                    .commit();
        } else {
            mainfragment = (MainActivityFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, "MainFrag");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "MainFrag", mainfragment);
    }

    @Override
    public void movieSelected(String mTitle, String mPosterUrl, String mDate, String mRating, String mOverview, String mId) {

        detailsFragment = (DetailsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_details);

        if(detailsFragment != null) {
            detailsFragment.updateMovies(mTitle, mPosterUrl, mDate, mRating, mOverview, mId);
        } else {
            Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class)
                        .putExtra("title", mTitle)
                        .putExtra("posterUrl", mPosterUrl)
                        .putExtra("releaseDate", mDate)
                        .putExtra("rating", mRating)
                        .putExtra("description", mOverview)
                        .putExtra("id", mId);
                startActivity(detailIntent);
        }
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

    @Override
    public void movieFavoriteSelected(String mTitle, String mPosterUrl, String mDate, String mRating,
                                      String mOverview, String mId) {

        DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_details);

        if(detailsFragment != null) {
            detailsFragment.updateMovies(mTitle, mPosterUrl, mDate, mRating, mOverview, mId);
        } else {
            Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class)
                    .putExtra("title", mTitle)
                    .putExtra("posterUrl", mPosterUrl)
                    .putExtra("releaseDate", mDate)
                    .putExtra("rating", mRating)
                    .putExtra("description", mOverview)
                    .putExtra("id", mId);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_favorites) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new FavoritesFragment())
                    .addToBackStack("FavoritesFragment")
                    .commit();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getApplication(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
