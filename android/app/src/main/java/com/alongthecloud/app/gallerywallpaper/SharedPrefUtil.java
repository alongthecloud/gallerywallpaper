package com.alongthecloud.app.gallerywallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class SharedPrefUtil {

    private static Context context;

    public SharedPrefUtil(Context context){
        this.context = context;
    }

    public final static String PREFS_NAME = "alongthecloud.app.gallerywallpaper";

    public boolean sharedPreferenceExist(String key)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        if(!prefs.contains(key)){
            return true;
        }
        else {
            return false;
        }
    }

    public void setChangeListener(OnSharedPreferenceChangeListener listener) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,0);
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unsetChangeListener(OnSharedPreferenceChangeListener listener) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,0);
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }


    public void setInt( String key, int value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(key, 0);
    }

    public void setStr(String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getStr(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(key, "");
    }

    public void setBool(String key, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBool(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(key, false);
    }
}