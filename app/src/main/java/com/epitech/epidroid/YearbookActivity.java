package com.epitech.epidroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class YearbookActivity extends AbstractActivity implements AdapterView.OnItemSelectedListener,
        AdapterView.OnItemClickListener {


    private CharSequence mTitle;
    private EpiContext appContext = null;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mArrayList = new ArrayList<String>();
    private ArrayAdapter<String> vAdapter;
    private ArrayList<String> vArrayList = new ArrayList<String>();
    private ArrayList<String> activitiesArrayList = new ArrayList<String>();
    private ArrayAdapter<String> activitiesAdapter;
    private String currentChoice = "tek1";
    private int offset = 0;
    private ProgressDialog load = null;
    private Boolean isLoading = false;
    private HashMap<String, JsonObject> studentInf = null;
    private View mLoginFormView;
    private View mProgressView;
    private PopupWindow pWIndow;
    private View pView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yearbook);
        appContext = (EpiContext) getApplication();
        load = new ProgressDialog(this);
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        vAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vArrayList);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mArrayList);
        activitiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activitiesArrayList);
        if (appContext.token == null) {
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
        }
        studentInf = new HashMap<String, JsonObject>();
        Spinner msgs = (Spinner) findViewById(R.id.yearbook_promo);
        ListView disp = (ListView) findViewById(R.id.yearbook_list);
        disp.setOnItemClickListener(this);
        disp.setAdapter(mAdapter);
        msgs.setAdapter(vAdapter);
        vArrayList.add(getString(R.string.tek1));
        vArrayList.add(getString(R.string.tek2));
        vArrayList.add(getString(R.string.tek3));
        vAdapter.notifyDataSetChanged();
        msgs.setOnItemSelectedListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

    public void yearbookCallback(JsonObject result) {
        try {

            System.out.println(result);
            JsonArray students;
            if (result.get(getString(R.string.items)).isJsonArray()) {
                students = result.get(getString(R.string.items)).getAsJsonArray();
            }
            else
                return;
            for (int i = 0; i < students.size(); ++i) {
                mArrayList.add(students.get(i).getAsJsonObject().get("login").getAsString());
                studentInf.put(students.get(i).getAsJsonObject().get("login").getAsString(), students.get(i).getAsJsonObject());

            }
            offset += students.size();
            mAdapter.notifyDataSetChanged();
            if (students.size() > 1)
                onItemSelected(null, null, 0, 0);
            else {
                load.dismiss();
                isLoading = false;
            }
        } catch (Exception e) {
            isLoading = false;
            load.dismiss();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner msgs = (Spinner) findViewById(R.id.yearbook_promo);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            load.setTitle("loading");
            load.setMessage("retrieving students infos...");
            load.setCanceledOnTouchOutside(false);
            if (!load.isShowing()) {
                load.show();
            }
            isLoading = true;
        }

        if (!(((Spinner) findViewById(R.id.yearbook_promo)).getSelectedItem().toString().equals(currentChoice))) {
            mArrayList.clear();
            mAdapter.clear();
            studentInf.clear();
            mAdapter.notifyDataSetChanged();
            offset = 0;
            currentChoice = ((Spinner) findViewById(R.id.yearbook_promo)).getSelectedItem().toString();
        }
        Ion.with(getApplicationContext())
                .load(getString(R.string.request_method_get), getString(R.string.api_domain) + getString(R.string.domain_yearbook))
                .setBodyParameter(getString(R.string.token), appContext.token)
                .setBodyParameter("year", "2015")
                .setBodyParameter("location", "FR/PAR")
                .setBodyParameter("promo", msgs.getSelectedItem().toString())
                .setBodyParameter("offset", String.valueOf(offset))
        .asJsonObject()
        .setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                yearbookCallback(result);
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            Toast.makeText(this, studentInf.get(mArrayList.get(position)).get("title").getAsString(), Toast.LENGTH_SHORT).show();
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Yearbook Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.epitech.epidroid/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Yearbook Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.epitech.epidroid/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
