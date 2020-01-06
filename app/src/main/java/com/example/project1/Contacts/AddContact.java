package com.example.project1.Contacts;

import android.content.Intent;
import android.os.Bundle;

import com.example.project1.Contacts.Contact;
import com.example.project1.MainActivity;
import com.example.project1.IOcustom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.project1.MyApplication;
import com.example.project1.R;

import java.util.ArrayList;
import java.util.List;

public class AddContact extends AppCompatActivity {


    TextView name;
    TextView number;
    EditText n_name;
    EditText n_number;
    MyApplication app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        n_name = findViewById(R.id.editName);
        n_number = findViewById(R.id.editNumber);
        name = findViewById(R.id.dispNumber);
        number = findViewById(R.id.dispName);

        //이전 activity 로부터 전달받았을 정보로 채운다.
        List<Contact> contacts = new ArrayList<>();

        app = (MyApplication) getApplicationContext();


        setContentView(R.layout.activity_add_contact);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Contacts");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    public void press_cancel(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //Main edit_normal_mode 로 돌아감...ㅜㅜ
    }

    public void press_save(View view){

        Intent intent = new Intent(this, MainActivity.class);

        EditText name = (EditText) findViewById(R.id.typename);
        String n_name = name.getText().toString();

        EditText addr = (EditText) findViewById(R.id.typeaddr);
        String n_addr = addr.getText().toString();

        Contact ncont = new Contact(n_name,n_addr);

        List<Contact> contacts = new ArrayList<>();
        contacts = app.getContacts();
        contacts.add(ncont);
        app.setContacts(contacts);

        //LOAD JSON FILE
        startActivity(intent); //main 으로 복귀
    }

}
