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

        Ion.with(getApplicationContext())
                .load(getString(R.string.request_method_get), getString(R.string.api_domain) + getString(R.string.domain_projects))
                .setBodyParameter(getString(R.string.token), app.token)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        requestCallback(result);
                    }
                });
    }


    public void requestCallback(JsonArray response) {

        if (response == null)
            return;
        try {
            final JsonArray cleanProjs = new JsonArray();

            for (int i=0; i < response.size() ; ++i) {
                if (response.get(i).getAsJsonObject().get(getString(R.string.acti_type)).getAsString().equals(getString(R.string.proj)))
                    cleanProjs.add(response.get(i));
            }

            for(int i = 0; i < cleanProjs.size(); ++i) {
                projList.add(cleanProjs.get(i).getAsJsonObject().get(getString(R.string.activity_title)).getAsString());
            }
            projAdapter.notifyDataSetChanged();


            ListView projDisp = (ListView)findViewById(R.id.project_title);

            projDisp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        final JsonObject clicked = cleanProjs.get(position).getAsJsonObject();
                        final int registered = clicked.get(getString(R.string.registered)).getAsInt();

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

                        title.setText(clicked.get(getString(R.string.activity_title)).getAsString());

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
            });

        }
        catch (Exception e) {
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

            Toast.makeText(getApplicationContext(), "You registered", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(getApplicationContext(), "You unregistered", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
