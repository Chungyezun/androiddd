package com.example.project1.Game;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.project1.Contacts.Contact;
import com.example.project1.MyApplication;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
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
import java.util.List;

public class UpdateGameThread extends Thread{
    Context context;
    MyApplication app;
    URL url;
    URL url2;

    UpdateGameThread(MyApplication app){
        this.app = app;
    };

    String getURL = "http://b1b3f083.ngrok.io/getPlayer";
    String setURL = "http://b1b3f083.ngrok.io/postPlayer/";


    public void updateUS(){
        Gson gson = new Gson();

        //app.getPosition();

        //2. Get List of All Players
        try {
            HttpURLConnection con2 = null;
            BufferedReader reader = null;
            try {
                url = new URL(getURL);//url을 가져온다.
                con2 = (HttpURLConnection) url.openConnection();
                con2.connect();//연결 수행
                InputStream stream;
                //입력 스트림 생성
                try {
                    stream = con2.getInputStream();
                } catch (FileNotFoundException e) {
                    return;
                }

                //속도를 향상시키고 부하를 줄이기 위한 버퍼를 선언한다.
                reader = new BufferedReader(new InputStreamReader(stream));

                //실제 데이터를 받는곳
                StringBuffer buffer = new StringBuffer();

                //line별 스트링을 받기 위한 temp 변수
                String line = "";

                //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String... urls) 니까
                // 여기서 Player List String 이 보내질 것이다.
                String json = buffer.toString();
                Player[] array = gson.fromJson(json, Player[].class);
                Log.e("what", String.valueOf(array));//json 에서 얻어가기
                List<Player> nplayers = new ArrayList<>();
                if (nplayers == null || array == null) {
                } else {
                    Collections.addAll(nplayers, array);
                    app.setAllPlayers(nplayers);                   //여기 local variable 도 덮어쓰기
                }

                //버퍼를 닫고 다시 지정해야할까...?
                try {
                    //버퍼를 닫아준다.
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //종료가 되면 disconnect 메소드를 호출한다.
                if (con2 != null) {
                    con2.disconnect();

                }
                try {
                    //버퍼를 닫아준다.
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }//finally 부분


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateMe(){
        Gson gson = new Gson();
        String pjson;
        Player myPlayer = app.getMyPlayer();
        pjson = gson.toJson(myPlayer);
        Log.e("PJ",pjson);
        try {
            //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
            JSONObject jsonObject = new JSONObject(pjson);

            HttpURLConnection con = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(setURL+myPlayer.getName());
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
    public void run() {
        Log.e("THREAD", "Game Thread Started");


        //1. Get GPS Data and form Player Information Chart.
        // Location Update 명령


        while (true) { // 1초마다 여기서 Connection을 유지해보자..////////////////////////////////////////////////
            //1. My Location Update
            //app.getCurrentActivity().runOnUiThread(new Runnable() {
             //   public void run() {
             //       app.getPosition();
              //  }
            //});
            app.getPosition();
            updateMe();
            try{
                Thread.sleep(500);
            }catch(InterruptedException e){

            }
            Log.e("FIN","finished Get Request");
            //3. Update My Player Information
            updateUS();
            try{
                Thread.sleep(500);
            }catch(InterruptedException e){

            }
        }
    }
}