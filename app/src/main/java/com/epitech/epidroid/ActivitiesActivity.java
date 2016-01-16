package com.epitech.epidroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivitiesActivity extends AbstractActivity {

    private CharSequence            mTitle;
    private EpiContext              appContext;
    private ArrayList<String>       activitiesList = new ArrayList<String>();
    private ArrayAdapter<String>    activitiesAdapter;
    private JSONArray               activitiesArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);

        mTitle = getTitle();
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        appContext = (EpiContext)getApplication();
        activitiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activitiesList);

        try {
            JSONObject infos = appContext.userInfos;
            activitiesArray = infos.getJSONObject(getString(R.string.board)).getJSONArray(getString(R.string.activites));
        }
        catch (JSONException e) {
            e.printStackTrace();
            finish();
        }

        final ListView activities = (ListView)findViewById(R.id.list_activities);
        activities.setAdapter(activitiesAdapter);


        activities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (activitiesArray == null)
                    return;
                try {
                    JSONObject activity = activitiesArray.getJSONObject(position);

                    Toast.makeText(getApplicationContext(), activity.getString(getString(R.string.title)), Toast.LENGTH_SHORT).show();
                    // TODO: ouvrir popup & proposer de s'inscrire ou de rentrer un token
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        getActivities();
    }


    private void getActivities() {
        try {
            for (int i=0; i < activitiesArray.length(); ++i) {
                String title = activitiesArray.getJSONObject(i).getString(getString(R.string.title));
                activitiesList.add(title);
            }
            activitiesAdapter.notifyDataSetChanged();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            case 3:
                mTitle = getString(R.string.title_section6);
                break;
            case 4:
                mTitle = getString(R.string.title_section2);
                Intent inte = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(inte);
                break;
            case 5:
                mTitle = getString(R.string.title_section3);
                Intent i = new Intent(getApplicationContext(), ModulesActivity.class);
                startActivity(i);
                break;
            case 6:
                mTitle = getString(R.string.title_section4);
                Intent inten = new Intent(getApplicationContext(), ProjectsActivity.class);
                startActivity(inten);
                break;
            case 7:
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
