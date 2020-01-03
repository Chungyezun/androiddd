package com.example.project1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.project1.Contacts.AddContact;
import com.example.project1.Contacts.Contact;
import com.example.project1.Gallery.Extern_Access;
import com.example.project1.Gallery.IMfile;
import com.example.project1.MLthings.ML_Image_Object;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.example.project1.ui.MainUI.SectionsPagerAdapter;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.project1.MyApplication.getAppContext;

public class MainActivity extends AppCompatActivity {
    private List<Contact> contacts = new ArrayList<>();
    private int tabPosition;

    private Map<String, String> cache = new HashMap<>();

    Gson gson;
    IOcustom iocustom;
    private List<ML_Image_Object> img = new ArrayList<>();
    List<IMfile> imdatas = new ArrayList<>();
    private static ML_Image_Object tmp = new ML_Image_Object(R.drawable.a,null,false);
    private static ML_Image_Object tmp2 = new ML_Image_Object(R.drawable.city,null,false);
    protected MyApplication app;

    public void buttonDo(int idx){
        final Intent camIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        if(idx == 0) {
            Intent intent = new Intent(this, AddContact.class);
            Log.e("TAB_PRESS", "0");
            startActivity(intent); // intent 를 통해 새 activity 에 접속?
        }else if(idx == 1) {
                startActivity(camIntent);
                Log.e("TAB_PRESS", "1");
        }else{
                startActivity(camIntent);
                Log.e("TAB_PRESS","2");
        }
    }

    public List<Contact> getContacts(){
        return contacts;
    }
    public List<ML_Image_Object> getImg(){return img;}


    // 이미지들을 외장 database 로부터 가져오기 위해 이것을 한다. Async 로 돌릴 수 있으면 그리 하자...
    private void load(){
        imdatas = Extern_Access.getGalleryImage(this);
        for(IMfile m : imdatas){
            ML_Image_Object mlo = new ML_Image_Object(R.drawable.city,null,false);
            mlo.setPath(m.path);
            mlo.setImID(m.document_id);
            img.add(mlo);
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},0);

        super.onCreate(savedInstanceState);

        app = (MyApplication)this.getApplicationContext();
        setContentView(R.layout.activity_main); //첫 화면은 activity_main.xml 로

        //Viewpager 및 Adapter 를 만들어주자
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        //TabLayout object (UI에 있는 것) 에 적용시키자
        final TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);



        //FAB
        final FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonDo(tabPosition);
            }
        });

        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                tabPosition = tabs.getSelectedTabPosition();
                if(tabPosition!= 0){
                    fab.setImageResource(R.drawable.ic_action_name);
                }else{
                    fab.setImageResource(R.drawable.ic_action_add);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab){

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab){

            }
        });
        // 이미지의 List 로 구현을 하겠습니다.

        /*IMAGE DATABASE 로딩!!!!*/
        //1. EXTERN_ACCESS class 안에 있는 함수들로 IMfile List 불러오기

        /*IMAGE DATABASE 로딩 끝*/
        load();


        //앱 lifecycle (전체 사용 기간) 시작할 때 자동으로 실행되고,
        iocustom = new IOcustom();
        gson = new Gson();

        //contacts 의 값들을 load 해줘야 한다.
        iocustom.readFromFile(this); //파일 열기

        //로딩 완료
        //로딩 완료가 되었으니, 이 값들을 sharedPreference 로 넘기자...


    }

    protected void onResume(){
        super.onResume();
        app.setCurrentActivity(this);
    }
    protected void onPause(){
        clearReferences();
        super.onPause();
    }
    protected void onDestroy(){
        clearReferences();
        super.onDestroy();
    }
    protected void clearReferences(){
        Activity currAct = app.getCurrentActivity();
        if(this.equals(currAct))app.setCurrentActivity(null);
    }

}
