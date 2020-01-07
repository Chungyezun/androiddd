package com.example.project1.Game;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.project1.Game.GameActivity;
import com.example.project1.Game.Player;
import com.example.project1.MyApplication;
import com.example.project1.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONException;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.util.function.Predicate;

import static com.example.project1.Game.GameActivity.cnt;


public class Tab3Activity extends AppCompatActivity implements GoogleMap.OnMapClickListener, OnMapReadyCallback {
    public static WaitingDialog wd;
    String setURL = "http://22f3e836.ngrok.io";
    // SOCKET FUNCTIONS HERE!
    boolean response;
    MyActivity request;

    private GoogleMap mGoogleMap;
    private MyApplication app;
    List<CircleOptions>enemies;
    CircleOptions battleRadius;
    List<Circle>enemyCircle;
    List<GoogleMap.OnCircleClickListener> circleClickListeners;
    Circle myCircle;
    Player enemy;
    Emitter.Listener battleListener;
    private Socket mSocket;
    List<Marker> enemyMarker;
    Marker myMarker;
    List<MarkerOptions> enemyMarkerOptions;


    private void sendBattleRequest(String name) {
        JSONObject data = new JSONObject();
        try {
            data.put("from", app.getMyPlayer().getName());
            data.put("to", name);
            mSocket.emit("BattleRequest", data);
            Log.d("Enemy","Sent Battle Req");
        }catch( JSONException e){
            e.printStackTrace();
        }
    }


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

        try{
            mSocket = IO.socket(setURL);
        }catch(URISyntaxException e){}

