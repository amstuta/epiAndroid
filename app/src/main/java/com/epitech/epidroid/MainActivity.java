package com.epitech.epidroid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.view.*;
import 	android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class MainActivity extends ActionBarActivity {

    private ImageRequest    imgHandler = new ImageRequest();
    private EpiContext      appContext;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList = new ArrayList<String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appContext = (EpiContext)getApplication();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        if (appContext.token == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);

            finish();
        }
        else {
            RequestAPI reqHandler = new RequestAPI();
            HashMap<String, String> netOptions = new HashMap<String, String>();
            netOptions.put(getString(R.string.request_method), getString(R.string.request_method_post));
            netOptions.put(getString(R.string.domain), getString(R.string.domain_infos));
            netOptions.put(getString(R.string.callback), getString(R.string.callback_info));

            HashMap<String, String> args = new HashMap<String, String>();
            args.put(getString(R.string.token), appContext.token);

            reqHandler.execute(this, netOptions, args);
        }
    }


    public void requestCallback(JSONObject result) {

        if (result != null)
            appContext.userInfos = result;

        try {
            JSONObject infos = result.getJSONObject(getString(R.string.domain_infos));
            String picture = infos.getString(getString(R.string.picture));

            imgHandler.execute(this, picture);
            displayInfos();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void imageCallback(Bitmap image) {
        ImageView img = (ImageView)findViewById(R.id.profileImg);
        img.setImageBitmap(image);
    }


    private void displayInfos() {
        JSONObject infos = appContext.userInfos;

        if (infos == null)
            return;

        try {
            JSONObject inf = infos.getJSONObject(getString(R.string.domain_infos));
            String login = inf.getString(getString(R.string.domain_login));
            String title = inf.getString(getString(R.string.title));

            TextView ttl = (TextView)findViewById(R.id.title);
            ttl.setText(title);

            String messages = "";
            JSONArray history = infos.getJSONArray(getString(R.string.history));

            ListView msgs = (ListView)findViewById(R.id.messages);
            msgs.setAdapter(adapter);

            for (int i=0; i < history.length(); ++i) {
                arrayList.add(Html.fromHtml(history.getJSONObject(i).getString(getString(R.string.title))).toString());
                adapter.notifyDataSetChanged();
            }

            RequestAPI reqHandler = new RequestAPI();
            HashMap<String, String> netOpts = new HashMap<String, String>();
            netOpts.put(getString(R.string.request_method), getString(R.string.request_method_get));
            netOpts.put(getString(R.string.domain), getString(R.string.domain_user));
            netOpts.put(getString(R.string.callback), getString(R.string.callback_user));

            HashMap<String, String> args = new HashMap<String, String>();
            args.put(getString(R.string.token), appContext.token);
            args.put(getString(R.string.domain_user), login);

            reqHandler.execute(this, netOpts, args);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void userCallback(JSONObject result) {
        if (result == null)
            return;
        try {

            JSONObject ns = result.getJSONObject(getString(R.string.ns_stat));
            String timeActive = ns.getString(getString(R.string.ns_stat_active));

            TextView msgsN = (TextView)findViewById(R.id.logTime);
            msgsN.setText("Netsoul: " + timeActive);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_refresh:
                break;

            case R.id.action_settings:
                if (appContext.token == null) {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(getApplicationContext(), DisconnectActivity.class);
                    startActivity(i);
                }
                break;
            default:
                break;
        }

        return true;
    }
}
