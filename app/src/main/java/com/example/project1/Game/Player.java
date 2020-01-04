package com.example.project1.Game;

public class Player {
    private String name;
    private String number;

    public String getName(){
        return name;
    }
    public String getNumber(){
        return number;
    }


    public Player(String nName, String nNumber){
        name = nName;
        number = nNumber;
    }
}
