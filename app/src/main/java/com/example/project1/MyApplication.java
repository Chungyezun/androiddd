package com.example.project1;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.example.project1.Contacts.Contact;
import com.example.project1.Gallery.Extern_Access;
import com.example.project1.Gallery.IMfile;
import com.example.project1.MLthings.ML_Image_Object;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyApplication extends Application {

    //내부 저장소를 사용할 일이 많을 것 같아, context 를 global variable로 저장하려 한다!
    private static Context context;

    //Three consistant data for application.
    //They are stored in disk by every edit sequence, and loaded onCreate();
    private List<Contact> contacts = new ArrayList<>();
    private Map<String, String> cache = new HashMap<>();
    private List<ML_Image_Object> img = new ArrayList<>();

    Gson gson;
    IOcustom iocustom;
    List<IMfile> imdatas = new ArrayList<>();

    private Activity mCurrentActivity = null;
    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    public void onCreate(){
        super.onCreate();

        // 이미지의 List 로 구현을 하겠습니다.

        //image loading
        load();
        //image loading done


        //앱 lifecycle (전체 사용 기간) 시작할 때 자동으로 실행되고,
        MyApplication.context = getApplicationContext();
        iocustom = new IOcustom();
        gson = new Gson();

        //contacts loading
        iocustom.readFromFile(context); //파일 열기
        /*
        if(json == null){
            Log.e("login activity","Non-existing DATABASE");
        }else{
            Contact[] array = gson.fromJson(json, Contact[].class); //json 에서 얻어가기
            Collections.addAll(contacts,array);
        }
         */
        //contacts loading done

        //Cache loading
        String jsonCache = iocustom.readFromFileCache(context); //파일 열기
            Type ssmap = new TypeToken<Map<String,String>>(){}.getType();
            cache = gson.fromJson(jsonCache, ssmap); //json 에서 얻어가기
        //Cache loading done


    }

    public List<Contact> getContacts(){
        return contacts;
    }
    public List<ML_Image_Object> getImg(){
        load();
        return img;

    }


    // 이미지들을 외장 database 로부터 가져오기 위해 이것을 한다. Async 로 돌릴 수 있으면 그리 하자...
    private void load(){
        imdatas = Extern_Access.getGalleryImage(getApplicationContext());
        //Log.d("IMDATASIZE",imdatas.size() +"");
        if(imdatas == null){
            return;
        }
        img.clear();
        for(IMfile m : imdatas){
            ML_Image_Object mlo = new ML_Image_Object(R.drawable.city,null,false);
            mlo.setPath(m.path);
            mlo.setImID(m.document_id);
            img.add(mlo);
        }
    }

    //Refresh 명령을 내리거나, 새로운 사진을 저장했을 때 이것을 부르자.
    public void reload(){
        load();
    }

    // set일 때 수정, get 일 때 수정안함. Mainactivity 는 앱이랑 꺼지지 않는다.


    //Push-Through like system. Once edit variable, overwrite database.
    public void setCache(Map<String, String> ncache){
        this.cache = ncache;
        String json;
        json = gson.toJson(ncache);
        iocustom.writeToFileCache(json,getAppContext());
    }

    public Map<String, String> getCache(){
        String jsonCache = iocustom.readFromFileCache(getAppContext());
        Type ssmap = new TypeToken<Map<String,String>>(){}.getType();
        this.cache = gson.fromJson(jsonCache, ssmap); //json 에서 얻어가기
        return this.cache;
    }

    public void setContacts(List<Contact> contacts){
        // List Manipulation 은 그냥 activity/fragment 에서 하자.
        // 여기는 I/O 및 덮어쓰기만 하자
        this.contacts = contacts;                       //여기 local variable 도 덮어쓰기
        String json;
        json = gson.toJson(contacts);                   //리스트를 json 으로 만들기
        iocustom.writeToFile(json,getAppContext());     // 그거를 그대로 덮어쓰기
    }
    public void updateContacts(String jsonString){
        Contact[] array = gson.fromJson(jsonString, Contact[].class); //json 에서 얻어가기
        List<Contact> ncontacts = new ArrayList<>();
        if(ncontacts == null || array == null){
    //
        }else{
            Collections.addAll(ncontacts,array);
            this.contacts = ncontacts;                       //여기 local variable 도 덮어쓰기
        }

    }
    // context 를 불러와야 할 때 이것을 불러주면 된다!
    public static Context getAppContext(){
        return MyApplication.context;
    }

}
