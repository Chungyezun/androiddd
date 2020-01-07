package com.example.project1.Game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.EmbossMaskFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.project1.MyApplication;
import com.example.project1.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


public class GameActivity extends Activity implements SensorEventListener {

    public static int cnt = 0;

    private TextView tView;
    private TextView tView2;
    private TextView myPlayer;
    private TextView enemy1;
    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;
    private boolean start = true;
    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private Player player1;
    private Player player2;
    private MyApplication app;
    private Socket mSocket;


    private static final int SHAKE_THRESHOLD = 800;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;
    String setURL = "http://22f3e836.ngrok.io";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        app = (MyApplication) getApplicationContext();
        tView = (TextView)findViewById(R.id.cntView);
        tView2 = (TextView)findViewById(R.id.textView2);
        myPlayer = (TextView)findViewById(R.id.myPlayer);
        enemy1 = (TextView)findViewById(R.id.enemy);


        try{
            mSocket = IO.socket(setURL);
        }catch(URISyntaxException e){}
        String enemy = getIntent().getStringExtra("Enemy");
        for(int i = 0; i < app.getAllPlayers().size();i++) {
            if(app.getAllPlayers().get(i).getName().equals(enemy)) {
                player2 = app.getAllPlayers().get(i);
            }
        }
        // 서버로부터의 소켓을 만들자!
        mSocket.connect();
        myPlayer.setText(app.getMyPlayer().getName());
        enemy1.setText(player2.getName());

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                Log.d("SOCKET","Battle Connected");
            }
        });

        mSocket.on("youwin",new Emitter.Listener(){

            @Override
            public void call(Object... args) {
                AlertDialog.Builder adb = new AlertDialog.Builder(GameActivity.this);
                adb.setTitle("승리!");
                adb.setMessage("축하합니다")
                        .setCancelable(false)
                        .setPositiveButton("그래",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        Intent intent = new Intent(getApplicationContext(), Tab3Activity.class);
                                        startActivity(intent);
                                    }

                                });
                if (!GameActivity.this.isFinishing()) {
                    AlertDialog alert = adb.create();
                    alert.show();
                }
            }
        });

        mSocket.on("ouch", new Emitter.Listener() {
            @Override
            public void call(final Object... objects) {
                mSocket.emit("surrender","");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject k = (JSONObject) objects[0];
                        //int dmg = (Integer) k.get("Damage");
                        player1.hp = player1.hp - 1;
                        tView2.setText("" + (++cnt));
                        progressBar1.incrementProgressBy(-1);
                        Log.d("HP",player1.hp + "");
                        //여기에 if문
                        if(player1.hp<0){

                            AlertDialog.Builder adb = new AlertDialog.Builder(GameActivity.this);
                            adb.setTitle("패배!");
                            adb.setMessage("ㅠㅠㅜㅜㅜㅜ")
                                    .setCancelable(false)
                                    .setPositiveButton("그래",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog, int id) {
                                                    Intent intent = new Intent(getApplicationContext(), Tab3Activity.class);
                                                    startActivity(intent);
                                                }

                                            });
                            if (!GameActivity.this.isFinishing()) {

                                AlertDialog alert = adb.create();
                                alert.show();
                            }
                        }
                    }
                });

            }
        });








        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        tView = (TextView) findViewById(R.id.cntView);
        tView.setText("" + cnt);
        progressBar1 = (ProgressBar) findViewById((R.id.progressBar));
        progressBar2 = (ProgressBar) findViewById((R.id.progressBar2));
        player1 = app.getMyPlayer();
        progressBar1.setProgress(player1.hp);
        progressBar2.setProgress(player2.hp);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
        }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {
                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    if(start) {
                        JSONObject data = new JSONObject();
                        try{
                            data.put("from",app.getMyPlayer().getName());
                            data.put("to",player2.getName());
                        }catch(JSONException e){
                        }
                        mSocket.emit("damage",data);
                        tView.setText("" + (++cnt));
                        progressBar2.incrementProgressBy(-1);
                        player2.hp--;
                        
                    }
                }

                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

//    public void mOnClick(View v) {
//        switch (v.getId()) {
//            case R.id.resetBtn :
//                cnt = 0;
//                tView.setText("" + cnt);
//                break;
//        }
//    }
}