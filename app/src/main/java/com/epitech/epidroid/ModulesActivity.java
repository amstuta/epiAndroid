package com.epitech.epidroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ModulesActivity extends AbstractActivity {

    private EpiContext              appContext = null;
    private ArrayAdapter<String>    mAdapter;
    private ArrayList<String>       mArrayList = new ArrayList<String>();
    private ArrayAdapter<String>    vAdapter;
    private ArrayList<String>       vArrayList = new ArrayList<String>();
    private CharSequence            mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules);

        appContext = (EpiContext)getApplication();
        vAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vArrayList);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mArrayList);

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout));

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


    public void modulesCallback(JSONObject result) {

        if (result == null) {
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            JSONArray mods = result.getJSONArray(getString(R.string.domain_modules));
            Spinner msgs = (Spinner)findViewById(R.id.modules);
            ListView lst = (ListView)findViewById(R.id.modules_title);
            final ArrayList<JSONObject>[] modules = getDick(mods);

            msgs.setAdapter(vAdapter);
            lst.setAdapter(mAdapter);

            msgs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView parent, View view, int position, long id) {

                    Toast.makeText(getBaseContext(), "Semester " + ++position + " selected", Toast.LENGTH_SHORT).show();
                    if (position < modules.length && modules[position] != null) {
                        ArrayList<JSONObject> tmp = modules[position];

                        mArrayList.clear();
                        mAdapter.notifyDataSetChanged();

                        try {
                            for (int i = 0; i < tmp.size(); ++i) {
                                String modName = tmp.get(i).getString(getString(R.string.title));

                                mArrayList.add(modName);
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView view) {
                }
            });

            for (int i = 1; i < modules.length && modules[i] != null; ++i) {
                vArrayList.add("" + i);
                vAdapter.notifyDataSetChanged();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private ArrayList<JSONObject>[] getDick(JSONArray mods) {
        if (mods == null)
            return null;

        ArrayList<JSONObject>[] res = new ArrayList[11];

        for (int i = 0; i < mods.length(); ++i) {

            try {
                JSONObject tmp = mods.getJSONObject(i);

                int sem = tmp.getInt(getString(R.string.semester));

                if (res[sem] == null) {
                    res[sem] = new ArrayList<JSONObject>();
                }
                res[sem].add(tmp);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return res;
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 2:
                mTitle = getString(R.string.title_section1);
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                break;
            case 3:
                mTitle = getString(R.string.title_section2);
                Intent inte = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(inte);
                break;
            case 4:
                mTitle = getString(R.string.title_section3);
                break;
            case 5:
                mTitle = getString(R.string.title_section4);
                Intent inten = new Intent(getApplicationContext(), ProjectsActivity.class);
                startActivity(inten);
                break;
            case 6:
                mTitle = getString(R.string.title_section5);
                Intent in = new Intent(getApplicationContext(), DisconnectActivity.class);
                startActivity(in);
                break;
        }
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


}
