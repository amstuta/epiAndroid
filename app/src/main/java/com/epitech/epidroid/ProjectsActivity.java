package com.epitech.epidroid;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ProjectsActivity extends AbstractActivity {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private ArrayList<String> projList = new ArrayList<String>();
    private ArrayAdapter<String> projAdapter;
    private EpiContext app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        app = (EpiContext)getApplication();
        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,(DrawerLayout)findViewById(R.id.drawer_layout));
        projAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, projList);
        ListView projDisp = (ListView)findViewById(R.id.project_title);
        projDisp.setAdapter(projAdapter);
        retrieveProjects();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    private void retrieveProjects(){
        HashMap<String, String> netOptions = new HashMap<String, String>();
        HashMap<String, String> args = new HashMap<String, String>();
        RequestAPI reqHandler = new RequestAPI();

        netOptions.put(getString(R.string.domain), getString(R.string.domain_projects));
        netOptions.put(getString(R.string.request_method), getString(R.string.request_method_get));
        netOptions.put(getString(R.string.callback), getString(R.string.callback_info));
        args.put(getString(R.string.token), app.token);
        reqHandler.execute(this, netOptions, args);
    }

    public void requestCallback(JSONObject response){

        if (response == null)
            return;
        try {
            JSONArray projs = response.getJSONArray(getString(R.string.response));
            for(int i = 0; i < projs.length(); ++i) {
                if (projs.getJSONObject(i).getString(getString(R.string.acti_type)).equals(getString(R.string.proj))) {
                    projList.add(projs.getJSONObject(i).getString(getString(R.string.activity_title)));
                    projAdapter.notifyDataSetChanged();
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 2:
                mTitle = getString(R.string.title_section1);
                Intent inter = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(inter);
                break;
            case 3:
                mTitle = getString(R.string.title_section2);
                Intent inte = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(inte);

                break;
            case 4:
                mTitle = getString(R.string.title_section3);
                Intent i = new Intent(getApplicationContext(), ModulesActivity.class);
                startActivity(i);
                break;
            case 5:
                mTitle = getString(R.string.title_section4);
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
