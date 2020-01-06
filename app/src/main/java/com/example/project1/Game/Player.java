package com.example.project1.Game;

import android.location.Location;

import androidx.core.util.Pair;

public class Player {
    private String name;
    private String number;

    private int hp;
    private int MAX_HP;

    /* 플레이어의 직업을 설정하자!!!


    */

    private Location location;


    public String getName(){
        return name;
    }
    public String getNumber(){
        return number;
    }

    public Location getLocation(){
        return this.location;
    }
    public void setLocation(Location location){
        this.location = location;
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

    public Player(String nName, String nNumber){
        name = nName;
        number = nNumber;
    }
}
