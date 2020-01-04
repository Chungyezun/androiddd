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
import com.example.project1.MLthings.ML_Image_Object;
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

    //private List<ML_Image_Object> img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (MyApplication) getAppContext();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_,container,false);


        final GridView gridView = (GridView)view.findViewById(R.id.myGrid);
        gridView.setAdapter(new MyAdapter(R.layout.row,app));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // ML_Image_Object item_pos = img.get(position);
               // ShowDialogBox(item_pos);

            }
        });

        swl = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshGL);
        swl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                app.getNames();

                MyAdapter nAdapter = new MyAdapter(R.layout.row,app);
                gridView.setAdapter(nAdapter);
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

    private void ShowDialogBox(final ML_Image_Object img)
    {
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.custom_dialog);

        //Getting custom dialog views
        TextView Image_name = dialog.findViewById(R.id.txt_Image_name);
        ImageView Image = dialog.findViewById(R.id.img);
        Button btn_Full = dialog.findViewById(R.id.btn_full);
        Button btn_Close = dialog.findViewById(R.id.btn_close);

        String path = img.getPath();
        String title = path.substring(path.lastIndexOf("/")+1);

        //extracting name

        int index = title.indexOf("/");
        final String name = title.substring(index+1,title.length());
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

