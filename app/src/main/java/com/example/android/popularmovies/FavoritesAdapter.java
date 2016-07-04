package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by firej on 6/29/2016.
 */
public class FavoritesAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Movies> mMovies;
    //LayoutInflater inflater;

    public FavoritesAdapter(Context c, ArrayList<Movies> posterImages){
        mContext = c;
        mMovies = posterImages;
    }

    @Override
    public Object getItem(int i) {
        return mMovies.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Holds the view references
        Holder holder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_poster, parent, false);
            holder = new Holder();
            holder.mImageView = (ImageView) convertView.findViewById(R.id.list_item_poster_imageview);
            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }
        if (mMovies.get(position) != null)
            Picasso.with(mContext).load(mMovies.get(position).getPosterUrl()).into(holder.mImageView);

        return convertView;
    }

    public class Holder {
        ImageView mImageView;
    }

    @Override
    public int getCount() {
        return mMovies.size();
    }
}
