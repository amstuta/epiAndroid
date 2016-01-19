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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CalendarView.OnDateChangeListener;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

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
    private JsonArray               activitiesObjects = new JsonArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        appContext = (EpiContext)getApplication();
        CalendarView calendar = (CalendarView)findViewById(R.id.calendar);
        activitiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activitiesList);

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            calendar.setShowWeekNumber(false);
            calendar.setFirstDayOfWeek(2);

            calendar.setOnDateChangeListener(new OnDateChangeListener() {

                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                    Toast.makeText(getApplicationContext(), day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
                    chosenDate = "" + year + "-" + (month + 1) + "-" + day;

                    Ion.with(getApplicationContext())
                    .load(getString(R.string.request_method_get) ,getString(R.string.api_domain) + getString(R.string.domain_planning))
                    .setBodyParameter(getString(R.string.token), appContext.token)
                    .setBodyParameter(getString(R.string.planning_start), chosenDate)
                    .setBodyParameter(getString(R.string.planning_end), chosenDate)
                    .asJsonArray()
                    .setCallback(new FutureCallback<JsonArray>() {
                        @Override
                        public void onCompleted(Exception e, JsonArray result) {
                            requestCallback(result);
                        }
                    });
                }
            });
        }
    }


    /**
     * Callback for the calendar request : when the user clicks on a date.
     * Opens a popup window to display activities for the selected date.
     * @param  result The result of the request in JSON format, containing the list of available activities.
     */
    public void requestCallback(JsonArray result) {

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
        activitiesObjects = new JsonArray();

        btnDismiss.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        date.setText(chosenDate);
        acts.setAdapter(activitiesAdapter);

        try {
            for (int i = 0; i < result.size(); ++i) {
                JsonObject tmp = result.get(i).getAsJsonObject();

                if (tmp.get(getString(R.string.can_register)).getAsBoolean()) {
                    activitiesList.add(tmp.get(getString(R.string.activity_title)).getAsString());
                    activitiesObjects.add(tmp);
                }
            }
            activitiesAdapter.notifyDataSetChanged();

        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }

        acts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JsonObject act = activitiesObjects.get(position).getAsJsonObject();
                    Intent intent = new Intent(getApplicationContext(), RegisterTokenActivity.class);

                    appContext.activity = act;
                    startActivity(intent);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        popupWindow.showAtLocation(popupView, Gravity.CENTER, Gravity.CENTER, Gravity.CENTER);
    }
}
