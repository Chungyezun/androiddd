package com.example.project1.Game;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.project1.Contacts.ContactAdapter;
import com.example.project1.MyApplication;
import com.example.project1.R;
import com.example.project1.Tab3Activity;

import java.util.ArrayList;
import java.util.List;


public class Game_Fragment extends Fragment {
    MyApplication app;
    private Button mButton;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private GameAdapter mAdapter;
    private List<Player> players = new ArrayList<>();

    public List<Player> getPlayers(){
        return players;
    }

    public Game_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



//        Intent intent = new Intent(getContext(), Tab3Activity.class);
//        startActivity(intent);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_game, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.playerRV);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new GameAdapter(players);
        mRecyclerView.setAdapter(mAdapter);
        mButton = (Button)view.findViewById(R.id.game_start);
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

