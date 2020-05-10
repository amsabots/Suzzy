package com.example.suzzy.Preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class OnBoardingScreen {
    private final String IS_NEW = "yea baby";
    private final String SHARED_PREFS_NAME = "IS_NEW";
    private Context context;
    private static OnBoardingScreen mInstance;
    private OnBoardingScreen(Context context){
        this.context = context;
    }
    public static synchronized OnBoardingScreen getInstance(Context context){
        if(mInstance == null) mInstance = new OnBoardingScreen(context);
         return mInstance;
    }
    public void setNew(boolean value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_NEW, value);
        editor.apply();
    }
    public  boolean isNew(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean(IS_NEW, true);
        return  value;
    }
}
