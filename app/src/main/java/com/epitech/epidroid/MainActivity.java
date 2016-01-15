package com.epitech.epidroid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AbstractActivity {

    private ImageRequest    imgHandler = new ImageRequest();
    private EpiContext      appContext;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList = new ArrayList<String>();
    private CharSequence mTitle;


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
            RequestAPI reqHandler = new RequestAPI();
            HashMap<String, String> netOptions = new HashMap<String, String>();
            netOptions.put(getString(R.string.request_method), getString(R.string.request_method_post));
            netOptions.put(getString(R.string.domain), getString(R.string.domain_infos));
            netOptions.put(getString(R.string.callback), getString(R.string.callback_info));

            HashMap<String, String> args = new HashMap<String, String>();
            args.put(getString(R.string.token), appContext.token);

            reqHandler.execute(this, netOptions, args);
        }
    }


    public void requestCallback(JSONObject result) {

        if (result != null)
            appContext.userInfos = result;

        try {
            JSONObject infos = result.getJSONObject(getString(R.string.domain_infos));
            String picture = infos.getString(getString(R.string.picture));

            imgHandler.execute(this, picture);
            displayInfos();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void imageCallback(Bitmap image) {
        ImageView img = (ImageView)findViewById(R.id.profileImg);
        img.setImageBitmap(image);
    }


    private void displayInfos() {
        JSONObject infos = appContext.userInfos;

        if (infos == null)
            return;

        try {
            JSONObject inf = infos.getJSONObject(getString(R.string.domain_infos));
            String login = inf.getString(getString(R.string.domain_login));
            String title = inf.getString(getString(R.string.title));

            TextView ttl = (TextView)findViewById(R.id.title);
            ttl.setText(title);

            JSONArray history = infos.getJSONArray(getString(R.string.history));
            ListView msgs = (ListView)findViewById(R.id.messages);
            msgs.setAdapter(adapter);

            for (int i=0; i < history.length(); ++i) {
                arrayList.add(Html.fromHtml(history.getJSONObject(i).getString(getString(R.string.title))).toString());
                adapter.notifyDataSetChanged();
            }

            RequestAPI reqHandler = new RequestAPI();
            HashMap<String, String> netOpts = new HashMap<String, String>();
            netOpts.put(getString(R.string.request_method), getString(R.string.request_method_get));
            netOpts.put(getString(R.string.domain), getString(R.string.domain_user));
            netOpts.put(getString(R.string.callback), getString(R.string.callback_user));

            HashMap<String, String> args = new HashMap<String, String>();
            args.put(getString(R.string.token), appContext.token);
            args.put(getString(R.string.domain_user), login);

            reqHandler.execute(this, netOpts, args);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void userCallback(JSONObject result) {
        if (result == null)
            return;

        TextView msgsN = (TextView)findViewById(R.id.logTime);
        try {

            Integer credits = result.getInt(getString(R.string.credits));
            JSONArray gpas = result.getJSONArray(getString(R.string.gpa));
            String gpaBachelor = gpas.getJSONObject(0).getString(getString(R.string.gpa));

            TextView cred = (TextView)findViewById(R.id.credits);
            TextView gpa = (TextView)findViewById(R.id.gpa);

            cred.setText("Credits: " + credits.toString());
            gpa.setText("GPA Bachelor: " + gpaBachelor);

            JSONObject ns = result.getJSONObject(getString(R.string.ns_stat));
            String timeActive = ns.getString(getString(R.string.ns_stat_active));
            msgsN.setText("Netsoul: " + timeActive);
        }
        catch (Exception e) {
            msgsN.setText("Netsoul: 0");
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 2:
                mTitle = getString(R.string.title_section1);
                break;
            case 3:
                mTitle = getString(R.string.title_section2);
                Intent inte = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(inte);
                break;
            case 4:
                mTitle = getString(R.string.title_section3);
                Intent i = new Intent(getApplicationContext(), ModulesActivity.class);
                startActivity(i);
                break;
            case 5:
                mTitle = getString(R.string.title_section4);
                Intent inten = new Intent(getApplicationContext(), ProjectsActivity.class);
                startActivity(inten);
                break;
            case 6:
                mTitle = getString(R.string.title_section5);
                Intent in = new Intent(getApplicationContext(), DisconnectActivity.class);
                startActivity(in);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


}
