package com.epitech.epidroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ModulesActivity extends AbstractActivity {

    private EpiContext              appContext = null;
    private ArrayAdapter<String>    mAdapter;
    private ArrayList<String>       mArrayList = new ArrayList<String>();
    private ArrayAdapter<String>    vAdapter;
    private ArrayList<String>       vArrayList = new ArrayList<String>();
    private CharSequence            mTitle;
    private int                     selectedSemester = 1;
    private ArrayList<String>       activitiesArrayList = new ArrayList<String>();
    private ArrayAdapter<String>    activitiesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules);

        appContext = (EpiContext)getApplication();
        vAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vArrayList);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mArrayList);
        activitiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activitiesArrayList);

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout));

        if (appContext.token == null) {
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
        }

        RequestAPI reqHandler = new RequestAPI();

        HashMap<String, String> netOpts = new HashMap<String, String>();
        netOpts.put(getString(R.string.domain), getString(R.string.domain_modules));
        netOpts.put(getString(R.string.request_method), getString(R.string.request_method_get));
        netOpts.put(getString(R.string.callback), getString(R.string.callback_modules));

        HashMap<String, String> args = new HashMap<String, String>();
        args.put(getString(R.string.token), appContext.token);

        reqHandler.execute(this, netOpts, args);
    }


    public void modulesCallback(JSONObject result) {

        if (result == null) {
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            JSONArray mods = result.getJSONArray(getString(R.string.domain_modules));
            Spinner msgs = (Spinner)findViewById(R.id.modules);
            ListView lst = (ListView)findViewById(R.id.modules_title);
            final ArrayList<JSONObject>[] modules = getDick(mods);

            msgs.setAdapter(vAdapter);
            lst.setAdapter(mAdapter);

            msgs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView parent, View view, int position, long id) {

                    Toast.makeText(getBaseContext(), "Semester " + ++position + " selected", Toast.LENGTH_SHORT).show();
                    selectedSemester = position;

                    if (position < modules.length && modules[position] != null) {
                        ArrayList<JSONObject> tmp = modules[position];

                        mArrayList.clear();
                        mAdapter.notifyDataSetChanged();

                        try {
                            for (int i = 0; i < tmp.size(); ++i) {
                                String modName = tmp.get(i).getString(getString(R.string.title));

                                mArrayList.add(modName);
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView view) {
                }
            });

            for (int i = 1; i < modules.length && modules[i] != null; ++i) {
                vArrayList.add("" + i);
                vAdapter.notifyDataSetChanged();
            }


            lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    /*ListView lv = (ListView)findViewById(R.id.modules_title);
                    String selectedFromList = (String)(lv.getItemAtPosition(position));*/

                    ArrayList<JSONObject> sem = modules[selectedSemester];
                    JSONObject selectedModule = sem.get(position);

                    try {
                        String scolarYear = selectedModule.getString(getString(R.string.scolarYear));
                        String codeModule = selectedModule.getString(getString(R.string.codeModule));
                        String codeInstance = selectedModule.getString(getString(R.string.codeInstance));

                        executeRequestModule(scolarYear, codeModule, codeInstance);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void executeRequestModule(String scolarYear, String codeModule, String codeInstance) {
        HashMap<String, String> netOpts = new HashMap<String, String>();
        netOpts.put(getString(R.string.request_method), getString(R.string.request_method_get));
        netOpts.put(getString(R.string.domain), getString(R.string.domain_module));
        netOpts.put(getString(R.string.callback), getString(R.string.callback_info));

        HashMap<String, String> args = new HashMap<String, String>();
        args.put(getString(R.string.token), appContext.token);
        args.put(getString(R.string.scolarYear), scolarYear);
        args.put(getString(R.string.codeModule), codeModule);
        args.put(getString(R.string.codeInstance), codeInstance);

        RequestAPI reqHandler = new RequestAPI();
        reqHandler.execute(this, netOpts, args);
    }


    public void requestCallback(JSONObject result) {
        if (result == null) {
            Toast.makeText(this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String title = result.getString(getString(R.string.title));


            LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.popup_module, null);
            final PopupWindow popupWindow = new PopupWindow(popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);


            TextView moduleName = (TextView)popupView.findViewById(R.id.module_name);
            moduleName.setText(title);


            ListView moduleInfo = (ListView)popupView.findViewById(R.id.module_infos);
            JSONArray activities = result.getJSONArray("activites");

            activitiesArrayList.clear();
            moduleInfo.setAdapter(activitiesAdapter);

            for (int i=0; i < activities.length(); ++i) {

                JSONObject tmp = activities.getJSONObject(i);
                Boolean isProject = tmp.getBoolean("is_projet");

                if (isProject) {

                    String infos = tmp.getString(getString(R.string.title)) + "          ";

                    if (!tmp.getString("note").equals("null"))
                        infos += tmp.getString("note");
                    activitiesArrayList.add(infos);
                    activitiesAdapter.notifyDataSetChanged();
                }
            }


            Button btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
            btnDismiss.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

            popupWindow.showAtLocation(popupView, Gravity.RIGHT, Gravity.CENTER, Gravity.CENTER);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private ArrayList<JSONObject>[] getDick(JSONArray mods) {
        if (mods == null)
            return null;

        ArrayList<JSONObject>[] res = new ArrayList[11];

        for (int i = 0; i < mods.length(); ++i) {

            try {
                JSONObject tmp = mods.getJSONObject(i);

                int sem = tmp.getInt(getString(R.string.semester));

                if (res[sem] == null) {
                    res[sem] = new ArrayList<JSONObject>();
                }
                res[sem].add(tmp);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return res;
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
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                break;
            case 3:
                mTitle = getString(R.string.title_section2);
                Intent inte = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(inte);
                break;
            case 4:
                mTitle = getString(R.string.title_section3);
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
