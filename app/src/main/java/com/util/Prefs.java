package com.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Obrina.KIMI on 7/11/2017.
 */

public class Prefs {
    SharedPreferences preferences;
    SharedPreferences sharedPrefs;

    public Prefs(Activity activity)
    {
        preferences = activity.getPreferences(Activity.MODE_PRIVATE);
        sharedPrefs = activity.getSharedPreferences("app_monetizer", Context.MODE_PRIVATE);
    }

    public void setReceiveNotif(Boolean decision)
    {
        preferences.edit().putBoolean("receiveNotif",decision).apply();
    }

    public void setFirstLaunch(Boolean isDone)
    {
        preferences.edit().putBoolean("firstLaunch", isDone).apply();
    }

    public void setMapCount(int valueLeft)
    {
        preferences.edit().putInt("mapCount", valueLeft).apply();
    }

    public void setOnDestroyCount(int count)
    {
        preferences.edit().putInt("destroyData", count).apply();
    }

    public void setToday(Long date)
    {
        preferences.edit().putLong("today", date).apply();
    }
    // defaut Receive notif value is true
    public boolean getReceiveNotif()
    {
        return preferences.getBoolean("receiveNotif",true);
    }
    public boolean getFirstLaunch()
    {
        return preferences.getBoolean("firstLaunch", false);
    }

    public long getToday()
    {
        return preferences.getLong("today", 0L);
    }

    public int getMapCount()
    {
        return preferences.getInt("mapCount", 0);
    }

    public int getOnDestroyCount()
    {
        return preferences.getInt("destroyData", 0);
    }

    public int getAdvertiserId(){
        return  sharedPrefs.getInt("advertiser", 1);
    }

    public void setAdvertiserId(int id){
        sharedPrefs.edit().putInt("advertiser", id).apply();
    }




}
