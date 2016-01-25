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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ProjectsActivity extends AbstractActivity {


    private NavigationDrawerFragment    mNavigationDrawerFragment;
    private CharSequence                mTitle;
    private ArrayList<String>           projList = new ArrayList<String>();
    private ArrayAdapter<String>        projAdapter;
    private EpiContext                  app;
    private JsonArray                   cleanProjects = new JsonArray();
    private JsonArray                   registeredProjects = new JsonArray();

    /* Spinner */
    private ArrayList<String>           filterList = new ArrayList<String>();
    private ArrayAdapter<String>        filterAdapter;
    private int                         selected = 0;


    /**
     * Automatically called whenever this activity is started.
     * will pre-set the fields and retrieves the user's current projects.
     * @param savedInstanceState bundle sent from previous activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        app = (EpiContext)getApplication();
        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,(DrawerLayout)findViewById(R.id.drawer_layout));
        projAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, projList);
        filterAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filterList);


        ListView projDisp = (ListView)findViewById(R.id.project_title);
        projDisp.setAdapter(projAdapter);
        projDisp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final JsonObject selectedItem;
                if (selected == 0) {
                    selectedItem = cleanProjects.get(position).getAsJsonObject();
                }
                else {
                    selectedItem = registeredProjects.get(position).getAsJsonObject();
                }
                openPopup(selectedItem);
            }
        });


        Spinner fSpinner = (Spinner)findViewById(R.id.filter);
        fSpinner.setAdapter(filterAdapter);

        filterList.add(getString(R.string.filter_all_projects));
        filterList.add(getString(R.string.filter_registered_projects));
        filterAdapter.notifyDataSetChanged();

        fSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selected = 0;
                    displayAll();
                }
                else {
                    selected = 1;
                    displayRegistered();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Ion.with(getApplicationContext())
                .load(getString(R.string.request_method_get), getString(R.string.api_domain) + getString(R.string.domain_projects))
                .setBodyParameter(getString(R.string.token), app.token)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        filterProjects(result);
                        displayAll();
                    }
                });
    }


    private void filterProjects(JsonArray projects) {
        try {
            for (int i=0; i < projects.size() ; ++i) {

                JsonObject tmp = projects.get(i).getAsJsonObject();

                if (tmp.get(getString(R.string.acti_type)).getAsString().equals(getString(R.string.proj))) {
                    cleanProjects.add(projects.get(i));

                    if (tmp.get(getString(R.string.registered)).getAsInt() == 1)
                        registeredProjects.add(tmp);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void displayAll() {
        projList.clear();
        for(int i = 0; i < cleanProjects.size(); ++i) {
            projList.add(cleanProjects.get(i).getAsJsonObject().get(getString(R.string.activity_title)).getAsString());
        }
        projAdapter.notifyDataSetChanged();
    }


    private void displayRegistered() {
        projList.clear();
        for(int i = 0; i < registeredProjects.size(); ++i) {
            projList.add(registeredProjects.get(i).getAsJsonObject().get(getString(R.string.activity_title)).getAsString());
        }
        projAdapter.notifyDataSetChanged();
    }


    /**
     * Callback for the projects getting request.
     * Opens a popup with the selected project informations.
     * @param selected Selected project in the projects list.
     */
    public void openPopup(final JsonObject selected) {
        try {
            final int registered = selected.get(getString(R.string.registered)).getAsInt();

            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.popup_projects, null);
            final PopupWindow popupWindow = new PopupWindow(popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView title = (TextView) popupView.findViewById(R.id.project_name);
            TextView reg = (TextView) popupView.findViewById(R.id.project_registered);
            TextView mod = (TextView)popupView.findViewById(R.id.project_module);
            Button regButton = (Button) popupView.findViewById(R.id.register);
            Button btnDismiss = (Button) popupView.findViewById(R.id.dismiss);


            btnDismiss.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

            regButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (registered == 1) {
                        unregisterRequest(selected);
                    } else {
                        registerRequest(selected);
                    }
                }
            });

            title.setText(selected.get(getString(R.string.activity_title)).getAsString());
            mod.setText(selected.get(getString(R.string.title_module2)).getAsString());

            if (registered == 1) {
                reg.setText("You are registered.");
                regButton.setText("Unregister");
            } else {
                reg.setText("You are not registered");
                regButton.setText("Register");
            }

            popupWindow.showAtLocation(popupView, Gravity.RIGHT, Gravity.CENTER, Gravity.CENTER);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void registerRequest(JsonObject project) {
        try {
            Ion.with(getApplicationContext())
                    .load(getString(R.string.api_domain) + getString(R.string.domain_project))
                    .setBodyParameter(getString(R.string.token), app.token)
                    .setBodyParameter(getString(R.string.scolarYear), project.get(getString(R.string.scolarYear)).getAsString())
                    .setBodyParameter(getString(R.string.codeModule), project.get(getString(R.string.codeModule)).getAsString())
                    .setBodyParameter(getString(R.string.codeInstance), project.get(getString(R.string.codeInstance)).getAsString())
                    .setBodyParameter(getString(R.string.codeActivity), project.get(getString(R.string.codeActivity)).getAsString())
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                }
            });
            Toast.makeText(getApplicationContext(), getString(R.string.you_registered), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void unregisterRequest(JsonObject project) {
        try {
            Ion.with(getApplicationContext())
                    .load(getString(R.string.request_method_delete), getString(R.string.api_domain) + getString(R.string.domain_project))
                    .setBodyParameter(getString(R.string.token), app.token)
                    .setBodyParameter(getString(R.string.scolarYear), project.get(getString(R.string.scolarYear)).getAsString())
                    .setBodyParameter(getString(R.string.codeModule), project.get(getString(R.string.codeModule)).getAsString())
                    .setBodyParameter(getString(R.string.codeInstance), project.get(getString(R.string.codeInstance)).getAsString())
                    .setBodyParameter(getString(R.string.codeActivity), project.get(getString(R.string.codeActivity)).getAsString())
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                }
            });
            Toast.makeText(getApplicationContext(), getString(R.string.you_unregistered), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
