package com.example.project1.Game;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.project1.MyApplication;
import com.example.project1.R;


public class GameActivity extends Activity implements SensorEventListener {

    public static int cnt = 0;

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

    private static final int SHAKE_THRESHOLD = 800;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        app = (MyApplication) getApplicationContext();
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