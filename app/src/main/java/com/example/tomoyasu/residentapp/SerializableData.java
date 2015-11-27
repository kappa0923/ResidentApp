package com.example.tomoyasu.residentapp;

import android.content.Intent;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by tomoyasu on 2015/11/27.
 */
public class SerializableData implements Serializable {
    private static final long serialVersionUID = 1833613812636250574L;
    private String string_;
    private int number_;
    private HashMap<String, Intent> map_;

    public String getString() {
        return string_;
    }

    public void setString(String string){
        string_ = string;
    }

    public HashMap<String, Intent> getMap() {
        return map_;
    }

    public void setMap(HashMap<String,Intent> map) {
        map_ = map;
    }

    public int getNumber() {
        return number_;
    }

    public void setNumber(int number) {
        number_ = number;
    }
}
