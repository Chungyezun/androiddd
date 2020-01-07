package com.example.project1.Game;

import android.location.Location;

import androidx.core.util.Pair;

public class Player implements Comparable{
    private String name;
    private String unique;

    int hp;
    int MAX_HP;
    private double Latitude;
    private double Longitude;

    int getBattleRequest;   //이것이 True 면, String enemy 확인해서 적 확인
    boolean ready;
    String enemy;               //Battle 승낙하면, Enemy.getBattleRequest True

    public void setReady(boolean ready) {
        this.ready = ready;
    }
    public boolean myReady() {
        return this.ready;
    }
    public void setGetBattleRequest(int battleRequest){
        this.getBattleRequest = battleRequest;
    }
    public int myGetBattleRequest(){
        return this.getBattleRequest;
    }
    public void setEnemy(String enemyName){
        this.enemy = enemyName;
    }
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
    public int getHP(){
        return hp;
    }
    public int getMAXhp(){
        return MAX_HP;
    }
    public Pair<Double,Double> getLocation(){
        return new Pair(this.Latitude,this.Longitude);
    }
    public boolean online = false;



    // getBattleRequest 둘다 True 면 전투 돌입!!!

    //.hasEnemy 가 true ==> 상대방이 내 Request 받아줌!

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

    public Player(String nName, String nUnique, int nMaxHP, int nHP){
        name = nName;
        unique = nUnique;
        hp = nHP;
        MAX_HP = nMaxHP;
    }

    @Override
    public int compareTo(Object o) {
        return (this.hp - ((Player)o).hp);
    }
}
