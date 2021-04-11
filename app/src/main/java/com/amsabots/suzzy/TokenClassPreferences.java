package com.amsabots.suzzy;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenClassPreferences {
    private static final String PREF_NAME = "classname";
    private static Context context;
    private static final String TOKEN_ID = "king andrew";
private static TokenClassPreferences mInstance;
    public TokenClassPreferences(Context ctx) {
        context = ctx;
    }
public static synchronized TokenClassPreferences getInstance(Context context){
        if(mInstance == null) mInstance = new TokenClassPreferences(context);
        return mInstance;
}
    public void setTokenInstance(String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_ID, token);
        editor.apply();
    }
    public String getTokenId(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TOKEN_ID, null);
    }
    public void unSetTokenID(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
