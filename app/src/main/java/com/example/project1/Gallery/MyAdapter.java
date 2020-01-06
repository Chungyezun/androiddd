package com.example.project1.Gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.icu.text.Transliterator;
import android.net.Uri;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.project1.MyApplication;
import com.example.project1.R;

import java.io.File;
import java.util.List;

public class MyAdapter extends BaseAdapter
{
    private Context mContext;
    private MyApplication app;
    private int layout;
    LayoutInflater inf;
    String url = "http://b5adc0ac.ngrok.io/image";


    public MyAdapter(int layout, Context mContext)
    {
        this.mContext = mContext;
        this.layout = layout;
        this.app = (MyApplication) mContext.getApplicationContext();
        this.inf = (LayoutInflater) mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        if(app.getNames() == null){
            return 0;
        }
        return app.getNames().size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)convertView = inf.inflate(layout, parent, false);
        ImageView imageView = convertView.findViewById(R.id.imageView1);

        List<String> imnames = app.getNames();
        Log.e("SERVER_REQUEST",url + imnames.get(position));
        Glide.with(mContext)
            .load(url +'/'+ imnames.get(position))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        convertView.getLayoutParams().height = convertView.getLayoutParams().width;
        //imageView.setImageResource(mThumbIds.get(position));
        return convertView;

    }
}