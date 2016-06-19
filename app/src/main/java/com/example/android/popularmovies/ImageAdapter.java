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
 * Created by firej on 6/17/2016.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mPosterImages;
    LayoutInflater inflater;

    public ImageAdapter(Context c, ArrayList<String> posterImages){
        mContext = c;
        mPosterImages = posterImages;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class Holder {
        ImageView mImageView;
    }
    @Override
    public Object getItem(int i) {
        return mPosterImages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final int num = i;

        Holder holder = new Holder();

        View griddleView;
        griddleView = inflater.inflate(R.layout.list_item_poster, null);
        holder.mImageView = (ImageView) griddleView.findViewById(R.id.list_item_poster_imageview);

        //Picasso.with(mContext).setLoggingEnabled(true);
        Picasso.with(mContext)
                .load(mPosterImages.get(i))
                .into(holder.mImageView);

        return griddleView;
    }

    @Override
    public int getCount() {
        return mPosterImages.size();
    }
}