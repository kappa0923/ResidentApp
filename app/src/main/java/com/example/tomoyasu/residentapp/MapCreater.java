package com.example.tomoyasu.residentapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by Tomoyasu on 2015/11/28.
 */
public class MapCreater {
    static final String TAG="LocalService";

    public static void mapWrite(HashMap<String,String> map) {
        try {
            FileWriter fileWriter = new FileWriter(new File("map.txt"), false);
            for (HashMap.Entry<String,String> entry : map.entrySet()) {
                fileWriter.write(entry.getKey() + "," + entry.getValue() + "\n");
            }
            fileWriter.close();
        } catch (Exception e) {
            Log.i(TAG,"Error1");
        }
    }

    public static HashMap<String,String> mapRead() {
        HashMap<String,String> map = new HashMap<>();
        try {
            FileInputStream fileInputStream = new FileInputStream("map.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
            String inputString = bufferedReader.readLine();
            while(inputString != null) {
                String[] separate = inputString.split(",", 0);
                map.put(separate[0], separate[1] + "," + separate[2]);
                inputString = bufferedReader.readLine();
            }
        } catch (Exception e) {
            Log.i(TAG,"Error2");
        }

        return map;
    }
}
