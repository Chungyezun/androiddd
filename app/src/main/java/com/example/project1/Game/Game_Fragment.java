package com.example.project1.Game;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.Spinner;
import android.widget.TextView;

import com.example.project1.MyApplication;
import com.example.project1.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.project1.MyApplication.getAppContext;


public class Game_Fragment extends Fragment {
    MyApplication app;
    private Button login_button;
    private Button mButton;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private EditText mEditText;
    private GameAdapter mAdapter;
    private Spinner mSpinner;
    private List<Player> players = new ArrayList<>();
    private String unique;
    private String player_name;
    private TextView mPlayerr;
    private TextView mJikup;
    private TextView mHp;
    private TextView mMaxhp;
    private SwipeRefreshLayout layout;
    private ImageView image;
    private Boolean login = false;

    SwipeRefreshLayout swl;

    public List<Player> getPlayers(){
        return players;
    }

    public Game_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_game, container, false);
        layout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefreshGL);
        image = (ImageView)view.findViewById(R.id.unique_pic);
        layout.setBackgroundResource(R.drawable.gamebackgroud);
        image.setImageResource(R.drawable.person);
        layout.bringChildToFront(image);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.players);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new GameAdapter(players);
        mRecyclerView.setAdapter(mAdapter);
        mSpinner = (Spinner)view.findViewById(R.id.class_spinner);
        mEditText = (EditText)view.findViewById(R.id.player);
        mButton = (Button)view.findViewById(R.id.game_start);
        login_button = (Button)view.findViewById(R.id.button_player);
        mPlayerr = (TextView)view.findViewById(R.id.playerr);
        mJikup =(TextView)view.findViewById(R.id.jikup);
        mHp = (TextView)view.findViewById(R.id.nowhp);
        mMaxhp = (TextView)view.findViewById(R.id.nowmaxhp);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Tab3Activity.class);
                startActivity(intent);
            }
        });
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                unique = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player_name = String.valueOf(mEditText.getText());
                Player newPlayer = new Player(player_name,unique,100,100);
                Log.e("player",player_name);
//                if(unique.equals("직업1")) {
//                    newPlayer.MAX_HP = 200;
//                    newPlayer.hp = 200;
//                }else if(unique.equals("직업2")){
//                    newPlayer.MAX_HP = 300;
//                    newPlayer.hp = 300;
//                }

                app = (MyApplication) getAppContext();
                app.setPlayer(newPlayer);
                mPlayerr.setText(newPlayer.getName());
                mJikup.setText(newPlayer.getUnique());
                app.getPosition();
                UpdateGameThread ugthread = new UpdateGameThread(app);
                ugthread.start();
                if(app.getAllPlayers() != null) {
                    for (int i = 0; i < app.getAllPlayers().size(); i++) {
                        Player position = app.getAllPlayers().get(i);
                        if (player_name.equals(position.getName()) && unique.equals(position.getUnique())) {
                            app.setPlayer(new Player(player_name, unique, position.getHP(), position.getMAXhp()));
                            mHp.setText(app.getMyPlayer().getHP());
                            mMaxhp.setText(app.getMyPlayer().getMAXhp());
                        } else {
                            app.setPlayer(newPlayer);
                            mHp.setText(String.valueOf(newPlayer.getHP()));
                            mMaxhp.setText(String.valueOf(newPlayer.getMAXhp()));
                        }
                    }
                }
            }
        });
        swl = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshGL);
        swl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                app = (MyApplication) getAppContext();
                if(app.getAllPlayers() != null) {
                    players = app.getAllPlayers();
                    Collections.sort(players,Collections.<Player>reverseOrder());
                    GameAdapter nAdapter = new GameAdapter(players);
                    mRecyclerView.setAdapter(nAdapter);
                    Log.e("Hi", String.valueOf(players));
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swl.setRefreshing(false);
                    }
                }, 300);
            }
        });
        /*
        gridView.setOnScrollListener(new GridView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int ScrollState){
                //super(gv.onScrollSt);
            }
            @Override
            public void onScroll(AbsListView view,int firstVisibleItem,int ic,int vc){
                if(gridView.getChildAt(0) != null){
                    swl.setEnabled(gridView.getFirstVisiblePosition() == 0 && gridView.getChildAt(0).getTop()==0);
                }
            }
        });
        */
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

