package com.epitech.epidroid;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterTokenActivity extends ActionBarActivity {

    private EpiContext appContext;
    private JSONObject activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_token);

        appContext = (EpiContext)getApplication();

        if (appContext.activity == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
        }
        activity = appContext.activity;

        TextView title = (TextView)findViewById(R.id.activity_title);
        TextView module = (TextView)findViewById(R.id.activity_module);

        try {
            title.setText(activity.getString(getString(R.string.activity_title)));
            module.setText(activity.getString(getString(R.string.title_module)));
        }
        catch (JSONException e) {
            e.printStackTrace();
            finish();
        }
    }

/*
    private void getActivity() {
        HashMap<String, String> netOpts = new HashMap<String, String>();
        HashMap<String, String> args = new HashMap<String, String>();
        JSONObject activity = appContext.activity;
        RequestAPI reqHandler = new RequestAPI();

        netOpts.put(getString(R.string.domain), getString(R.string.domain_event));
        netOpts.put(getString(R.string.request_method), getString(R.string.request_method_get));
        netOpts.put(getString(R.string.callback), getString(R.string.callback_info));

        try {
            args.put(getString(R.string.token), appContext.token);
            args.put(getString(R.string.scolarYear), activity.getString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestCallback(JSONObject result) {

    }
    */

}
