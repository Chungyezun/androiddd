package com.example.project1.Gallery;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;





public class Extern_Access {
    public static ArrayList<IMfile> getGalleryImage(Context context){

        /*
        * <Flow of downloading task>
            1. Glide 에서 /Gallery 로 Get Request
            2. Get 으로부터 LIST OF FILE NAME 가지고 오기
            3. FILE PATH NAME 으로부터 GLIDE 이용해서 전부 받아오기~~~
         */



        /*
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        int column_index_id;
        ArrayList<IMfile> listOfAllImg = new ArrayList<>();
        String absPath = null;
        String docId = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,MediaStore.MediaColumns._ID,MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };
        cursor = context.getContentResolver().query(uri,projection,null,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                // 여기서 SQL 관련 나옴.
                // Query -> Data, Bucket_Display_Name 을 축출하고 싶고,
                // Projection:: 돌러받을 Column 들의 LIST!!
                // uri:: 어떤 주소에서 Database 를 받을 것인가...

                column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                column_index_id = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);

                column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                while(cursor.moveToNext()){
                    absPath = cursor.getString(column_index_data);
                    docId = cursor.getString(column_index_id);
                    IMfile k = new IMfile(docId,absPath);
                    listOfAllImg.add(k);
                }
                cursor.close();
            }
        }

        Log.d("EXTERN_IMLIST_SIZE",""+listOfAllImg.size());
        return listOfAllImg;// 모든 이미지의 filepath 를 돌려준다.

        */
        return null;
    }


}
