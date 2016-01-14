package com.epitech.epidroid;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private ImageRequest    imgHandler = new ImageRequest();
    private EpiContext      appContext;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList = new ArrayList<String>();
    private CharSequence mTitle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));





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

            String messages = "";
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

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_modules:
                Intent i = new Intent(getApplicationContext(), ModulesActivity.class);
                startActivity(i);
                break;

            case R.id.action_settings:
                if (appContext.token == null) {
                    Intent in = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(in);
                }
                else {
                    Intent in = new Intent(getApplicationContext(), DisconnectActivity.class);
                    startActivity(in);
                }
                break;

            case R.id.action_calendar:
                Intent inte = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(inte);
                break;

            case R.id.action_new:
                Intent inten = new Intent(getApplicationContext(), ProjectsActivity.class);
                startActivity(inten);
                break;

            default:
                break;
        }

        return true;
    }
    */

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, MainActivity.PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }



    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_projects, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
