package com.epitech.epidroid;

import android.app.Application;
import com.google.gson.JsonObject;


public class EpiContext extends Application{
    public String       token = null;
    public JsonObject   userInfos = null;
    public JsonObject   activity = null;
}
