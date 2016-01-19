package com.epitech.epidroid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


public class MainActivity extends AbstractActivity {

    private ImageRequest            imgHandler = new ImageRequest();
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

    public void requestCallback(JsonObject result) {

        if (result == null)
            finish();

        appContext.userInfos = result;

        JsonObject infos = result.getAsJsonObject(getString(R.string.domain_infos));
        String picture = infos.get(getString(R.string.picture)).getAsString();

        imgHandler.execute(this, picture);
        displayInfos();
    }


    public void imageCallback(Bitmap image) {
        ImageView img = (ImageView)findViewById(R.id.profileImg);
        img.setImageBitmap(image);
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
            .load("GET", getString(R.string.api_domain) + getString(R.string.domain_user))
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


    public void onSectionAttached(int number) {
        switch (number) {
            case 2:
                mTitle = getString(R.string.title_section1);
                break;
            case 3:
                mTitle = getString(R.string.title_section6);
                Intent intent = new Intent(getApplicationContext(), YearbookActivity.class);
                startActivity(intent);
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
}
