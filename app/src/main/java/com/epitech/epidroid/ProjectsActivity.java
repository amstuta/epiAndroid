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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ProjectsActivity extends AbstractActivity {

    private NavigationDrawerFragment mNavigationDrawerFragment;
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
            final JSONArray cleanProjs = new JSONArray();

            for (int i=0; i < projs.length(); ++i) {
                if (projs.getJSONObject(i).getString(getString(R.string.acti_type)).equals(getString(R.string.proj)))
                    cleanProjs.put(projs.getJSONObject(i));
            }

            for(int i = 0; i < cleanProjs.length(); ++i) {
                if (cleanProjs.getJSONObject(i).getString(getString(R.string.acti_type)).equals(getString(R.string.proj))) {
                    projList.add(cleanProjs.getJSONObject(i).getString(getString(R.string.activity_title)));
                    projAdapter.notifyDataSetChanged();
                }
            }

            ListView projDisp = (ListView)findViewById(R.id.project_title);

            projDisp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        final JSONObject clicked = cleanProjs.getJSONObject(position);
                        final int registered = clicked.getInt(getString(R.string.registered));

                        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = layoutInflater.inflate(R.layout.popup_projects, null);
                        final PopupWindow popupWindow = new PopupWindow(popupView,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);

                        TextView title = (TextView) popupView.findViewById(R.id.project_name);
                        TextView reg = (TextView) popupView.findViewById(R.id.project_registered);
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
                                    unregisterRequest(clicked);
                                } else {
                                    registerRequest(clicked);
                                }
                            }
                        });

                        title.setText(clicked.getString(getString(R.string.activity_title)));
                        if (registered == 1) {
                            reg.setText("You are registered.");
                            regButton.setText("Unregister");
                        } else {
                            reg.setText("You are not registered");
                            regButton.setText("Register");
                        }

                        popupWindow.showAtLocation(popupView, Gravity.RIGHT, Gravity.CENTER, Gravity.CENTER);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void registerRequest(JSONObject project) {
        RequestAPI reqHandler = new RequestAPI();
        HashMap<String, String> netOpts = new HashMap<String, String>();
        HashMap<String, String> args = new HashMap<String, String>();

        try {
            netOpts.put(getString(R.string.request_method), getString(R.string.request_method_post));
            netOpts.put(getString(R.string.domain), getString(R.string.domain_project));
            netOpts.put(getString(R.string.callback), getString(R.string.callback_project));
            args.put(getString(R.string.token), app.token);
            args.put(getString(R.string.scolarYear), project.getString(getString(R.string.scolarYear)));
            args.put(getString(R.string.codeModule), project.getString(getString(R.string.codeModule)));
            args.put(getString(R.string.codeInstance), project.getString(getString(R.string.codeInstance)));
            args.put(getString(R.string.codeActivity), project.getString(getString(R.string.codeActivity)));

            Toast.makeText(getApplicationContext(), "You registered", Toast.LENGTH_SHORT).show();
            reqHandler.execute(this, netOpts, args);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void unregisterRequest(JSONObject project) {
        RequestAPI reqHandler = new RequestAPI();
        HashMap<String, String> netOpts = new HashMap<String, String>();
        HashMap<String, String> args = new HashMap<String, String>();

        try {
            netOpts.put(getString(R.string.request_method), getString(R.string.request_method_delete));
            netOpts.put(getString(R.string.domain), getString(R.string.domain_project));
            netOpts.put(getString(R.string.callback), getString(R.string.callback_project));
            args.put(getString(R.string.token), app.token);
            args.put(getString(R.string.scolarYear), project.getString(getString(R.string.scolarYear)));
            args.put(getString(R.string.codeModule), project.getString(getString(R.string.codeModule)));
            args.put(getString(R.string.codeInstance), project.getString(getString(R.string.codeInstance)));
            args.put(getString(R.string.codeActivity), project.getString(getString(R.string.codeActivity)));

            Toast.makeText(getApplicationContext(), "You unregistered", Toast.LENGTH_SHORT).show();
            reqHandler.execute(this, netOpts, args);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void projectCallback(JSONObject result) {
    }
}
