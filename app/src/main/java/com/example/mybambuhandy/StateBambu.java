package com.example.mybambuhandy;


import org.parceler.Parcel;

import java.util.HashMap;
@Parcel
public class StateBambu {



    String name;
    int year;


    public StateBambu(){

    }
    public StateBambu(String name, int year) {
        this.name = name;
        this.year = year;
    }


}
