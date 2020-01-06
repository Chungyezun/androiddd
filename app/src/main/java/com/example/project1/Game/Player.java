package com.example.project1.Game;

import android.location.Location;

import androidx.core.util.Pair;

public class Player {
    private String name;
    private String unique;

    private int hp;
    private int MAX_HP;
    private double Latitude;
    private double Longitude;

    /* 플레이어의 직업을 설정하자!!!


    */

    //private Location location;
    private Pair<Double,Double> getPos(){
        return new Pair(this.Latitude,this.Longitude);
    }

    public String getName(){
        return name;
    }
    public String getUnique(){
        return unique;
    }

    public Pair<Double,Double> getLocation(){
        return new Pair(this.Latitude,this.Longitude);
    }
    public void setLocation(double Latitude,double Longitude){
        this.Latitude = Latitude;
        this.Longitude = Longitude;
    }

    public void Heal(int amount){
        this.hp = this.hp + amount;
        if(this.hp>MAX_HP){
            this.hp = MAX_HP;
        }
    }
    public boolean Damage(int amount){
        this.hp = this.hp - amount;
        if(this.hp < 0){
            return false;
            //내가 플레이어를 죽였으므로, 포인트를 얻는다...!
        }
        return true;
    }

    public Player(String nName, String nUnique){
        name = nName;
        unique = nUnique;
    }
}
