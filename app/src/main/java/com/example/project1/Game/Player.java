package com.example.project1.Game;

import androidx.core.util.Pair;

public class Player {
    private String name;
    private String number;

    private int hp;
    private int MAX_HP;

    private Pair<Integer, Integer> Location; // LAT LONG


    public String getName(){
        return name;
    }
    public String getNumber(){
        return number;
    }

    public Pair<Integer, Integer> getLocation(){
        return this.Location;
    }

    public void Heal(int amount){
        hp = hp + amount;
    }
    public void Damage(int amount){
        hp = hp - amount;
    }

    public Player(String nName, String nNumber){
        name = nName;
        number = nNumber;
    }
}
