package com.backers.backers;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by so on 2017-07-27.
 */

public class App extends Application{

//    public static final String SERVER_URL="http://so.air-button.com:9506/backers";
    //public static final String SERVER_URL="http://backersapp.air-button.com";

    //public static final String SERVER_URL = "http://35.165.7.243/backers";
    //public static final String SERVER_URL = "http://54.212.215.106/backers"; // changed on may 14
    public static final String SERVER_URL = "http://care.luna2.co/backers"; // changed on may 14
    public SharedPreferences setting;


    @Override
    public void onCreate() {
        super.onCreate();

        try {
            setting = getSharedPreferences("backers", Activity.MODE_PRIVATE);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.e("airbutton", "global onCreate error");
        }
    }
}
