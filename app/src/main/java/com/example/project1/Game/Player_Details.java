package com.example.project1.Game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.project1.Contacts.Contact;
import com.example.project1.MainActivity;
import com.example.project1.MyApplication;
import com.example.project1.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Player_Details extends AppCompatActivity {
    boolean inEdit;
    MyApplication app;
    int pos;
    TextView name;
    TextView number;
    EditText n_name;
    EditText n_number;

    //여기서, Toolbar 의 Menu 를 edit_normal_mode_normal_mode.xml 로 교체하고 싶다.
    //그러므로, 여기서 이것을 override 한다
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(inEdit){
            getMenuInflater().inflate(R.menu.edit_edit_mode, menu);
        }else{
            getMenuInflater().inflate(R.menu.edit_normal_mode, menu);
        }
        return true;
    }

    //Menu Item 선택 시 어떻게 반응하나.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(inEdit){
            switch (item.getItemId()) {
                case R.id.okMenu:
                    //Visibility 를 바꿔주자
                    n_name.setVisibility(View.INVISIBLE);
                    n_number.setVisibility(View.INVISIBLE);
                    name.setVisibility(View.VISIBLE);
                    number.setVisibility(View.VISIBLE);

                    String newName = n_name.getText().toString();
                    String newNumber = n_number.getText().toString();
                    Contact newcont = new Contact(newName,newNumber);


                    app.setContacts(newcont);//apply the edited stuff.
                    //apply edit to new interface aswell.
                    name.setText(newName);
                    number.setText(newNumber);

                    inEdit = false;
                    invalidateOptionsMenu();
                    return true;

                case R.id.cancelMenu:
                    //Visibility transfer
                    n_name.setVisibility(View.INVISIBLE);
                    n_number.setVisibility(View.INVISIBLE);
                    name.setVisibility(View.VISIBLE);
                    number.setVisibility(View.VISIBLE);

                    //Restore text to DEFAULT values
                    n_name.setText(name.getText());
                    n_number.setText(name.getText());

                    //mode variable change and reload menu.
                    inEdit = false;
                    invalidateOptionsMenu();
                    return true;

                default:
                    // If we got here, the user's action was not recognized.
                    // Invoke the superclass to handle it.
                    return super.onOptionsItemSelected(item);
            }

        }else{
            switch (item.getItemId()) {
                case R.id.editMenu:
                    //Visibility 를 바꿔주자
                    n_name.setVisibility(View.VISIBLE);
                    n_number.setVisibility(View.VISIBLE);
                    name.setVisibility(View.INVISIBLE);
                    number.setVisibility(View.INVISIBLE);
                    inEdit = true;
                    invalidateOptionsMenu();
                    return true;

                case R.id.deleteMenu:
                    //1. Display "Are you sure?" pop-up screen
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("삭제하시겠습니까?");
                    builder.setPositiveButton("아니요",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //아무것도 안함

                                }
                            });
                    builder.setNegativeButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //지우고 밖으로 가자.
                                    List<Contact> getlist = new ArrayList<>(app.getContacts());
                                    app.deleteContact(getlist.get(pos));//apply the edited stuff.
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                    builder.show();
                    return true;

                default:
                    // If we got here, the user's action was not recognized.
                    // Invoke the superclass to handle it.
                    return super.onOptionsItemSelected(item);
            }
        }
    }

    public static final String EXTRA_MESSAGE = "CONTACT_DATA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inEdit = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact__details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (TextView) findViewById(R.id.dispName);
        number = (TextView) findViewById(R.id.dispNumber);
        n_name = (EditText) findViewById(R.id.editName);
        n_number = (EditText) findViewById(R.id.editNumber);



        //초기화 상태는 editName editNumber 안보이게
        n_name.setVisibility(View.INVISIBLE);
        n_number.setVisibility(View.INVISIBLE);
        app = (MyApplication) getApplicationContext();


        Intent intent = getIntent();
        // intent 를 가져옴
        pos = intent.getIntExtra(EXTRA_MESSAGE,0);
        List<Contact> mcontacts = new ArrayList<>(app.getContacts());
        Contact target = mcontacts.get(pos);
        String load_name = target.getName();
        String load_number = target.getNumber();
        name.setText(load_name);
        number.setText(load_number);
        n_name.setText(load_name);
        n_number.setText(load_number);
    }

}
