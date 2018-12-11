package com.example.gladyputra.logtransawit;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private static final String Shared_Pref_Name = "mysharedpref12";
    private static final String Key_Username = "username";
    private static final String Key_Password = "password";
    private static final String Key_Flag = "flag";
    private static final String Key_Distribusi = "id_dist";

    private SharedPrefManager (Context context){
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance (Context context) {
        if (mInstance == null)
        {
            mInstance = new SharedPrefManager(context);
        }
        return  mInstance;
    }

    public boolean isLoggedin()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Shared_Pref_Name, Context.MODE_PRIVATE);
        if(sharedPreferences.getString(Key_Username,null)!=null)
        {
            return true;
        }
        return false;
    }

    public boolean logout()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Shared_Pref_Name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    //get Data Username
    public String getUserName()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Shared_Pref_Name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Key_Username,null);
    }

    //get Data Flag
    public String getFlag()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Shared_Pref_Name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Key_Flag,null);
    }

    //get Data Distribusi
    public String getDistribusi()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Shared_Pref_Name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Key_Distribusi,null);
    }

    //set Password User
    public String getPassword()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Shared_Pref_Name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Key_Password,null);
    }

    public String setUsername(String username)
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Shared_Pref_Name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Key_Username,username);

        editor.apply();

        return username;
    }

    public String setPassword(String password)
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Shared_Pref_Name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Key_Password,password);

        editor.apply();

        return password;
    }

    public String setFlag(String flag)
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Shared_Pref_Name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Key_Flag,flag);

        editor.apply();

        return flag;
    }

    public String setDistribusi(String id_dist)
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Shared_Pref_Name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Key_Distribusi,id_dist);

        editor.apply();

        return id_dist;
    }
}
