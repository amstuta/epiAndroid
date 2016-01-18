package com.epitech.epidroid;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CalendarView.OnDateChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CalendarActivity extends AbstractActivity {

    private EpiContext              appContext = null;
    private ArrayAdapter<String>    activitiesAdapter;
    private ArrayList<String>       activitiesList = new ArrayList<String>();
    private CharSequence            mTitle;
    private String                  chosenDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        appContext = (EpiContext)getApplication();
        CalendarView calendar = (CalendarView)findViewById(R.id.calendar);
        activitiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activitiesList);

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            calendar.setShowWeekNumber(false);
            calendar.setFirstDayOfWeek(2);

            calendar.setOnDateChangeListener(new OnDateChangeListener() {

                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                    Toast.makeText(getApplicationContext(), day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
                    chosenDate = "" + year + "-" + (month + 1) + "-" + day;
                    callAPI();
                }
            });
        }
    }


    private void callAPI() {
        RequestAPI reqHandler = new RequestAPI();

        HashMap<String, String> netOpts = new HashMap<String, String>();
        netOpts.put(getString(R.string.domain), getString(R.string.domain_planning));
        netOpts.put(getString(R.string.request_method), getString(R.string.request_method_get));
        netOpts.put(getString(R.string.callback), getString(R.string.callback_info));

        HashMap<String, String> args = new HashMap<String, String>();
        args.put(getString(R.string.token), appContext.token);
        args.put(getString(R.string.planning_start), chosenDate);
        args.put(getString(R.string.planning_end), chosenDate);

        reqHandler.execute(this, netOpts, args);
    }


    public void requestCallback(JSONObject result) {

        if (result == null) {
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_calendar, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
        TextView date = (TextView)popupView.findViewById(R.id.date);
        ListView acts = (ListView)popupView.findViewById(R.id.activities);

        activitiesList.clear();
        btnDismiss.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        date.setText(chosenDate);
        acts.setAdapter(activitiesAdapter);
        try {
            JSONArray activities = result.getJSONArray(getString(R.string.response));

            for (int i=0; i < activities.length(); ++i) {

                if (activities.getJSONObject(i).getBoolean(getString(R.string.can_register))) {
                    activitiesList.add(activities.getJSONObject(i).getString(getString(R.string.activity_title)));
                    activitiesAdapter.notifyDataSetChanged();
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        popupWindow.showAtLocation(popupView, Gravity.CENTER, Gravity.CENTER, Gravity.CENTER);

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
                Intent inte = new Intent(getApplicationContext(), YearbookActivity.class);
                startActivity(inte);
                break;
            case 4:
                mTitle = getString(R.string.title_section2);
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
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


}
