package com.example.project1.Gallery;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.project1.MainActivity;
import com.example.project1.MyApplication;
import com.example.project1.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.project1.MyApplication.getAppContext;

public class Gallery_Fragment extends Fragment {

    MyApplication app;
    SwipeRefreshLayout swl;
    MyAdapter mAdapter;
    //private List<ML_Image_Objec  t> img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (MyApplication) getAppContext();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_,container,false);
        mAdapter = new MyAdapter(R.layout.row,app);

        final GridView gridView = (GridView)view.findViewById(R.id.myGrid);
        gridView.setAdapter(new MyAdapter(R.layout.row,app));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShowDialogBox(position);
            }
        });

        swl = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshGL);
        swl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                app.getNames();
                app.updateNames();

                //MyAdapter nAdapter = new MyAdapter(R.layout.row,app);
                mAdapter.notifyDataSetChanged();
                gridView.invalidateViews();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run(){
                        swl.setRefreshing(false);
                    }
                },300);

            }
        });

        gridView.setOnScrollListener(new GridView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int ScrollState){
                //super(gv.onScrollSt);
            }
            @Override
            public void onScroll(AbsListView view,int firstVisibleItem,int ic,int vc){
                if(gridView.getChildAt(0) != null){
                    swl.setEnabled(gridView.getFirstVisiblePosition() == 0 && gridView.getChildAt(0).getTop()==0);
                }
            }
        });

        return view;

    }

    private void ShowDialogBox(final int i)
    {
        final Dialog dialog = new Dialog(getActivity());
        List<String> names = app.getNames();
        dialog.setContentView(R.layout.custom_dialog);

        //Getting custom dialog views
        TextView Image_name = dialog.findViewById(R.id.txt_Image_name);
        ImageView Image = dialog.findViewById(R.id.img);
        Button btn_Full = dialog.findViewById(R.id.btn_full);
        Button btn_Close = dialog.findViewById(R.id.btn_close);

        Log.d("ImDialog",app.getURL() +"image/"+ names.get(i));
        Glide.with(getAppContext())
                .load(app.getURL() +"image/"+ names.get(i))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(Image);


        //String path = img.getPath();

        final String name = names.get(i);
        Image_name.setText(name);
        //File f1 = new File(img.getPath());
        //Glide
        //        .with(getAppContext())
        //        .load(Uri.fromFile(f1))
        //        .into(Image);
        //Image.setImageBitmap();
        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(getActivity(), FullView.class);
                //i.putExtra("img_id",img.getPath());
                //i.putExtra("filename",name);
                //startActivity(i);
            }
        });



        btn_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_Full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent i = new Intent(getActivity(), FullView.class);
                //i.putExtra("img_id",img.getPath());
                //i.putExtra("filename",name);
                //startActivity(i);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}

