package com.epitech.epidroid;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


public class MainActivity extends AbstractActivity {

    private EpiContext              appContext;
    private ArrayAdapter<String>    adapter;
    private ArrayList<String>       arrayList = new ArrayList<String>();
    private CharSequence            mTitle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout));


        appContext = (EpiContext)getApplication();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        if (appContext.token == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        else {
            Ion.with(getApplicationContext())
                .load(getString(R.string.api_domain) + getString(R.string.domain_infos))
                .setBodyParameter(getString(R.string.token), appContext.token)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        requestCallback(result);
                    }
                });
        }
    }


    /**
     * Callback for the user informations request.
     * Gets the profile picture URL and tries to display it.
     * @param  result The result of the request in JSON format
     */
    public void requestCallback(JsonObject result) {

        if (result == null
                || !result.has(getString(R.string.domain_infos))
                || result.get(getString(R.string.domain_infos)).isJsonNull()) {
            Toast.makeText(getApplicationContext(), getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        appContext.userInfos = result;
        try {
            JsonObject infos = result.getAsJsonObject(getString(R.string.domain_infos));
            String picture = infos.get(getString(R.string.picture)).getAsString();
            String url = getString(R.string.api_photos) + picture;
            ImageView img = (ImageView) findViewById(R.id.profileImg);

            Ion.with(img).load(url);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        displayInfos();
    }


    private void displayInfos() {
        JsonObject infos = appContext.userInfos;

        if (infos == null)
            return;

        try {
            JsonObject inf = infos.getAsJsonObject(getString(R.string.domain_infos));
            String login = inf.get(getString(R.string.domain_login)).getAsString();
            String title = inf.get(getString(R.string.title)).getAsString();
            JsonArray history = infos.getAsJsonArray(getString(R.string.history));

            TextView ttl = (TextView)findViewById(R.id.title);
            ttl.setText(title);

            ListView msgs = (ListView)findViewById(R.id.messages);
            msgs.setAdapter(adapter);

            for (int i=0; i < history.size(); ++i) {
                JsonObject tmp = history.get(i).getAsJsonObject();

                arrayList.add(Html.fromHtml(tmp.get(getString(R.string.title)).getAsString()).toString());
            }
            adapter.notifyDataSetChanged();

            Ion.with(getApplicationContext())
            .load(getString(R.string.request_method_get), getString(R.string.api_domain) + getString(R.string.domain_user))
                    .setBodyParameter(getString(R.string.token), appContext.token)
                    .setBodyParameter(getString(R.string.domain_user), login)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    userCallback(result);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Callback for the user informations request.
     * Displays the user's credits, gpa for the bachelor cycle and active log time.
     * @param  result The result of the request in JSON format
     */
    public void userCallback(JsonObject result) {
        if (result == null)
            return;

        TextView msgsN = (TextView)findViewById(R.id.logTime);
        try {
            Integer credits = result.get(getString(R.string.credits)).getAsInt();
            JsonArray gpas = result.getAsJsonArray(getString(R.string.gpa));
            String gpaBachelor = gpas.get(0).getAsJsonObject().get(getString(R.string.gpa)).getAsString();

            TextView cred = (TextView)findViewById(R.id.credits);
            TextView gpa = (TextView)findViewById(R.id.gpa);

            cred.setText("Credits: " + credits.toString());
            gpa.setText("GPA Bachelor: " + gpaBachelor);

            JsonObject ns = result.get(getString(R.string.ns_stat)).getAsJsonObject();
            String timeActive = ns.get(getString(R.string.ns_stat_active)).getAsString();

            msgsN.setText("Netsoul: " + timeActive);
        }
        catch (Exception e) {
            msgsN.setText("Netsoul: 0");
        }
    }
}
