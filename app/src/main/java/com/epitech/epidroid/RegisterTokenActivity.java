package com.epitech.epidroid;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
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
        TextView registered = (TextView)findViewById(R.id.activity_registered);

        try {
            title.setText(activity.getString(getString(R.string.activity_title)));
            module.setText(activity.getString(getString(R.string.title_module)));
            registered.setText(activity.getString("event_registered"));
        }
        catch (JSONException e) {
            e.printStackTrace();
            finish();
        }

        Button ret = (Button)findViewById(R.id.return_button);
        Button reg = (Button)findViewById(R.id.register_button);
        // TODO: set a unregister si registered

        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerToActivity();
            }
        });
    }


    private void registerToActivity() {
        HashMap<String, String> netOpts = new HashMap<String, String>();
        HashMap<String, String> args = new HashMap<String, String>();
        RequestAPI reqHandler = new RequestAPI();

        netOpts.put(getString(R.string.domain), getString(R.string.domain_event));
        netOpts.put(getString(R.string.request_method), getString(R.string.request_method_get));
        netOpts.put(getString(R.string.callback), getString(R.string.callback_info));

        try {
            args.put(getString(R.string.token), appContext.token);
            args.put(getString(R.string.scolarYear), activity.getString(getString(R.string.scolarYear)));
            args.put(getString(R.string.codeModule), activity.getString(getString(R.string.codeModule)));
            args.put(getString(R.string.codeInstance), activity.getString(getString(R.string.codeInstance)));
            args.put(getString(R.string.codeActivity), activity.getString(getString(R.string.codeActivity)));
            args.put(getString(R.string.codeEvent), activity.getString(getString(R.string.codeEvent)));
        }
        catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Toast.makeText(getApplicationContext(), "Registered succesfully", Toast.LENGTH_SHORT).show();
        reqHandler.execute(this, netOpts, args);
    }

    public void requestCallback(JSONObject result) {

    }
}
