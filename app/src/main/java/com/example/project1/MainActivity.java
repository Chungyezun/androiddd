package com.example.project1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.project1.Contacts.AddContact;
import com.example.project1.Contacts.Contact;
import com.example.project1.Gallery.Extern_Access;
import com.example.project1.Gallery.IMfile;
import com.example.project1.MLthings.ML_Image_Object;
import com.example.project1.ui.MainUI.SectionsPagerAdapter;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.project1.MyApplication.getAppContext;

public class MainActivity extends AppCompatActivity {
    private List<Contact> contacts = new ArrayList<>();
    private int tabPosition;
    private CallbackManager callbackManager;
    LoginButton loginButton;

    private Map<String, String> cache = new HashMap<>();

    Gson gson;
    IOcustom iocustom;
    private List<ML_Image_Object> img = new ArrayList<>();
    List<IMfile> imdatas = new ArrayList<>();
    private static ML_Image_Object tmp = new ML_Image_Object(R.drawable.a,null,false);
    private static ML_Image_Object tmp2 = new ML_Image_Object(R.drawable.city,null,false);
    private static final String TAG = "MainActivity";
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
        FacebookSdk.sdkInitialize(getApplicationContext());

        app = (MyApplication)this.getApplicationContext();

        setContentView(R.layout.login); //첫 화면은 login.xml 로

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.project1",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {
        }
        catch (NoSuchAlgorithmException e) {
        }




        callbackManager = CallbackManager.Factory.create();


        loginButton = (LoginButton) findViewById(R.id.login_button);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
            public void onSuccess(LoginResult loginResult) {
                    ///////////////////////////////////////////////////////////////////////////////////////////////
                    //Viewpager 및 Adapter 를 만들어주자

                    Log.e("Error","success");
                    setContentView(R.layout.activity_main);
                    SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getAppContext(), getSupportFragmentManager());
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
                    iocustom.readFromFile(getAppContext()); //파일 열기

                    //로딩 완료
                    //로딩 완료가 되었으니, 이 값들을 sharedPreference 로 넘기자...

///////////////////////////////////////////////////////////////////
            }

            @Override
            public void onCancel() {
                // App code
                Log.e("Error","cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e("Error","error");
            }
        });



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
