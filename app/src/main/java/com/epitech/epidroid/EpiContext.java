package com.epitech.epidroid;

import android.app.Application;

import com.google.gson.JsonObject;

import org.json.JSONObject;

public class EpiContext extends Application{
    public String       token = null;
    //public JSONObject   userInfos = null;
    public JsonObject   userInfos = null;
    public JSONObject   activity = null;
}