        // 서버로부터의 소켓을 만들자!
        mSocket.connect();

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                Log.d("SOCKET","Connected");
            }
        });

        mSocket.on("BattleRequest", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                Log.d("SOCKET","You have got a battle Request");
                String from = (String) objects[0];
                //Accept or not.. This is the question...
                AlertDialog.Builder adb = new AlertDialog.Builder(getApplicationContext());
                adb.setTitle("대결신청");
                adb.setMessage("싸우시겠습니까?");
                adb.setMessage("프로그램을 종료할 것입니까?")
                .setCancelable(false)
                .setPositiveButton("싸운다",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        JSONObject yes = new JSONObject();
                                        try {
                                            yes.accumulate("answer", true);
                                        }catch(JSONException e){

                                        }
                                        mSocket.emit("AcceptBattleRequest",yes);
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("도망간다",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        JSONObject yes = new JSONObject();
                                        try {
                                            yes.accumulate("answer", false);
                                        }catch(JSONException e){

                                        }
                                        mSocket.emit("AcceptBattleRequest",yes);
                                        dialog.cancel()
                                        dialog.cancel();
                                    }
                                });
                //mSocket.emit("AcceptBattleRequest",0); //YES 라고 가정할 때...
            }
        });

        // 이름을 보내서 connection 이랑 mapping 해준다
        JSONObject name = new JSONObject();
        try {
            name.put("name", app.getMyPlayer().getName());
            mSocket.emit("UserNameSend", name);

        }catch(JSONException e){
        }



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
                .radius(300)
                .fillColor(Color.parseColor("#4D0000ff"))

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
        enemyMarkerOptions = new ArrayList<>();
        enemyMarker = new ArrayList<>();


        List<Player> allPlayers = app.getAllPlayers();
        if(allPlayers == null){

        }else{
            for(int i = 0; i < length;i++){

                MarkerOptions mko = new MarkerOptions()
                        .position(new LatLng(app.getAllPlayers().get(i).getLocation().first, app.getAllPlayers().get(i).getLocation().second))
                        .title(app.getAllPlayers().get(i).getName())
                        .snippet(app.getAllPlayers().get(i).getUnique());
                enemyMarkerOptions.add(mko);

                if(app.getAllPlayers().get(i).getUnique().equals("직업2")) {
                    CircleOptions otherCircles = new CircleOptions()
                            .center(new LatLng(app.getAllPlayers().get(i).getLocation().first, app.getAllPlayers().get(i).getLocation().second))
                            .radius(50)
                            .fillColor(Color.parseColor("#4D0000ff"))
                            .strokeWidth(0f)
                            .clickable(false);
                    enemies.add(otherCircles);
                }else if(app.getAllPlayers().get(i).getUnique().equals("직업1")){
                    CircleOptions otherCircles = new CircleOptions()
                            .center(new LatLng(app.getAllPlayers().get(i).getLocation().first, app.getAllPlayers().get(i).getLocation().second))
                            .radius(70)
                            .fillColor(Color.parseColor("#4DEE0000"))
                            .strokeWidth(0f)
                            .clickable(false);
                    enemies.add(otherCircles);
                }else{
                    CircleOptions otherCircles = new CircleOptions()
                            .center(new LatLng(app.getAllPlayers().get(i).getLocation().first, app.getAllPlayers().get(i).getLocation().second))
                            .radius(50)
                            .fillColor(Color.parseColor("#4D0000ff"))
                            .strokeWidth(0f)
                            .clickable(false);
                    enemies.add(otherCircles);
                }
            }
        }
        for(int i = 0; i < length;i++){
            if(!app.getAllPlayers().get(i).getName().equals(app.getMyPlayer().getName()) ){
                enemyCircle.add(mGoogleMap.addCircle(enemies.get(i)));
                enemyCircle.get(enemyCircle.size()-1).setTag(app.getAllPlayers().get(i).getName());

                enemyMarker.add(mGoogleMap.addMarker(enemyMarkerOptions.get(i)));
                enemyMarker.get(enemyCircle.size()-1).setTag(app.getAllPlayers().get(i).getName());

            }else{
                enemyCircle.add(null);
                enemyMarker.add(null);
                Log.d("me","me");
            }
        }

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String name = (String) marker.getTag();
                if(name == null){

                }else{


                Log.d("SELECT_NAME",name);
                MyActivity request = new MyActivity();
                request.startEvent();
                wd = new WaitingDialog();

                for(Player player : app.getAllPlayers()){
                    if(player.getName().equals(name)){
                        enemy = player;
                        // 반경 계산해서 if 돌리자!!!!!!!!!!!
                        sendBattleRequest(enemy.getName());
                        break;
                    }
                }

                // if 로 처리!!
                wd.show(getSupportFragmentManager(),name);
                //Waiting For.. 창
                }
            }
        });
        mGoogleMap.setOnMyLocationButtonClickListener(null);
        mGoogleMap.setOnMyLocationClickListener(null);
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){

            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
        mGoogleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {

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
                if(enemyCircle.get(i) != null){
                    enemyCircle.get(i).setCenter(new LatLng(app.getAllPlayers().get(i).getLocation().first, app.getAllPlayers().get(i).getLocation().second));
                }
            }
        }
        myMarker.setPosition(new LatLng(app.getMyPlayer().getLocation().first,app.getMyPlayer().getLocation().second));

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
            myMarker = mGoogleMap.addMarker(optFirst);
            t.start();
    }
        public static class WaitingDialog extends DialogFragment
        {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            MyEventListener ml;
            // 10초간의 서버 통신 시작!
            builder.setMessage("답장 기다리는 중...")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            /*
                            * 1. Send "cancelBattle/enemyname" request!
                            */
                            //app.getMyPlayer().setGetBattleRequest(-1); //내가 취소할 때...
                            dialog.dismiss(); // Cancel 누르면 그냥 초기화
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    public interface MyEventListener {
        public void onEventCompleted();
        public void onEventFailed();
    }
    public class MyActivity implements MyEventListener {

        public void startEvent() {
            new battleRequest(this).execute();
        }

        @Override
        public void onEventCompleted() {
            //response 가지고 한다
            if(response){
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                intent.putExtra("Enemy",enemy.getName());
                startActivity(intent);
            }else{
                Log.e("MES","Enemy Refuesd to fight you");
            }


        }
        @Override
        public void onEventFailed() {
            //포기...
        }
    }

    public Player getPlayer(String name) {
        List<Player> allPlayers = app.getAllPlayers();
        for (Player p : allPlayers) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }
    public class battleRequest extends AsyncTask<Void, Void, Void> {
        private MyEventListener callback;
        int cnt;
        public battleRequest(MyEventListener cb) {
            callback = cb;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //Request 받기!
            try {
            response = false;
            battleListener = new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("Enemy","Got Response from " + enemy.getName());
                    JSONObject obj = (JSONObject)args[0];
                    try{
                        response = obj.getBoolean("Response");
                        if(response){
                            Log.d("Enemy","ACCEPT BATTLE");
                            throw new InterruptedException();
                        }else{
                            Log.d("Enemy","Ran Away...");
                        }
                    }catch (JSONException e){

                    }catch( InterruptedException e){
                        //throw new InterruptedException();
                    }
                }
            };
            mSocket.on("requestAnswer",battleListener);
            Log.d("Enemy","WAIT RESPONSE FOR REQUEST");

                    Thread.sleep(10000);
            }catch(InterruptedException e){
                Log.d("ENEMY","Enemy responded");
            }

            return null; // 10초 기다림!
        }

        @Override
        protected void onPostExecute(Void aVoid) {
                callback.onEventCompleted();
        }
    }



    public void updateEnemy(Player enemy){
        Gson gson = new Gson();
        String pjson;
        pjson = gson.toJson(enemy);
        Log.e("PJ",pjson);
        try {
            //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
            JSONObject jsonObject = new JSONObject(pjson);

            HttpURLConnection con = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(setURL+"/postPlayer/"+enemy.getName());
                //연결을 함
                con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("POST");//POST방식으로 보냄
                con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                con.setRequestProperty("Accept", "application/json");//서버에 response 데이터를 html로 받음
                con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                con.connect();

                //서버로 보내기위해서 스트림 만듬
                OutputStream outStream = con.getOutputStream();
                //버퍼를 생성하고 넣음
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();//버퍼를 받아줌

                //서버로 부터 데이터를 받음
                InputStream stream = con.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                Log.d("SEND",buffer.toString());//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();//버퍼를 닫아줌
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}
