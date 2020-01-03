package com.example.project1;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.example.project1.MyApplication.getAppContext;

public class IOcustom {
    String input_data;
    String write_data;
    public void writeToFile(String data, Context context) {
        // Exception 이 안 뜨면, Contacts.json 에 data string 저장. contexts 는 이 앱으로 준다.
        input_data = data;
        new JSONTask1().execute("http://cs497madcampproj2yeah.localtunnel.me/post");

    }

    public void readFromFile(Context context) {
        new JSONTask2().execute("http://cs497madcampproj2yeah.localtunnel.me/contacts");
    }


    public void writeToFileLabel(String data, Context context) {
        // Exception 이 안 뜨면, Contacts.json 에 data string 저장. contexts 는 이 앱으로 준다.
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("labelHist.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String readFromFileLabel(Context context) {
        String res = new String();
        try{
            FileInputStream fis = context.openFileInput("labelHist.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
                res = stringBuilder.toString();
            } catch (IOException e) {
                // Error occurred when opening raw file for reading.
                Log.e("FILE ERROR","File read failed");
                res = null;
            }

        }catch(FileNotFoundException e){
            Log.e("FILE ERROR","labelHist.json does not exist!!");
            res = null;
        }
        return res;
    }
    public void writeToFileCache(String data, Context context) {
        // Exception 이 안 뜨면, Contacts.json 에 data string 저장. contexts 는 이 앱으로 준다.
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("cache.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String readFromFileCache(Context context) {
        String res = new String();
        try{
            FileInputStream fis = context.openFileInput("cache.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
                res = stringBuilder.toString();
            } catch (IOException e) {
                // Error occurred when opening raw file for reading.
                Log.e("FILE ERROR","File read failed");
                res = null;
            }

        }catch(FileNotFoundException e){
            Log.e("FILE ERROR","labelHist.json does not exist!!");
            res = null;
        }
        return res;
    }

    public class JSONTask1 extends AsyncTask<String,String,String> {

        protected String doInBackground(String [] urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONArray jarray = new JSONArray(input_data);
                JSONObject jsonObject = jarray.getJSONObject(0);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
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

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

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
            return null;
        }
    }



    public class JSONTask2 extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    URL url = new URL(urls[0]);//url을 가져온다.
                    con = (HttpURLConnection) url.openConnection();
                    con.connect();//연결 수행

                    //입력 스트림 생성
                    InputStream stream = con.getInputStream();

                    //속도를 향상시키고 부하를 줄이기 위한 버퍼를 선언한다.
                    reader = new BufferedReader(new InputStreamReader(stream));

                    //실제 데이터를 받는곳
                    StringBuffer buffer = new StringBuffer();

                    //line별 스트링을 받기 위한 temp 변수
                    String line = "";

                    //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String... urls) 니까
                    return buffer.toString();

                    //아래는 예외처리 부분이다.
                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //종료가 되면 disconnect메소드를 호출한다.
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        //버퍼를 닫아준다.
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }//finally 부분
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            MyApplication app = (MyApplication) getAppContext();
            app.updateContacts(result);

        }
    }
}
