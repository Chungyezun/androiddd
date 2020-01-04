package com.example.project1.MLthings;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.project1.Gallery.FullView;
import com.example.project1.Gallery.MyAdapter;
import com.example.project1.MyApplication;
import com.example.project1.MLthings.Label_Image.LabelAll;

import com.example.project1.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.Inflater;

import static com.example.project1.MyApplication.getAppContext;

public class ML_Fragment extends Fragment {

    private MyApplication app;
    private List<ML_Image_Object> img;
    private MLAdapter adapter;
    private GridView gv;
    private SwipeRefreshLayout swl;

    private Set<String> selectionSet;

    public MLAdapter getAdapter(){
        return adapter;
    }

    ChipGroup chipGroup;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (MyApplication) getAppContext();
        selectionSet = new HashSet<>();

    }



    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ml_gallery,container,false);
        chipGroup = (ChipGroup)view.findViewById(R.id.labelChips);
        final View layout = view.findViewById(R.id.invLayout);


        img = null;

        //여기 이 List 에서, Label 이 되어있지 않은 친구들을 Label 시켜버리자!!!!
        //여기서 그냥 thread 를 만들어 관리할까.....

        adapter = new MLAdapter(
                getAppContext(),
                R.layout.mlrow,
                img);

        gv = (GridView)view.findViewById(R.id.gridViewML);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ML_Image_Object item_pos = (ML_Image_Object) adapter.getItem(position);
                ShowDialogBox(item_pos);

            }
        });
        //gv.setStretchMode(GridView.NO_STRETCH);

        class getdelegate implements Label_Image.AsyncDelegate{
            @Override
            public void asyncComplete(boolean success) {
                adapter.notifyDataSetChanged();
                gv.invalidateViews();
                loadLabelChips(inflater);
            }
        }
        final getdelegate as = new getdelegate();
        LabelAll labelAll = new LabelAll(as);
        labelAll.execute(img.toArray(new ML_Image_Object[0]));



        //Filter Action Start
        loadLabelChips(inflater);
        applyfilter(img,selectionSet,app.getCache());
        swl = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshML);
        swl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                img =null;
                adapter = new MLAdapter(
                        getAppContext(),
                        R.layout.mlrow,
                        img);
                gv.setAdapter(adapter);
                LabelAll nlabel = new LabelAll(as);
                nlabel.execute(img.toArray(new ML_Image_Object[0]));
                loadLabelChips(inflater);



                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run(){
                        swl.setRefreshing(false);
                    }
                },300);

            }
        });

        gv.setOnScrollListener(new GridView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int ScrollState){
                //super(gv.onScrollSt);
            }
            @Override
            public void onScroll(AbsListView view,int firstVisibleItem,int ic,int vc){
                if(gv.getChildAt(0) != null){
                    swl.setEnabled(gv.getFirstVisiblePosition() == 0 && gv.getChildAt(0).getTop()==0);
                }
            }
        });

        //Get to Unpressed State
        chipGroup.setVisibility(View.GONE);
        layout.setBackgroundColor(Color.WHITE);

        //Setting up Button Placement Constraints




        ConstraintSet csOFF = new ConstraintSet();


        ToggleButton toggle = (ToggleButton) view.findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //layout.setBackgroundColor(Color.TRANSPARENT);
                    chipGroup.setVisibility(View.VISIBLE);
                } else {
                    chipGroup.setVisibility(View.GONE);
                    //layout.setBackgroundColor(Color.WHITE);
                }
            }
        });

        return view;
    }

    private void applyfilter(List<ML_Image_Object> imgs, Set<String> selectionSet,Map<String,String> cache){
        // All image paths in cache
        if(cache == null){
            return;
        }
        List<ML_Image_Object> filtImg = new ArrayList<>();
        for(ML_Image_Object img : imgs){
            String path = img.getImID();
            String label = cache.get(path);
            if(selectionSet.contains(label)){
                filtImg.add(img);
            }
            //
        }
        //List if filtered images passed through. now we need to apply these filters.
        if(selectionSet.size() != 0){
            MLAdapter adapter2 = new MLAdapter(
                    getAppContext(),
                    R.layout.mlrow,
                    filtImg);
            gv.setAdapter(adapter2);
            gv.invalidateViews();
        }else{
            gv.setAdapter(adapter);
            gv.invalidateViews();
        }
    }


    private void loadLabelChips(LayoutInflater inflater){
        Set<String> labelSet = new HashSet<>();
        Map<String,String> cache = app.getCache();

        if(cache != null){
            for(String key : cache.values()){
                labelSet.add(key);
            }
        }
        chipGroup.removeAllViews();
        if(cache != null){
            for(final String labels : labelSet){
                Chip chip = (Chip)inflater.inflate(R.layout.filterchip,chipGroup,false);
                chip.setText(labels);
                chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            selectionSet.add(labels);
                            Log.e("SELECTION"," :: "+selectionSet);
                            applyfilter(img,selectionSet,app.getCache());
                        }else{
                            selectionSet.remove(labels);
                            Log.e("SELECTION"," :: "+selectionSet);
                            applyfilter(img,selectionSet,app.getCache());
                        }
                    }
                });
                chipGroup.addView(chip);
            }
        }

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
        File f1 = new File(img.getPath());
        Glide
                .with(getAppContext())
                .load(Uri.fromFile(f1))
                .into(Image);
        //Image.setImageBitmap();
        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FullView.class);
                i.putExtra("img_id",img.getPath());
                i.putExtra("filename",name);
                startActivity(i);
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

                Intent i = new Intent(getActivity(), FullView.class);
                i.putExtra("img_id",img.getPath());
                i.putExtra("filename",name);
                startActivity(i);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}

