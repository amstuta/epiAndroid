package com.epitech.epidroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class YearbookActivity extends AbstractActivity implements AdapterView.OnItemSelectedListener,
        AdapterView.OnItemClickListener {


    private CharSequence mTitle;
    private EpiContext              appContext = null;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mArrayList = new ArrayList<String>();
    private ArrayAdapter<String>    vAdapter;
    private ArrayList<String>       vArrayList = new ArrayList<String>();
    private ArrayList<String>       activitiesArrayList = new ArrayList<String>();
    private ArrayAdapter<String>    activitiesAdapter;
    private String                  currentChoice = "tek1";
    private int                     offset = 0;
    private ProgressDialog          load = null;
    private Boolean                 isLoading = false;
    private HashMap<String, JSONObject> studentInf = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yearbook);
        appContext = (EpiContext)getApplication();
        load = new ProgressDialog(this);
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        vAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vArrayList);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mArrayList);
        activitiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activitiesArrayList);
        if (appContext.token == null) {
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
        }

        Spinner msgs = (Spinner)findViewById(R.id.yearbook_promo);
        ListView disp = (ListView)findViewById(R.id.yearbook_list);
        disp.setOnItemClickListener(this);
        disp.setAdapter(mAdapter);
        msgs.setAdapter(vAdapter);
        vArrayList.add(getString(R.string.tek1));
        vArrayList.add(getString(R.string.tek2));
        vArrayList.add(getString(R.string.tek3));
        vAdapter.notifyDataSetChanged();
        msgs.setOnItemSelectedListener(this);
    }


    public void onSectionAttached(int number) {
        switch (number) {
            case 2:
                mTitle = getString(R.string.title_section1);
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
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
                Intent intent = new Intent(getApplicationContext(), ModulesActivity.class);
                startActivity(intent);
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

    public void yearbookCallback(JSONObject result)
    {
        try {
            /*if (((Spinner)findViewById(R.id.yearbook_promo)).getSelectedItem().toString() != currentChoice) {
                mArrayList.clear();
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                offset = 0;
                currentChoice = ((Spinner)findViewById(R.id.yearbook_promo)).getSelectedItem().toString();
            }*/
            JSONArray students = result.getJSONArray(getString(R.string.items));
            for (int i = 0; i < students.length();++i)
            {
                mArrayList.add(students.getJSONObject(i).getString("login"));
            }
            offset += students.length();
            mAdapter.notifyDataSetChanged();
            if (students.length() > 1)
                onItemSelected(null, null, 0, 0);
            else {
                load.dismiss();
                isLoading = false;
            }
        }
        catch (Exception e) {
            isLoading = false;
            load.dismiss();
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //ProgressDialog.show(this, "Loading", "Wait while loading...");
        Spinner msgs = (Spinner)findViewById(R.id.yearbook_promo);
        RequestAPI reqHandler = new RequestAPI();
        if (Looper.myLooper() == Looper.getMainLooper()) {

            studentInf = new HashMap<String, JSONObject>();
            load.setTitle("loading");
            load.setMessage("retrieving students infos...");
            load.setCanceledOnTouchOutside(false);
            if (!load.isShowing()) {
                load.show();
            }
            isLoading = true;
        }

        HashMap<String, String> netOpts = new HashMap<String, String>();
        netOpts.put(getString(R.string.domain), getString(R.string.domain_yearbook));
        netOpts.put(getString(R.string.request_method), getString(R.string.request_method_get));
        netOpts.put(getString(R.string.callback), getString(R.string.callback_yearbook));


        if (!(((Spinner)findViewById(R.id.yearbook_promo)).getSelectedItem().toString().equals(currentChoice))) {
            mArrayList.clear();
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
            offset = 0;
            currentChoice = ((Spinner)findViewById(R.id.yearbook_promo)).getSelectedItem().toString();
        }

        HashMap<String, String> args = new HashMap<String, String>();
        args.put(getString(R.string.token), appContext.token);
        args.put("year", "2015");
        args.put("location", "FR/PAR");
        args.put("promo", msgs.getSelectedItem().toString());
        args.put("offset", String.valueOf(offset));

        reqHandler.execute(this, netOpts, args);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
