package com.epitech.epidroid;

import android.app.Application;

import org.json.JSONObject;

public class EpiContext extends Application{
    public String       token = null;
    public JSONObject   userInfos = null;
    public JSONObject   activity = null;
}
