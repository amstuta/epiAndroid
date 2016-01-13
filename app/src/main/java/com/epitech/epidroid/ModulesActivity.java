package com.epitech.epidroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ModulesActivity extends ActionBarActivity {

    private EpiContext              appContext = null;
    private ArrayAdapter<String>    adapter;
    private ArrayList<String>       arrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules);

        appContext = (EpiContext)getApplication();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        if (appContext.token == null) {
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
        }

        RequestAPI reqHandler = new RequestAPI();

        HashMap<String, String> netOpts = new HashMap<String, String>();
        netOpts.put(getString(R.string.domain), getString(R.string.domain_modules));
        netOpts.put(getString(R.string.request_method), getString(R.string.request_method_get));
        netOpts.put(getString(R.string.callback), getString(R.string.callback_modules));

        HashMap<String, String> args = new HashMap<String, String>();
        args.put(getString(R.string.token), appContext.token);

        reqHandler.execute(this, netOpts, args);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_home:
                finish();
                break;

            case R.id.action_settings:
                if (appContext.token == null) {
                    Intent in = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(in);
                }
                else {
                    Intent in = new Intent(getApplicationContext(), DisconnectActivity.class);
                    startActivity(in);
                }
                break;
            default:
                break;
        }

        return true;
    }


    public void modulesCallback(JSONObject result) {

        if (result == null) {
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {

            JSONArray mods = result.getJSONArray(getString(R.string.domain_modules));
            ListView msgs = (ListView)findViewById(R.id.modules);

            msgs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getBaseContext(), "YOLO", Toast.LENGTH_SHORT).show();
                }
            });

            msgs.setAdapter(adapter);
            for (int i=0; i < mods.length(); ++i) {
                arrayList.add(Html.fromHtml(mods.getJSONObject(i).getString(getString(R.string.title))).toString());
                adapter.notifyDataSetChanged();
            }
        }
        catch (Exception e) {
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
