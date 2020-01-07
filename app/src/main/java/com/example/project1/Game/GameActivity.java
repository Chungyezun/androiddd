package com.example.project1.Game;

import android.app.Activity;
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

    public static int cnt = 1;

    private TextView tView;

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
        try{
            mSocket = IO.socket(setURL);
        }catch(URISyntaxException e){}

        // 서버로부터의 소켓을 만들자!
        mSocket.connect();

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                Log.d("SOCKET","Battle Connected");
            }
        });

        mSocket.on("ouch", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                JSONObject k = (JSONObject) objects[0];
                    //int dmg = (Integer) k.get("Damage");
                player1.hp = player1.hp - cnt;
                Log.d("SOCKET","Battle Connected");

            }
        });







        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        tView = (TextView) findViewById(R.id.cntView);
        tView.setText("" + cnt);
        progressBar1 = (ProgressBar) findViewById((R.id.progressBar));
        progressBar2 = (ProgressBar) findViewById((R.id.progressBar2));
        player1 = app.getMyPlayer();
        String enemy = getIntent().getStringExtra("Enemy");
        for(int i = 0; i < app.getAllPlayers().size();i++) {
            if(app.getAllPlayers().get(i).equals(enemy)) {
                player2 = app.getAllPlayers().get(i);
            }
        }
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