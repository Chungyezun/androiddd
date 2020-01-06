package com.example.project1;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import com.example.project1.Game.Player;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Tab3Activity extends AppCompatActivity implements GoogleMap.OnMapClickListener, OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private MyApplication app;
    List<CircleOptions>enemies;
    CircleOptions battleRadius;
    List<Circle>enemyCircle;
    List<GoogleMap.OnCircleClickListener> circleClickListeners;
    Circle myCircle;
    Thread t = new Thread(){
        @Override
        public void run(){
            while(true){
                try {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            update();
                            Log.d("t","tick");
                        }
                    });
                }catch(InterruptedException e){
                    break;
                }
            }
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        app = (MyApplication) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab3);
        MapsInitializer.initialize(getApplicationContext());
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    @Override //구글맵을 띄울준비가 됬으면 자동호출된다.
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //지도타입 - 일반
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        battleRadius = new CircleOptions()
            .center( new LatLng(app.getMyPlayer().getLocation().first,app.getMyPlayer().getLocation().second))
                .radius(50)
                .fillColor(Color.parseColor("#880000ff"))
                .strokeWidth(0f)
                .clickable(false);
        myCircle = mGoogleMap.addCircle(battleRadius);

        int length;
        if(app.getAllPlayers() == null) {
            length = 0;
        }else{
            length =app.getAllPlayers().size();
        }

        enemies = new ArrayList<>();
        enemyCircle = new ArrayList<>();

        List<Player> allPlayers = app.getAllPlayers();
        if(allPlayers == null){

        }else{
            for(int i = 0; i < length;i++){
                if(!app.getAllPlayers().get(i).getName().equals(app.getMyPlayer().getName()) ){
                    CircleOptions otherCircles = new CircleOptions()
                            .center( new LatLng(app.getAllPlayers().get(i).getLocation().first,app.getAllPlayers().get(i).getLocation().second))
                            .radius(5)
                            .fillColor(Color.parseColor("#8800ff00"))
                            .clickable(true);
                    enemies.add(otherCircles);
                }
            }
        }
        for(int i = 0; i < length;i++){
            if(!app.getAllPlayers().get(i).getName().equals(app.getMyPlayer().getName()) ){
                enemyCircle.add(mGoogleMap.addCircle(enemies.get(i)));
                enemyCircle.get(enemyCircle.size()-1).setTag(app.getAllPlayers().get(i).getName());
            }else{

                Log.d("me","me");
            }
        }

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMyLocationButtonClickListener(null);
        mGoogleMap.setOnMyLocationClickListener(null);
        mGoogleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {
                String name = (String) circle.getTag();
                Log.d("SELECT_NAME",name);

            }
        });
        init();



    }
    public void update(){
        int length;
        if(app.getAllPlayers() == null) {
            length = 0;
        }else{
            length =app.getAllPlayers().size()-1;
        }

        List<Player> allPlayers = app.getAllPlayers();
        if(allPlayers == null){
        }else {
            for (int i = 0; i < length; i++) {
                enemyCircle.get(i).setCenter(new LatLng(app.getAllPlayers().get(i).getLocation().first, app.getAllPlayers().get(i).getLocation().second));
            }
        }
        myCircle.setCenter(new LatLng(app.getMyPlayer().getLocation().first,app.getMyPlayer().getLocation().second));

    }





    /** Map 클릭시 터치 이벤트 */
    public void onMapClick(LatLng point) {

        // 현재 위도와 경도에서 화면 포인트를 알려준다
        Point screenPt = mGoogleMap.getProjection().toScreenLocation(point);

        // 현재 화면에 찍힌 포인트로 부터 위도와 경도를 알려준다.
        LatLng latLng = mGoogleMap.getProjection().fromScreenLocation(screenPt);

        Log.d("맵좌표", "좌표: 위도(" + String.valueOf(point.latitude) + "), 경도("
                + String.valueOf(point.longitude) + ")");
        Log.d("화면좌표", "화면좌표: X(" + String.valueOf(screenPt.x) + "), Y("
                + String.valueOf(screenPt.y) + ")");
    }


    /**
     * 초기화
     * @author
     */
    private void init() {

        Pair<Double, Double> pos = app.getMyPlayer().getLocation();
        // 맵의 이동
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        // GPS 사용유무 가져오기
            double latitude = pos.first;
            double longitude = pos.second;

            // Creating a LatLng object for the current location
            LatLng latLng = new LatLng(latitude,longitude);

            // Showing the current location in Google Map
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Map 을 zoom 합니다.
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(20));

            // 마커 설정.
            MarkerOptions optFirst = new MarkerOptions();
            optFirst.position(latLng);// 위도 • 경도
            optFirst.title("Current Position");// 제목 미리보기
            optFirst.snippet("Snippet");
            mGoogleMap.addMarker(optFirst).showInfoWindow();
            t.start();

    }

}
