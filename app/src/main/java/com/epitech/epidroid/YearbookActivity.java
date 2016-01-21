package com.epitech.epidroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class YearbookActivity extends AbstractActivity implements AdapterView.OnItemSelectedListener,
        AdapterView.OnItemClickListener {


    private CharSequence mTitle;
    private EpiContext appContext = null;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mArrayList = new ArrayList<String>();
    private ArrayAdapter<String> vAdapter;
    private ArrayList<String> vArrayList = new ArrayList<String>();
    private ArrayAdapter<String> locAdapter;
    private ArrayList<String> locArrayList = new ArrayList<String>();

    private ArrayList<String> activitiesArrayList = new ArrayList<String>();
    private ArrayAdapter<String> activitiesAdapter;
    private String currentChoice = "tek1";
    private String currentLoc = "FR/PAR";
    private int offset = 0;
    private ProgressDialog load = null;
    private Boolean isLoading = false;
    private HashMap<String, JsonObject> studentInf = null;
    //private View mLoginFormView;
    //private View mProgressView;
    //private PopupWindow pWIndow;
    //private View pView;


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
        locAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locArrayList);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mArrayList);
        activitiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activitiesArrayList);
        if (appContext.token == null) {
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
        }
        studentInf = new HashMap<String, JsonObject>();
        Spinner msgs = (Spinner) findViewById(R.id.yearbook_promo);
        Spinner loc = (Spinner) findViewById(R.id.yearbook_location);
        ListView disp = (ListView) findViewById(R.id.yearbook_list);
        disp.setOnItemClickListener(this);
        disp.setAdapter(mAdapter);
        msgs.setAdapter(vAdapter);
        loc.setAdapter(locAdapter);
        vArrayList.add(getString(R.string.tek1));
        vArrayList.add(getString(R.string.tek2));
        vArrayList.add(getString(R.string.tek3));
        vArrayList.add(getString(R.string.tek4));
        vArrayList.add(getString(R.string.tek5));
        vAdapter.notifyDataSetChanged();
        locAdapter.add(getString(R.string.paris));
        locAdapter.add(getString(R.string.montpelliers));
        locAdapter.add(getString(R.string.bordeaux));
        locAdapter.add(getString(R.string.nice));
        locAdapter.add(getString(R.string.nancy));
        locAdapter.add(getString(R.string.nantes));
        locAdapter.add(getString(R.string.strasbourgs));
        locAdapter.add(getString(R.string.lyon));
        locAdapter.add(getString(R.string.rennes));
        locAdapter.add(getString(R.string.lille));
        locAdapter.add(getString(R.string.marseille));
        locAdapter.notifyDataSetChanged();
        msgs.setOnItemSelectedListener(this);
        loc.setOnItemSelectedListener(this);
    }


    /**
     * Callback for the yearbook getting request.
     * Displays the received list of students, and calls `onItemSelected` if there are more.
     * @param result The request response in JSON format
     */
    public void yearbookCallback(JsonObject result) {
        try {
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


    /**
     * Called when an item is selected in the spinner.
     * Makes the request to get a list of students fot the selected promotion.
     * Displays a progress bar too.
     * @param position Index of the selected item.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner msgs = (Spinner) findViewById(R.id.yearbook_promo);
        Spinner loci = (Spinner) findViewById(R.id.yearbook_location);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            load.setTitle("loading");
            load.setMessage("retrieving students infos...");
            load.setCanceledOnTouchOutside(false);
            if (!load.isShowing()) {
                load.show();
            }
            isLoading = true;
        }

        if (!(((Spinner) findViewById(R.id.yearbook_promo)).getSelectedItem().toString().equals(currentChoice)) ||
                !(((Spinner) findViewById(R.id.yearbook_location)).getSelectedItem().toString().equals(currentLoc))) {
            mArrayList.clear();
            mAdapter.clear();
            studentInf.clear();
            mAdapter.notifyDataSetChanged();
            offset = 0;
            currentChoice = ((Spinner) findViewById(R.id.yearbook_promo)).getSelectedItem().toString();
            currentLoc = ((Spinner) findViewById(R.id.yearbook_location)).getSelectedItem().toString();
        }
        Ion.with(getApplicationContext())
                .load(getString(R.string.request_method_get), getString(R.string.api_domain) + getString(R.string.domain_yearbook))
                .setBodyParameter(getString(R.string.token), appContext.token)
                .setBodyParameter("year", "" + (Calendar.getInstance().get(Calendar.YEAR) - 1))
                .setBodyParameter("location", loci.getSelectedItem().toString())
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


    /**
     * Called when the user clicked on a student's login.
     * Makes the request to get the selected student's informations.
     * @param position Index of the selected login
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra(getString(R.string.prompt_login), studentInf.get(mArrayList.get(position)).get("login").getAsString());
            intent.putExtra("picture", studentInf.get(mArrayList.get(position)).get("picture").getAsString());
            startActivity(intent);
            Toast.makeText(this, studentInf.get(mArrayList.get(position)).get("title").getAsString(), Toast.LENGTH_SHORT).show();
        } catch (Exception E) {
            E.printStackTrace();
        }
    }
}
