package com.example.project1;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.project1.Contacts.Contact;
import com.example.project1.Game.Player;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;

public class MyApplication extends Application {

    private static FusedLocationProviderClient fusedLocationClient;
    //내부 저장소를 사용할 일이 많을 것 같아, context 를 global variable로 저장하려 한다!
    private static Context context;

    //Three consistant data for application.
    //They are stored in disk by every edit sequence, and loaded onCreate();
    private List<Contact> contacts = new ArrayList<>();
    private Map<String, String> cache = new HashMap<>();
    private List<String> imgNames;
    private static Player myPlayer;
    private static List<Player> allPlayers;
    private static LocationRequest locationRequest;
    private static LocationCallback locationCallback;
    private static FusedLocationProviderClient mFusedLocationClient;
    private static String URL = "http://54083c4f.ngrok.io/";
    public String getURL(){
        return this.URL;
    }

    Gson gson;
    IOcustom iocustom;

    private Activity mCurrentActivity = null;
    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    public void onCreate(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        super.onCreate();
        iocustom = new IOcustom();
         imgNames = new ArrayList<>();

        // 이미지의 List 로 구현을 하겠습니다.

        //image loading

        //앱 lifecycle (전체 사용 기간) 시작할 때 자동으로 실행되고,
        MyApplication.context = getApplicationContext();
        iocustom = new IOcustom();
        gson = new Gson();

        //contacts loading
        iocustom.readFromFile(context); //파일 열기
        iocustom.getImName(context);


        //contacts loading done

        //Cache loading
        String jsonCache = iocustom.readFromFileCache(context); //파일 열기
            Type ssmap = new TypeToken<Map<String,String>>(){}.getType();
            cache = gson.fromJson(jsonCache, ssmap); //json 에서 얻어가기
        //Cache loading done


    }

    public List<Contact> getContacts(){
        iocustom.getImName(context);
        return contacts;
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
        }else{
            Collections.addAll(ncontacts,array);
            this.contacts = ncontacts;                       //여기 local variable 도 덮어쓰기
        }
    }

    public void updateNames(String jsonString){
        String[] array = gson.fromJson(jsonString, String[].class); //json 에서 얻어가기
        List<String> abc = new ArrayList<>();
        if(array == null){
            this.imgNames = null;
        }else{
            Collections.addAll(abc,array);
            Log.e("gotArray",abc.toString());
            this.imgNames = abc;                       //여기 local variable 도 덮어쓰기
        }
    }
    public List<String> getNames(){
        iocustom.getImName(context); // Name 리스트 업데이트도 겸함..
        return this.imgNames;
    }
    public List<Player> getAllPlayers(){
        return this.allPlayers;
    }
    public void setPlayer(Player player){
        this.myPlayer = player;
    }
    public void setAllPlayers(List<Player> allPlayers){

        this.allPlayers = allPlayers;
        Log.d("Alls",getAllPlayers().get(0).getName());
    }
    public Player getMyPlayer(){
        return this.myPlayer;
    }
    // context 를 불러와야 할 때 이것을 불러주면 된다!
    public static Context getAppContext(){
        return MyApplication.context;
    }
    public static void getPosition(){

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        myPlayer.setLocation(location.getLatitude(),location.getLongitude());

                    }
                }
                if (mFusedLocationClient != null) {
                    mFusedLocationClient.removeLocationUpdates(locationCallback);
                }
            }
        };
        HandlerThread handlerThread = new HandlerThread("MHT");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, looper);
    }
}
